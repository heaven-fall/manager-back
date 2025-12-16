use manager;

DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
                         `admin_id` INT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
                         `admin_name` VARCHAR(100) NOT NULL COMMENT '管理员姓名',
                         `pwd` VARCHAR(255) NOT NULL COMMENT '密码',
                         `role` VARCHAR(50) NOT NULL DEFAULT 'admin' COMMENT '角色标识',
                         PRIMARY KEY (`admin_id`),
                         UNIQUE KEY `uk_admin_name` (`admin_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='超级管理员表';
INSERT INTO `admin` (admin_name, pwd) VALUES ('admin', '123456');
-- 创建teacher表
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
                           `teacher_id` INT NOT NULL AUTO_INCREMENT COMMENT '教师ID',
                           `teacher_name` VARCHAR(100) NOT NULL COMMENT '教师姓名',
                           `institute` VARCHAR(200) NOT NULL COMMENT '所属院系',
                           `pwd` VARCHAR(255) NOT NULL COMMENT '密码',
                           `role` VARCHAR(50) NOT NULL DEFAULT 'teacher' COMMENT '角色标识（teacher、defenseLeader）',
                           PRIMARY KEY (`teacher_id`),
                           UNIQUE KEY `uk_teacher_name` (`teacher_name`),
                           KEY `idx_institute` (`institute`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 创建inst_admin表
DROP TABLE IF EXISTS `inst_admin`;
CREATE TABLE `inst_admin` (
                              `admin_id` INT NOT NULL AUTO_INCREMENT COMMENT '院系管理员ID',
                              `admin_name` VARCHAR(100) NOT NULL COMMENT '管理员姓名',
                              `institute` VARCHAR(200) NOT NULL COMMENT '院系名',
                              `pwd` VARCHAR(255) NOT NULL COMMENT '密码',
                              `role` VARCHAR(50) NOT NULL DEFAULT 'instAdmin' COMMENT '角色标识',
                              PRIMARY KEY (`admin_id`),
                              UNIQUE KEY `uk_inst_admin_name` (`admin_name`),
                              KEY `idx_institute` (`institute`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院系管理员表';

-- 创建defense_leader表
DROP TABLE IF EXISTS `defense_leader`;
CREATE TABLE `defense_leader` (
                                  `teacher_id` INT NOT NULL COMMENT '教师ID',
                                  `granted_year` VARCHAR(10) NOT NULL COMMENT '答辩组长授予的年份',
                                  PRIMARY KEY (`teacher_id`),
                                  CONSTRAINT `fk_defense_leader_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`teacher_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                  KEY `idx_granted_year` (`granted_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答辩组长表';

-- 添加索引
CREATE INDEX idx_admin_name ON admin(admin_name);
CREATE INDEX idx_teacher_name_institute ON teacher(teacher_name, institute);
CREATE INDEX idx_inst_admin_institute ON inst_admin(institute, admin_name);
