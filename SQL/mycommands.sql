## run this file in mysql to create database

create database if not exists clicker;
 
use clicker;

drop table if exists responses;

create table responses(
	questionNo int, 
	choice varchar(1),
);
