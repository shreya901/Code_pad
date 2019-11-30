drop database codedb;
create database codedb;
USE codedb;
CREATE TABLE Code
(
    id              INTEGER PRIMARY KEY AUTO_INCREMENT,
    codetext        VARCHAR(10000),
    executionresult VARCHAR(20000)
);