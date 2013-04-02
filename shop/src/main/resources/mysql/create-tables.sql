create table hibernate_sequence(
next_val bigint primary key);


CREATE TABLE Abteilung(
id BIGINT PRIMARY KEY,
bezeichnung NVARCHAR(50));


CREATE TABLE Kategorie(
id bigint PRIMARY KEY,
bezeichnung nvarchar(50));


CREATE TABLE Saison(
id bigint PRIMARY KEY,
bezeichnung nvarchar(50));


CREATE TABLE Bestellung(
id bigint PRIMARY KEY,
bezeichnung NVARCHAR(50),
kunden_id bigint,
datum date,
idx bigint,
erstelldatum date,
aktualisierungsdatum date);


CREATE TABLE Artikel(
id bigint PRIMARY KEY,
bezeichnung nvarchar(50),
groesse nvarchar(10),
preis decimal(10,2),
saison_id bigint,
kategorie_id bigint,
abteilung_id bigint,
erstelldatum date,
aktualisierungsdatum date);


CREATE TABLE Kunde(
id bigint PRIMARY KEY,
email nvarchar(50),
name nvarchar(50),
vorname nvarchar(50),
strasse nvarchar(50),
hausnummer nvarchar(50),
plz nvarchar(10),
ort nvarchar(50),
erstelldatum date,
aktualisierungsdatum date);


CREATE TABLE Posten(
id bigint PRIMARY KEY,
menge INTEGER,
bestellungs_id bigint,
artikel_id bigint,
idx bigint,
erstelldatum date,
aktualisierungsdatum date);


alter table bestellung add foreign key (kunden_id) references kunde (id);
alter table artikel add foreign key (kategorie_id) references kategorie (id);
alter table artikel add foreign key (saison_id) references saison (id);
alter table artikel add foreign key (abteilung_id) references abteilung (id);
alter table posten add foreign key (bestellungs_id) references bestellung (id);
alter table posten add foreign key (artikel_id) references artikel (id);
alter table kunde add UNIQUE (email);