LOAD DATA LOCAL INFILE 'C:/SWE/eclipse-workspace-git/swa03/shop/src/test/resources/mysql/hibernate_sequence.csv'
INTO TABLE hibernate_sequence
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/SWE/eclipse-workspace-git/swa03/shop/src/test/resources/mysql/abteilung.csv'
INTO TABLE abteilung
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/SWE/eclipse-workspace-git/swa03/shop/src/test/resources/mysql/kategorie.csv'
INTO TABLE kategorie
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/SWE/eclipse-workspace-git/swa03/shop/src/test/resources/mysql/saison.csv'
INTO TABLE saison
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/SWE/eclipse-workspace-git/swa03/shop/src/test/resources/mysql/artikel.csv'
INTO TABLE artikel
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/SWE/eclipse-workspace-git/swa03/shop/src/test/resources/mysql/kunde.csv'
INTO TABLE kunde
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/SWE/eclipse-workspace-git/swa03/shop/src/test/resources/mysql/bestellung.csv'
INTO TABLE bestellung
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;

LOAD DATA LOCAL INFILE 'C:/SWE/eclipse-workspace-git/swa03/shop/src/test/resources/mysql/posten.csv'
INTO TABLE posten
FIELDS TERMINATED BY ';'
OPTIONALLY ENCLOSED BY '"'
IGNORE 1 LINES;





