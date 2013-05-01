DROP SEQUENCE hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START WITH 5000;

CREATE INDEX bestellung__kunde_index ON bestellung(kunden_id);
CREATE INDEX posten__bestellung_index ON posten(bestellungs_id);
CREATE INDEX posten__artikel_index ON posten(artikel_id);
CREATE INDEX artikel__saison_index ON artikel(saison_id);
CREATE INDEX artikel__kategorie_index ON artikel(kategorie_id);
CREATE INDEX artikel__abteilung_index ON artikel(abteilung_id);

--CREATE TABLE hibernate_sequence(next_val bigint PRIMARY KEY);
--CREATE TABLE Abteilung(id BIGINT PRIMARY KEY,bezeichnung NVARCHAR(50));
--CREATE TABLE Kategorie(id bigint PRIMARY KEY,bezeichnung nvarchar(50));
--CREATE TABLE Saison(id bigint PRIMARY KEY,bezeichnung nvarchar(50));
--CREATE TABLE Bestellung(id bigint PRIMARY KEY,bezeichnung NVARCHAR(50),kunden_id bigint,datum date,idx bigint,erstelldatum date,aktualisierungsdatum date);
--CREATE TABLE Artikel(id bigint PRIMARY KEY,bezeichnung nvarchar(50),groesse nvarchar(10),preis decimal(10,2),saison_id bigint,kategorie_id bigint,abteilung_id bigint,erstelldatum date,aktualisierungsdatum date);
--CREATE TABLE Kunde(id bigint PRIMARY KEY,email nvarchar(50),name nvarchar(50),vorname nvarchar(50),strasse nvarchar(50),hausnummer nvarchar(50),plz nvarchar(10),ort nvarchar(50),erstelldatum date,aktualisierungsdatum date);
--CREATE TABLE Posten(id bigint PRIMARY KEY,menge INTEGER,bestellungs_id bigint,artikel_id bigint,idx bigint,erstelldatum date,aktualisierungsdatum date);

--alter table bestellung add foreign key (kunden_id) references kunde (id);
--alter table artikel add foreign key (kategorie_id) references kategorie (id);
--alter table artikel add foreign key (saison_id) references saison (id);
--alter table artikel add foreign key (abteilung_id) references abteilung (id);
--alter table posten add foreign key (bestellungs_id) references bestellung (id);
--alter table posten add foreign key (artikel_id) references artikel (id);
--alter table kunde add UNIQUE (email);


--insert into hibernate_sequence values(100);

insert into Abteilung (id, version, bezeichnung) values(0,0,'Damenmode');
insert into Abteilung (id, version, bezeichnung) values(1,0,'Herrenmode');
insert into Abteilung (id, version, bezeichnung) values(2,0,'Kindermode');
insert into Abteilung (id, version, bezeichnung) values(3,0,'Babymode');

insert into Kategorie (id, version, bezeichnung) values(0,0,'Babykleidung');
insert into Kategorie (id, version, bezeichnung) values(1,0,'Accessoires');
insert into Kategorie (id, version, bezeichnung) values(2,0,'Anzuege');
insert into Kategorie (id, version, bezeichnung) values(3,0,'Bademode');
insert into Kategorie (id, version, bezeichnung) values(4,0,'Blazer und Sakkos');
insert into Kategorie (id, version, bezeichnung) values(5,0,'Blusen');
insert into Kategorie (id, version, bezeichnung) values(6,0,'Hemden');
insert into Kategorie (id, version, bezeichnung) values(7,0,'Hosen');
insert into Kategorie (id, version, bezeichnung) values(8,0,'Jacken und Maentel');
insert into Kategorie (id, version, bezeichnung) values(9,0,'Jeanshosen');
insert into Kategorie (id, version, bezeichnung) values(10,0,'Kleider');
insert into Kategorie (id, version, bezeichnung) values(11,0,'Kostueme und Mehrteiler');
insert into Kategorie (id, version, bezeichnung) values(12,0,'Nachtwaesche und Bademaentel');
insert into Kategorie (id, version, bezeichnung) values(13,0,'Pullover und Strickjacken');
insert into Kategorie (id, version, bezeichnung) values(14,0,'Roecke');
insert into Kategorie (id, version, bezeichnung) values(15,0,'Shirts');
insert into Kategorie (id, version, bezeichnung) values(16,0,'Struempfe und Strumpfhosen');
insert into Kategorie (id, version, bezeichnung) values(17,0,'Sweatshirts');
insert into Kategorie (id, version, bezeichnung) values(18,0,'Tops');
insert into Kategorie (id, version, bezeichnung) values(19,0,'Unterwaesche');
insert into Kategorie (id, version, bezeichnung) values(20,0,'Westen');
insert into Kategorie (id, version, bezeichnung) values(21,0,'Umstandskleidung');
insert into Kategorie (id, version, bezeichnung) values(22,0,'Spezielle Anlaesse');

insert into Saison (id, version, bezeichnung) values(0,0,'Fruejahr');
insert into Saison (id, version, bezeichnung) values(1,0,'Sommer');
insert into Saison (id, version, bezeichnung) values(2,0,'Herbst');
insert into Saison (id, version, bezeichnung) values(3,0,'Winter');

