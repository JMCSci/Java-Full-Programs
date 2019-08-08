/* SQL Script */

/* create new database -- Bank */
create database Bank;

/* create new table -- Accounts */
create table Accounts (id int(5), firstName char(50), lastName char(50), chkBalance double (10,2), savBalance double(10,2));

/* create new table -- PIN */
 create table PIN (id int(5), pin int(4));

 /* create new table -- idCount */
 create table idCount(id_count int(5) null);	

/* create new table -- Transaction */
create table Transactions(id int(5), type char(1), description char(255));
