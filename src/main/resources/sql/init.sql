use manager;

drop table if exists tea_group_rel;
drop table if exists dbinfo;
drop table if exists dbgroup;
drop table if exists tea_stu_rel;
drop table if exists user_inst_rel;
drop table if exists student;
drop table if exists institute;
drop table if exists user;
drop table if exists template;
drop table if exists date_config;
drop table if exists placeholder_config;

create table user(
                     id char(10) not null comment '用户id',
                     pwd varchar(30) not null comment '密码',
                     role int not null comment '角色标识',
    -- 0-2,admin,instAdmin,defenseLeader/teacher,
                     real_name varchar(20) not null comment '真实姓名',
                     phone char(11) null comment '联系电话',
                     email varchar(32) null comment '邮箱',
                     signaturePath varchar(100) null comment '签名路径',
                     primary key (id)
) comment='用户表';

create table institute(
     id int not null auto_increment comment '院系id',
     name varchar(20) not null comment '院系名称',
     user_id char(10) comment '管理员id',
     primary key (id),
     unique key uk_institute(name)
 ) comment='院系表';

create table user_inst_rel(
                              user_id char(10) comment '用户id',
                              inst_id int comment '院系id',
                              UNIQUE KEY uk_inst (inst_id),
                              foreign key uk_user_id(user_id) references user(id),
                              foreign key uk_inst_id(inst_id) references institute(id)
) comment='用户所属院系';

create table student(
                        id char(10) not null comment '学号',
                        real_name varchar(20) not null comment '姓名',
                        tel char(11) comment '电话号码',
                        email varchar(20) comment '邮箱',
                        institute_id int not null comment '所属院系id',
                        foreign key fk_institute_id(institute_id) references institute(id),
                        primary key (id)
) comment='学生基本信息';

create table tea_stu_rel(
                            tea_id char(10) comment '老师id',
                            stu_id char(10) comment '学生id',
                            year int comment '指导年份',
                            unique key(tea_id, stu_id),
                            foreign key fk_tea_id(tea_id) references user(id),
                            foreign key fk_stu_id(stu_id) references student(id)
) comment='老师指导学生信息';

create table dbgroup(
                        id int auto_increment comment '编号',
                        year int comment '答辩年份',
                        admin_id char(10) comment '答辩组长',
                        max_student_count int comment '最大学生数量',
                        adjustmentCoefficient double null comment '调节系数',
                        primary key (id),
                        foreign key fk_user_id(admin_id) references user(id)
) comment='答辩组';

create table dbinfo(
                       gid int comment '答辩组号',
                       stu_id char(10) comment '学生编号',
                       type int comment '毕业考核类型',
                       title varchar(128) comment '毕业考核题目',
                       time date comment '答辩日期',
                       summary varchar(255) comment '毕业考核摘要',
                       reviewer_id char(10) comment '评阅人id',
                       total_score int default 0 comment '总分',
                       comment text comment '答辩小组评语',
                       graded_by varchar(10) comment '评分人id',
                       teacher_scores json comment '其他教师评分(json格式存储)',
                       foreign key (reviewer_id) references user(id),
                       foreign key (gid) references dbgroup(id),
                       foreign key (stu_id) references student(id),
                       unique key uk_stu_gid (stu_id, gid)
) comment='学生答辩信息';

-- 添加教师与小组的关联表
create table tea_group_rel (
                               id int auto_increment primary key,
                               teacher_id char(10) not null comment '教师ID',
                               group_id int not null comment '小组ID',
                               is_defense_leader boolean default false comment '是否为组长',
                               foreign key (teacher_id) references user(id),
                               foreign key (group_id) references dbgroup(id),
                               unique key uk_teacher_group (teacher_id, group_id)
) comment='教师与答辩小组关联表';

create table group_defense(
                               group_id int not null comment '答辩组号',
                               stu_id char(10) not null comment '学生编号',
                               major_score int null comment '大组答辩成绩',
                               primary key (group_id, stu_id),
                               foreign key (group_id) references dbgroup(id),
                               foreign key (stu_id) references student(id)
) comment='大组答辩表';
-- 添加索引
create index idx_user_id on user(id);
create index idx_student_id on student(id);

insert into user (id, pwd, role, real_name) values
                                                ('admin', '123456', 0, 'jj1'),
                                                ('inst', '123456', 1, 'jj2'),
                                                ('123123', '123456', 2, 'jj3'),
                                                ('123456', '123456', 2, 'jj4');

insert into institute (name) values
    ('计算机与信息学院');