insert into Artikel(id, version, bezeichnung, groesse, preis, saison_id, kategorie_id, abteilung_id, erstelldatum, aktualisierungsdatum) values(0,0,'Winterjacke EsIstKalt',54,99.99,3,9,1,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Artikel(id, version, bezeichnung, groesse, preis, saison_id, kategorie_id, abteilung_id, erstelldatum, aktualisierungsdatum) values(1,0,'BlueJeans Classic','31/31',49.99,3, 10, 1, to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Artikel(id, version, bezeichnung, groesse, preis, saison_id, kategorie_id, abteilung_id, erstelldatum, aktualisierungsdatum) values(2,0,'Schicker Anzug','52',499.99,3,2,1,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Artikel(id, version, bezeichnung, groesse, preis, saison_id, kategorie_id, abteilung_id, erstelldatum, aktualisierungsdatum) values(3,0,'Lederguertel Active','M',29.95,3,1,1,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Artikel(id, version, bezeichnung, groesse, preis, saison_id, kategorie_id, abteilung_id, erstelldatum, aktualisierungsdatum) values(4,0,'Abendkleid Frischluft','40',99.95,1,11,0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));

insert into Kunde(id, version, email, password, name, vorname, strasse, hausnummer, plz, ort, erstelldatum, aktualisierungsdatum) values(0,0,'fred.ly@googlemail.com', 'tlifxqsNyCzxIJnRwtQKuZToQQw=','Ly','Fred','Auerbacher Straße','5A','86514','Angelbach',to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Kunde(id, version, email, password, name, vorname, strasse, hausnummer, plz, ort, erstelldatum, aktualisierungsdatum) values(1,0,'pascal.vetter21@googlemail.com', 'NWoZK3kTsExUV00Ywo1G5jlUKKs=', 'Vetter','Pascal','Breitenweg','22','94832','Bietigheim',to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Kunde(id, version, email, password, name, vorname, strasse, hausnummer, plz, ort, erstelldatum, aktualisierungsdatum) values(2,0,'eberhardt.yannick@gmail.com', '2kuSN7rMzfGcB2DKt67EqDWQELA=', 'Eberhardt','Yannick','Crossweg','1','47512','Chemnitz',to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Kunde(id, version, email, password, name, vorname, strasse, hausnummer, plz, ort, erstelldatum, aktualisierungsdatum) values(3,0,'kristian.sudar@gmail.com', 'd95o2uzYI7q7tY7bHI4U1xBug7s=', 'Sudar','Kristian','Drachtengasse','23','21564','Dallau',to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Kunde(id, version, email, password, name, vorname, strasse, hausnummer, plz, ort, erstelldatum, aktualisierungsdatum) values(4,0,'menges.marc@gmail.com', 'G2RTiSRzpGfQc3LUXrBavCAxZHo=', 'Menges','Marc','Erlenweg','87B','47862','Eberbach',to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));

INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (0,0);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (0,1);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (0,2);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (1,0);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (1,1);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (1,2);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (2,1);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (2,2);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (3,1);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (3,2);
INSERT INTO kunde_rolle (kunde_fk, rolle_fk) VALUES (4,2);

insert into Bestellung(id, version, bezeichnung, kunden_id, datum, idx, erstelldatum, aktualisierungsdatum) values(0,0,'Bestellung_K0_0',0,to_date('01/23/2012','mm/dd/yyyy'),0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Bestellung(id, version, bezeichnung, kunden_id, datum, idx, erstelldatum, aktualisierungsdatum) values(1,0,'Bestellung_K1_0',1,to_date('01/23/2012','mm/dd/yyyy'),0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Bestellung(id, version, bezeichnung, kunden_id, datum, idx, erstelldatum, aktualisierungsdatum) values(2,0,'Bestellung_K2_0',2,to_date('01/23/2012','mm/dd/yyyy'),0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Bestellung(id, version, bezeichnung, kunden_id, datum, idx, erstelldatum, aktualisierungsdatum) values(3,0,'Bestellung_K3_0',3,to_date('01/23/2012','mm/dd/yyyy'),0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Bestellung(id, version, bezeichnung, kunden_id, datum, idx, erstelldatum, aktualisierungsdatum) values(4,0,'Bestellung_K3_1',3,to_date('01/23/2012','mm/dd/yyyy'),1,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));

insert into Posten(id, version, menge, bestellungs_id, artikel_id, idx, erstelldatum, aktualisierungsdatum) values(0,0,1,0,0,0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Posten(id, version, menge, bestellungs_id, artikel_id, idx, erstelldatum, aktualisierungsdatum) values(1,0,2,0,1,1,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Posten(id, version, menge, bestellungs_id, artikel_id, idx, erstelldatum, aktualisierungsdatum) values(2,0,1,1,3,0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Posten(id, version, menge, bestellungs_id, artikel_id, idx, erstelldatum, aktualisierungsdatum) values(3,0,5,2,4,0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Posten(id, version, menge, bestellungs_id, artikel_id, idx, erstelldatum, aktualisierungsdatum) values(4,0,1,2,1,1,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Posten(id, version, menge, bestellungs_id, artikel_id, idx, erstelldatum, aktualisierungsdatum) values(5,0,1,3,2,0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));
insert into Posten(id, version, menge, bestellungs_id, artikel_id, idx, erstelldatum, aktualisierungsdatum) values(6,0,2,4,4,0,to_date('10/29/2012','mm/dd/yyyy'),to_date('10/29/2012','mm/dd/yyyy'));

