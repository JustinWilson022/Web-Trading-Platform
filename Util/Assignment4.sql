DROP DATABASE IF EXISTS Assignment4;
CREATE DATABASE Assignment4;

USE Assignment4;

CREATE TABLE Assignment4.Users(
	ID INT NOT NULL AUTO_INCREMENT,
	Email varchar(45),
    Username varchar(45),
    Password varchar(45),
    Balance double,
    PRIMARY KEY(ID)
); 

CREATE TABLE Favorites(
	Username varchar(45),
    Ticker varchar(45)
);

CREATE TABLE Portfolio(
	ID INT NOT NULL AUTO_INCREMENT,
	Username varchar(45),
    Ticker varchar(45),
    Price double, 
    Quantity int,
    PRIMARY KEY(ID)
);
