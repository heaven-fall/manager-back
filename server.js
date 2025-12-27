const express = require('express');
const mysql = require('mysql2/promise');
const multer = require('multer');
const path = require('path');
const cors = require('cors');
const fs = require('fs');
const mammoth = require('mammoth');

const app = express();
const port = 3000;

// 中间件
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 静态文件服务
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// 数据库连接配置
const dbConfig = {
  host: 'localhost',
  user: 'root',
  password: '123456',
  database: 'manager',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
};

// 创建数据库连接池
const pool = mysql.createPool(dbConfig);

// 文件上传配置
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    const uploadDir = path.join(__dirname, 'uploads/templates');
    if (!fs.existsSync(uploadDir)) {
      fs.mkdirSync(uploadDir, { recursive: true });
    }
    cb(null, uploadDir);
  },
  filename: function (req, file, cb) {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, uniqueSuffix + path.extname(file.originalname));
  }
});

const upload = multer({
  storage: storage,
  limits: { fileSize: 10 * 1024 * 1024 }, // 10MB限制
  fileFilter: (req, file, cb) => {
    const allowedTypes = ['.doc', '.docx'];
    const extname = path.extname(file.originalname).toLowerCase();
    if (allowedTypes.includes(extname)) {
      cb(null, true);
    } else {
      cb(new Error('只支持上传Word文档 (.doc/.docx)'));
    }
  }
});

// 辅助函数：提取占位符
function extractPlaceholders(text) {
  const placeholders = [];

  // 匹配 {{placeholder}} 格式
  const regex1 = /\{\{([^}]+)\}\}/g;
  let match;
  while ((match = regex1.exec(text)) !== null) {
    placeholders.push(`{{${match[1]}}}`);
  }

  // 匹配 ${placeholder} 格式
  const regex2 = /\$\{([^}]+)\}/g;
  while ((match = regex2.exec(text)) !== null) {
    placeholders.push(`{{${match[1]}}}`); // 统一转换为双花括号格式
  }

  return [...new Set(placeholders)]; // 去重
}

// 验证token中间件
const authenticateToken = async (req, res, next) => {
  try {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
      return res.status(401).json({ code: 401, message: '未授权访问' });
    }

    // 这里应该验证token的有效性
    // 简化示例：假设token有效
    req.user = { id: 'admin', role: 'admin' };
    next();
  } catch (error) {
    return res.status(401).json({ code: 401, message: '认证失败' });
  }
};

// API路由

// 1. 获取模板列表
app.get('/api/templates', authenticateToken, async (req, res) => {
  try {
    const connection = await pool.getConnection();

    const [templates] = await connection.execute(`
      SELECT t.*,
             GROUP_CONCAT(DISTINCT pc.placeholder_key) as requiredPlaceholders
      FROM template t
      LEFT JOIN placeholder_config pc ON t.type = pc.template_type
      WHERE pc.is_required = 1
      GROUP BY t.id
      ORDER BY t.type
    `);

    const result = templates.map(template => ({
      id: template.id,
      type: template.type,
      name: template.name,
      hasTemplate: !!template.file_path && template.file_path !== '',
      fileSize: template.file_size,
      updatedAt: template.updated_at ? new Date(template.updated_at).toLocaleString('zh-CN') : null,
      updatedBy: template.updated_by,
      fileName: template.file_name,
      requiredPlaceholders: template.requiredPlaceholders ?
        template.requiredPlaceholders.split(',') : []
    }));

    connection.release();
    res.json({ code: 200, data: result });
  } catch (error) {
    console.error('获取模板列表失败:', error);
    res.status(500).json({ code: 500, message: '获取模板列表失败' });
  }
});

// 2. 上传模板
app.post('/api/templates/upload', authenticateToken, upload.single('file'), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ code: 400, message: '请选择文件' });
    }

    const { templateId } = req.body;
    const userId = req.user.id;
    const file = req.file;

    // 获取必需占位符
    const connection = await pool.getConnection();

    // 先获取模板类型
    const [templateRows] = await connection.execute(
      'SELECT type FROM template WHERE id = ?',
      [templateId]
    );

    if (templateRows.length === 0) {
      connection.release();
      return res.status(404).json({ code: 404, message: '模板不存在' });
    }

    const templateType = templateRows[0].type;

    // 获取该类型模板的必需占位符
    const [placeholderRows] = await connection.execute(
      'SELECT placeholder_key FROM placeholder_config WHERE template_type = ? AND is_required = 1',
      [templateType]
    );

    const requiredPlaceholders = placeholderRows.map(row => row.placeholder_key);

    // 读取Word文件内容并验证占位符
    const fileBuffer = fs.readFileSync(file.path);
    const result = await mammoth.extractRawText({ buffer: fileBuffer });
    const textContent = result.value;
    const foundPlaceholders = extractPlaceholders(textContent);

    // 检查是否包含所有必需占位符
    const missingPlaceholders = requiredPlaceholders.filter(
      placeholder => !foundPlaceholders.includes(placeholder)
    );

    if (missingPlaceholders.length > 0) {
      // 删除上传的文件
      fs.unlinkSync(file.path);
      connection.release();
      return res.status(400).json({
        code: 400,
        message: '模板缺少必需占位符',
        data: { missingPlaceholders }
      });
    }

    // 更新数据库
    await connection.execute(`
      UPDATE template
      SET file_path = ?, file_name = ?, file_size = ?, updated_by = ?, updated_at = NOW()
      WHERE id = ?
    `, [file.path, file.originalname, file.size, userId, templateId]);

    connection.release();

    res.json({
      code: 200,
      message: '上传成功',
      data: {
        fileSize: formatFileSize(file.size),
        updatedAt: new Date().toLocaleString('zh-CN'),
        updatedBy: userId
      }
    });
  } catch (error) {
    console.error('上传失败:', error);
    res.status(500).json({ code: 500, message: '上传失败: ' + error.message });
  }
});