insert into student (id, real_name, tel, email, institute_id) values
                                                                  ('2023001', 'wxy', '13800138001', 'wxy@email.com',1),
                                                                  ('2023002', 'lwx', '13800138002', 'lwx@email.com',1),
                                                                  ('2023003', 'zzh', '13800138003', 'zzh@email.com',1);

insert into user (id, pwd, role, real_name, phone, email) values
                                                              ('100001', '123456', 2, '张老师', '13800138001', 'zhang@example.com'),
                                                              ('100002', '123456', 2, '李老师', '13800138002', 'li@example.com'),
                                                              ('100003', '123456', 2, '王老师', '13800138003', 'wang@example.com'),
                                                              ('100004', '123456', 2, '赵老师', '13800138004', 'zhao@example.com');

insert into tea_stu_rel (tea_id, stu_id, year) values
                                                   ('100001', '2023001', 2023),
                                                   ('100001', '2023002', 2023);

insert into user_inst_rel(user_id, inst_id) values
    ('inst',1);

insert into user_inst_rel (user_id, inst_id) values
                                                 ('100001', 1),
                                                 ('100002', 1),
                                                 ('100003', 1),
                                                 ('100004', 1);

-- 插入多个年份的小组数据
insert into dbgroup (admin_id, year) values
                                         ('100001', 2023),
                                         (null, 2023),  -- 第2组，没有指定组长
                                         ('100003', 2024),
                                         (null, 2024),  -- 第4组，没有指定组长
                                         (null, 2025);  -- 第5组，2025年的小组

insert into tea_group_rel (teacher_id, group_id, is_defense_leader) values
                                                                        ('100001', 1, true),   -- 张老师是第1组组长
                                                                        ('100002', 1, false),  -- 李老师是第1组成员
                                                                        ('100002', 2, false),  -- 李老师也是第2组成员
                                                                        ('100003', 3, true),   -- 王老师是第3组组长
                                                                        ('100004', 3, false);  -- 赵老师是第3组成员


-- 1. 模板表
create table template (
                          id int auto_increment primary key comment '模板id',
                          name varchar(100) not null comment '模板名称',
                          type int not null comment '模板类型：1-本科毕业设计答辩成绩表, 2-本科毕业设计成绩评定表, 3-本科毕业论文答辩成绩表, 4-本科毕业论文成绩评定表, 5-毕业论文(设计)答辩小组统分表, 6-毕业论文答辩成绩无评语过程表, 7-毕业设计答辩成绩无评语过程表',
                          file_path varchar(500) not null comment '文件存储路径',
                          file_name varchar(255) not null comment '原始文件名',
                          file_size bigint comment '文件大小',
                          updated_at timestamp default current_timestamp on update current_timestamp comment '更新时间',
                          updated_by varchar(50) comment '最后更新人',
                          unique key uk_template_type (type)
) comment='文档模板表';

-- 2. 日期配置表
create table date_config (
                             id int auto_increment primary key,
                             config_key varchar(50) not null comment '配置键：defense_date/evaluation_date',
                             config_value date not null comment '日期值',
                             updated_at timestamp default current_timestamp on update current_timestamp comment '更新时间',
                             unique key uk_config_key (config_key)
) comment='日期配置表';

-- 3. 占位符配置表（用于验证）
create table placeholder_config (
                                    id int auto_increment primary key,
                                    template_type int not null comment '模板类型',
                                    placeholder_key varchar(100) not null comment '占位符键名',
                                    placeholder_name varchar(100) not null comment '占位符显示名称',
                                    is_required boolean default true comment '是否必需',
                                    unique key uk_type_placeholder (template_type, placeholder_key)
) comment='模板占位符配置表';

-- 初始化模板类型
insert into template (name, type, file_path, file_name) values
                                                            ('本科毕业设计答辩成绩表', 1, '', ''),
                                                            ('本科毕业设计成绩评定表', 2, '', ''),
                                                            ('本科毕业论文答辩成绩表', 3, '', ''),
                                                            ('本科毕业论文成绩评定表', 4, '', ''),
                                                            ('毕业论文(设计)答辩小组统分表', 5, '', ''),
                                                            ('毕业论文答辩成绩无评语过程表', 6, '', ''),
                                                            ('毕业设计答辩成绩无评语过程表', 7, '', '');

-- 初始化日期配置
insert into date_config (config_key, config_value) values
                                                       ('defense_date', curdate()),
                                                       ('evaluation_date', curdate());

