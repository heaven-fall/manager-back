drop table if exists `defense_leader`;
drop table if exists `institute_admin`;
drop table if exists `teacher`;
drop table if exists `institute`;
drop table if exists `user`;

-- 创建系统用户表
create table `user` (
                        `user_id` int not null auto_increment comment '用户id',
                        `username` varchar(30) not null comment '用户名',
                        `pwd` varchar(30) not null comment '密码',
                        `role` varchar(20) not null comment '角色标识',
                        `real_name` varchar(10) not null comment '真实姓名',
                        primary key (`user_id`),
                        unique key `uk_username_role` (`username`, `role`)
) engine=innodb default charset=utf8mb4 comment='系统用户表';

-- 院系表
create table `institute` (
                             `institute_id` int not null auto_increment comment '院系id',
                             `institute_name` varchar(20) not null comment '院系名称',
                             primary key (`institute_id`),
                             unique key `uk_institute_name` (`institute_name`)
) engine=innodb default charset=utf8mb4 comment='院系表';

-- 教师信息扩展表
create table `teacher` (
                           `teacher_id` int not null comment '教师用户id',
                           `institute_id` int not null comment '所属院系id',
                           primary key (`teacher_id`),
                           foreign key (`teacher_id`) references `user`(`user_id`) on delete cascade on update cascade,
                           foreign key (`institute_id`) references `institute`(`institute_id`) on delete restrict on update cascade,
                           key `idx_institute` (`institute_id`)
) engine=innodb default charset=utf8mb4 comment='教师信息表';

-- 院系管理员扩展表
create table `institute_admin` (
                                   `admin_id` int not null comment '院系管理员用户id',
                                   `institute_id` int not null comment '管理的院系id',
                                   primary key (`admin_id`),
                                   foreign key (`admin_id`) references `user`(`user_id`) on delete cascade on update cascade,
                                   foreign key (`institute_id`) references `institute`(`institute_id`) on delete cascade on update cascade,
                                   key `idx_institute` (`institute_id`)
) engine=innodb default charset=utf8mb4 comment='院系管理员关联表';

-- 答辩组长表
create table `defense_leader` (
                                  `teacher_id` int not null comment '教师id',
                                  `granted_year` varchar(10) not null comment '答辩组长授予的年份',
                                  primary key (`teacher_id`),
                                  foreign key (`teacher_id`) references `teacher`(`teacher_id`) on delete cascade on update cascade,
                                  key `idx_granted_year` (`granted_year`)
) engine=innodb default charset=utf8mb4 comment='答辩组长表';

-- 添加索引
create index idx_user_role on user(role);
create index idx_username_password on user(username, pwd);
create index idx_real_name on user(real_name)