// 3. 下载模板
app.get('/api/templates/download/:id', authenticateToken, async (req, res) => {
  try {
    const connection = await pool.getConnection();
    const [rows] = await connection.execute(
      'SELECT file_path, file_name FROM template WHERE id = ?',
      [req.params.id]
    );

    connection.release();

    if (rows.length === 0 || !rows[0].file_path || rows[0].file_path === '') {
      return res.status(404).json({ code: 404, message: '模板文件不存在' });
    }

    const filePath = rows[0].file_path;
    const fileName = rows[0].file_name;

    if (!fs.existsSync(filePath)) {
      return res.status(404).json({ code: 404, message: '文件不存在' });
    }

    res.download(filePath, fileName);
  } catch (error) {
    console.error('下载失败:', error);
    res.status(500).json({ code: 500, message: '下载失败' });
  }
});

// 4. 删除模板
app.delete('/api/templates/:id', authenticateToken, async (req, res) => {
  try {
    const connection = await pool.getConnection();

    // 先获取文件路径
    const [rows] = await connection.execute(
      'SELECT file_path FROM template WHERE id = ?',
      [req.params.id]
    );

    if (rows.length === 0) {
      connection.release();
      return res.status(404).json({ code: 404, message: '模板不存在' });
    }

    const filePath = rows[0].file_path;

    // 如果存在物理文件，删除它
    if (filePath && filePath !== '' && fs.existsSync(filePath)) {
      fs.unlinkSync(filePath);
    }

    // 更新数据库记录（清空文件信息）
    await connection.execute(`
      UPDATE template
      SET file_path = '', file_name = '', file_size = NULL, updated_by = NULL, updated_at = NOW()
      WHERE id = ?
    `, [req.params.id]);

    connection.release();
    res.json({ code: 200, message: '删除成功' });
  } catch (error) {
    console.error('删除失败:', error);
    res.status(500).json({ code: 500, message: '删除失败' });
  }
});

// 5. 获取日期配置
app.get('/api/date-config', authenticateToken, async (req, res) => {
  try {
    const connection = await pool.getConnection();
    const [rows] = await connection.execute(
      'SELECT config_key, config_value FROM date_config'
    );
    connection.release();
    res.json({ code: 200, data: rows });
  } catch (error) {
    console.error('获取日期配置失败:', error);
    res.status(500).json({ code: 500, message: '获取日期配置失败' });
  }
});

// 6. 保存日期配置
app.post('/api/date-config/save', authenticateToken, async (req, res) => {
  try {
    const { defenseDate, evaluationDate } = req.body;
    const connection = await pool.getConnection();

    await connection.execute(`
      INSERT INTO date_config (config_key, config_value)
      VALUES ('defense_date', ?), ('evaluation_date', ?)
      ON DUPLICATE KEY UPDATE config_value = VALUES(config_value)
    `, [defenseDate, evaluationDate]);

    connection.release();
    res.json({ code: 200, message: '保存成功' });
  } catch (error) {
    console.error('保存日期配置失败:', error);
    res.status(500).json({ code: 500, message: '保存失败' });
  }
});

// 7. 应用日期到模板
app.post('/api/templates/apply-dates', authenticateToken, async (req, res) => {
  try {
    const { defenseDate, evaluationDate } = req.body;

    // 这里可以添加具体的业务逻辑
    // 例如：更新相关模板中的日期信息
    // 简化实现：记录日志
    console.log('应用日期设置到模板:', { defenseDate, evaluationDate });

    res.json({ code: 200, message: '应用成功' });
  } catch (error) {
    console.error('应用日期失败:', error);
    res.status(500).json({ code: 500, message: '应用失败' });
  }
});

// 格式化文件大小
function formatFileSize(bytes) {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// 启动服务器
app.listen(port, () => {
  console.log(`模板管理API服务运行在 http://localhost:${port}`);
  console.log(`上传文件存储目录: ${path.join(__dirname, 'uploads/templates')}`);
});