-- 初始化占位符配置（根据需求文档）
-- 类型1：本科毕业设计答辩成绩表
insert into placeholder_config (template_type, placeholder_key, placeholder_name) values
                                                                                      (1, '{{student_name}}', '学生姓名'),
                                                                                      (1, '{{student_id}}', '学号'),
                                                                                      (1, '{{date_year}}', '年份'),
                                                                                      (1, '{{date_month}}', '月份'),
                                                                                      (1, '{{date_day}}', '日期'),
                                                                                      (1, '{{thesis_title}}', '题目'),
                                                                                      (1, '{{design_quality_score1}}', '设计质量分1'),
                                                                                      (1, '{{design_quality_score2}}', '设计质量分2'),
                                                                                      (1, '{{design_quality_score3}}', '设计质量分3'),
                                                                                      (1, '{{defense_report_score}}', '答辩报告分'),
                                                                                      (1, '{{response_score1}}', '回答问题分1'),
                                                                                      (1, '{{response_score2}}', '回答问题分2'),
                                                                                      (1, '{{total_score}}', '总成绩'),
                                                                                      (1, '{{signature_judge}}', '评委签名');

-- 类型2：本科毕业设计成绩评定表
insert into placeholder_config (template_type, placeholder_key, placeholder_name) values
                                                                                      (2, '{{student_name}}', '学生姓名'),
                                                                                      (2, '{{student_id}}', '学号'),
                                                                                      (2, '{{date_year}}', '年份'),
                                                                                      (2, '{{date_month}}', '月份'),
                                                                                      (2, '{{date_day}}', '日期'),
                                                                                      (2, '{{thesis_title}}', '题目'),
                                                                                      (2, '{{advisor_score}}', '指导老师成绩'),
                                                                                      (2, '{{reviewer_score}}', '评阅人成绩'),
                                                                                      (2, '{{defense_score}}', '答辩成绩'),
                                                                                      (2, '{{advisor_calculated}}', '指导老师成绩×0.3'),
                                                                                      (2, '{{reviewer_calculated}}', '评阅人成绩×0.3'),
                                                                                      (2, '{{defense_calculated}}', '答辩成绩×0.4'),
                                                                                      (2, '{{final_score}}', '最终成绩'),
                                                                                      (2, '{{signature_department_head}}', '系主任签名');

-- 类型3：本科毕业论文答辩成绩表
insert into placeholder_config (template_type, placeholder_key, placeholder_name) values
                                                                                      (3, '{{student_name}}', '学生姓名'),
                                                                                      (3, '{{student_id}}', '学号'),
                                                                                      (3, '{{date_year}}', '年份'),
                                                                                      (3, '{{date_month}}', '月份'),
                                                                                      (3, '{{date_day}}', '日期'),
                                                                                      (3, '{{thesis_title}}', '题目'),
                                                                                      (3, '{{paper_quality_score}}', '论文质量分'),
                                                                                      (3, '{{defense_report_score}}', '答辩报告分'),
                                                                                      (3, '{{response_score}}', '回答问题分'),
                                                                                      (3, '{{total_score}}', '总成绩'),
                                                                                      (3, '{{defense_comment}}', '答辩评语'),
                                                                                      (3, '{{signature_group_leader}}', '组长签名');

-- 类型4：本科毕业论文成绩评定表（同类型2）
insert into placeholder_config (template_type, placeholder_key, placeholder_name) values
                                                                                      (4, '{{student_name}}', '学生姓名'),
                                                                                      (4, '{{student_id}}', '学号'),
                                                                                      (4, '{{date_year}}', '年份'),
                                                                                      (4, '{{date_month}}', '月份'),
                                                                                      (4, '{{date_day}}', '日期'),
                                                                                      (4, '{{thesis_title}}', '题目'),
                                                                                      (4, '{{advisor_score}}', '指导老师成绩'),
                                                                                      (4, '{{reviewer_score}}', '评阅人成绩'),
                                                                                      (4, '{{defense_score}}', '答辩成绩'),
                                                                                      (4, '{{advisor_calculated}}', '指导老师成绩×0.3'),
                                                                                      (4, '{{reviewer_calculated}}', '评阅人成绩×0.3'),
                                                                                      (4, '{{defense_calculated}}', '答辩成绩×0.4'),
                                                                                      (4, '{{final_score}}', '最终成绩'),
                                                                                      (4, '{{signature_department_head}}', '系主任签名');

-- 类型5：毕业论文(设计)答辩小组统分表（通用字段）
insert into placeholder_config (template_type, placeholder_key, placeholder_name) values
                                                                                      (5, '{{student_name}}', '学生姓名'),
                                                                                      (5, '{{student_id}}', '学号'),
                                                                                      (5, '{{date_year}}', '年份'),
                                                                                      (5, '{{date_month}}', '月份'),
                                                                                      (5, '{{date_day}}', '日期'),
                                                                                      (5, '{{thesis_title}}', '题目'),
                                                                                      (5, '{{total_score}}', '总成绩'),
                                                                                      (5, '{{signature_judge}}', '评委签名');

