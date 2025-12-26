use manager;

drop table if exists tea_stu_rel;
drop table if exists user_inst_rel;
drop table if exists tea_group_rel;
drop table if exists dbinfo;
drop table if exists dbgroup;
drop table if exists student;
drop table if exists institute;
drop table if exists user;

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
                          user_id char(10) comment '管理员用户id',
                          primary key (id),
                          unique key uk_institute(name),
                          foreign key fk_user_id(user_id) references user(id)
) comment='院系表';

create table user_inst_rel(
                              user_id char(10) comment '用户id',
                              inst_id int comment '院系id',
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
                        student_count int comment '学生数量',
                        max_student_count int comment '最大学生数量',
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
                       foreign key fk_rev_id(reviewer_id) references user(id),
                       foreign key fk_gid(gid) references dbgroup(id),
                       foreign key fk_stu_id(stu_id) references student(id)
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
-- 添加索引
create index idx_user_id on user(id);
create index idx_student_id on student(id);

insert into user (id, pwd, role, real_name) values
                                                ('admin', '123456', 0, 'jj1'),
                                                ('inst', '123456', 1, 'jj2'),
                                                ('123123', '123456', 2, 'jj3'),
                                                ('123456', '123456', 2, 'jj4');

insert into institute (name, user_id) values
    ('计算机与信息学院', 'inst');

insert into student (id, real_name, tel, email, institute_id) values
                                                                  ('2023001', 'wxy', '13800138001', 'wxy@email.com',1),
                                                                  ('2023002', 'lwx', '13800138002', 'lwx@email.com',1),
                                                                  ('2023003', 'zzh', '13800138003', 'zzh@email.com',1);

insert into tea_stu_rel (tea_id, stu_id, year) values
    ('123123', '2023003', 2025);

insert into user_inst_rel(user_id, inst_id) values
    ('inst',1);

insert into user (id, pwd, role, real_name, phone, email) values
                                                              ('100001', '123456', 2, '张老师', '13800138001', 'zhang@example.com'),
                                                              ('100002', '123456', 2, '李老师', '13800138002', 'li@example.com'),
                                                              ('100003', '123456', 2, '王老师', '13800138003', 'wang@example.com'),
                                                              ('100004', '123456', 2, '赵老师', '13800138004', 'zhao@example.com');

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
