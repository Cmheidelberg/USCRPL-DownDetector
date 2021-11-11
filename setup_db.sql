CREATE SCHEMA down_detector;
CREATE DATABASE down_detector;
USE down_detector;

CREATE TABLE users (
  `idusers` INT UNSIGNED NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idusers`),
  UNIQUE INDEX `idusers_UNIQUE` (`idusers` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC));

CREATE TABLE uptime (
  `ping_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `service_id` INT UNSIGNED NOT NULL,
  `ping_date` DATETIME NOT NULL,
  `response_code` INT UNSIGNED NULL,
  PRIMARY KEY (`idpurchase`),
  UNIQUE INDEX `pingid_UNIQUE` (`pingid` ASC)
  );

CREATE TABLE service (
  `service_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `service_name` VARCHAR(90) NULL,
  `service_url` VARCHAR(90) NULL,
  PRIMARY KEY (`idfavorite`)
  );

