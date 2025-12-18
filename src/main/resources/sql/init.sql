
drop table if exists user;
drop table if exists institute;
drop table if exists user_inst_rel;
drop table if exists student;
drop table if exists tea_stu_rel;
drop table if exists dbgroup;
drop table if exists dbinfo;

create table user(
  id char(10) not null comment '用户id',
  pwd varchar(30) not null comment '密码',
  role int not null comment '角色标识',
  real_name varchar(20) not null comment '真实姓名',
  primary key (id),
  unique key uk_username_role (id)
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
  user_id char(10) comment '组长id',
  year int comment '答辩年份',
  primary key (id),
  foreign key fk_user_id(user_id) references user(id)
) comment='答辩组';

create table dbinfo(
  gid int comment '答辩组号',
  stu_id char(10) comment '学生编号',
  type int comment '毕业考核类型',
  title varchar(128) comment '毕业考核题目',
  summary text comment '摘要',
  time date comment '答辩日期',
  foreign key fk_gid(gid) references dbgroup(id),
  foreign key fk_stu_id(stu_id) references student(id)
) comment='学生所属答辩组';

-- 添加索引
create index idx_user_id on user(id);
create index idx_student_id on student(id);