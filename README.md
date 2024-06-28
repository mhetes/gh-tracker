# gh-tracker
Home assignment project for Go Health

## MySQL Preparation

Commands:
```
docker pull mysql/mysql-server
docker run -p 3306:3306 --name=mysql1 -d mysql/mysql-server:latest
docker exec -it mysql1 mysql -uroot -p
```

SQL Statements:
```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'QwEAsD123.369';

CREATE DATABASE tracker;
CREATE USER 'tracker'@'%' IDENTIFIED BY 'QwEAsD123.369';
GRANT ALL PRIVILEGES ON tracker.* TO 'tracker'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;

USE tracker;

CREATE TABLE Bugs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description NVARCHAR(255) NOT NULL,
    status NVARCHAR(10) NOT NULL,
    timestamp BIGINT DEFAULT 0,
    link NVARCHAR(255) DEFAULT '',
    parent BIGINT DEFAULT 0
);

CREATE INDEX Idx_Bugs_Status ON Bugs (status);
```