CREATE DATABASE `quotes_0` /*!40100 DEFAULT CHARACTER SET latin1 */;

DROP TABLE IF EXISTS `quotes_0`.`properties`;
CREATE TABLE  `quotes_0`.`properties` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key1` varchar(100) DEFAULT NULL,
  `value1` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `quotes_0`.`quotes`;
CREATE TABLE  `quotes_0`.`quotes` (
  `SYMBOLID` int(10) NOT NULL,
  `QDate` int(10) NOT NULL,
  `QClose` double(10,2) NOT NULL,
  `QVol` int(20) unsigned NOT NULL,
  `QHigh` double(10,2) NOT NULL,
  `QLow` double(10,2) NOT NULL,
  `QOpen` double(10,2) NOT NULL,
  `ID` int(10) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Index_2` (`SYMBOLID`,`QDate`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 PACK_KEYS=1 ROW_FORMAT=FIXED;

DROP TABLE IF EXISTS `quotes_0`.`symbol`;
CREATE TABLE  `quotes_0`.`symbol` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `exchange` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `quotes_0`.`symbolgrp`;
CREATE TABLE  `quotes_0`.`symbolgrp` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `desc` varchar(45) NOT NULL,
  `addresss` varchar(45) NOT NULL,
  `exchange` varchar(45) NOT NULL,
  `gmtoffset` decimal(10,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;