-- 类型6：毕业论文答辩成绩无评语过程表
insert into placeholder_config (template_type, placeholder_key, placeholder_name) values
                                                                                      (6, '{{student_name}}', '学生姓名'),
                                                                                      (6, '{{student_id}}', '学号'),
                                                                                      (6, '{{date_year}}', '年份'),
                                                                                      (6, '{{date_month}}', '月份'),
                                                                                      (6, '{{date_day}}', '日期'),
                                                                                      (6, '{{thesis_title}}', '题目'),
                                                                                      (6, '{{paper_quality_score}}', '论文质量分'),
                                                                                      (6, '{{defense_report_score}}', '答辩报告分'),
                                                                                      (6, '{{response_score}}', '回答问题分'),
                                                                                      (6, '{{total_score}}', '总成绩'),
                                                                                      (6, '{{signature_judge}}', '评委签名');

-- 类型7：毕业设计答辩成绩无评语过程表（同类型1）
insert into placeholder_config (template_type, placeholder_key, placeholder_name) values
                                                                                      (7, '{{student_name}}', '学生姓名'),
                                                                                      (7, '{{student_id}}', '学号'),
                                                                                      (7, '{{date_year}}', '年份'),
                                                                                      (7, '{{date_month}}', '月份'),
                                                                                      (7, '{{date_day}}', '日期'),
                                                                                      (7, '{{thesis_title}}', '题目'),
                                                                                      (7, '{{design_quality_score1}}', '设计质量分1'),
                                                                                      (7, '{{design_quality_score2}}', '设计质量分2'),
                                                                                      (7, '{{design_quality_score3}}', '设计质量分3'),
                                                                                      (7, '{{defense_report_score}}', '答辩报告分'),
                                                                                      (7, '{{response_score1}}', '回答问题分1'),
                                                                                      (7, '{{response_score2}}', '回答问题分2'),
                                                                                      (7, '{{total_score}}', '总成绩'),
                                                                                      (7, '{{signature_judge}}', '评委签名');
-- 学生2023001（wxy）的毕业设计答辩信息
insert into dbinfo (gid, stu_id, type, title, time, summary, reviewer_id) values
    (1, '2023001', 1, '基于springboot的在线教育平台设计与实现', '2023-05-20',
     '本毕业设计基于springboot框架开发了一个在线教育平台，实现了课程管理、在线学习、考试系统等功能。', '100002');

-- 学生2023002（lwx）的毕业论文答辩信息
insert into dbinfo (gid, stu_id, type, title, time, summary, reviewer_id) values
    (1, '2023002', 2, '人工智能在智能客服系统中的关键技术研究与应用', '2023-05-20',
     '本毕业论文研究了人工智能技术在智能客服系统中的应用，重点探讨了自然语言处理和机器学习算法。', '100002');

-- 对于毕业设计类型（type=1）
UPDATE dbinfo
SET teacher_scores = '[
  {
    "teacher_id": "100002",
    "teacher_name": "李老师",
    "design_quality1": 13,
    "design_quality2": 13,
    "design_quality3": 13,
    "design_presentation": 22,
    "design_qa1": 13,
    "design_qa2": 13,
    "total_score": 86
  },
  {
    "teacher_id": "100003",
    "teacher_name": "王老师",
    "design_quality1": 14,
    "design_quality2": 14,
    "design_quality3": 13,
    "design_presentation": 22,
    "design_qa1": 13,
    "design_qa2": 13,
    "total_score": 89
  },
  {
    "teacher_id": "100004",
    "teacher_name": "赵老师",
    "design_quality1": 13,
    "design_quality2": 13,
    "design_quality3": 12,
    "design_presentation": 22,
    "design_qa1": 12,
    "design_qa2": 12,
    "total_score": 84
  }
]',
    total_score = 86,
    graded_by = '100001'
WHERE stu_id = '2023001' AND gid = 1;

-- 对于毕业论文类型（type=2）
UPDATE dbinfo
SET teacher_scores = '[
  {
    "teacher_id": "100002",
    "teacher_name": "李老师",
    "paper_quality": 45,
    "presentation": 25,
    "qa_performance": 30,
    "total_score": 100
  },
  {
    "teacher_id": "100003",
    "teacher_name": "王老师",
    "paper_quality": 43,
    "presentation": 28,
    "qa_performance": 29,
    "total_score": 100
  },
  {
    "teacher_id": "100004",
    "teacher_name": "赵老师",
    "paper_quality": 40,
    "presentation": 25,
    "qa_performance": 28,
    "total_score": 93
  }
]',
    total_score = 98,
    graded_by = '100001'
WHERE stu_id = '2023002' AND gid = 1;