create database manager;
create table user(
  id int primary key auto_increment,
  username varchar(64),
  password varchar(128)
);