/* 删除repository数据库 */
drop database repository;

/* 创建repository数据库和相应的表 */
create database repository;
use repository;

create table jobs
(
   id bigint,
   start_time datetime not null,
   end_time datetime not null,
   description varchar(200),
   primary key(id)
);

create table businessentity_0(id bigint not null auto_increment,name varchar(100) not null,url varchar(200),description varchar(200),domain varchar(200),primary key(id));
create table services_0(id bigint not null auto_increment,name varchar(100) not null,description varchar(200),url varchar(200) not null,addition_info text,wsdl_url varchar(100) not null,wsdl_path varchar(100) not null,businessId bigint not null,primary key(id),foreign key(businessId) references BusinessEntity_0(id) on delete cascade);