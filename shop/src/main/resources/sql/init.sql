DROP SEQUENCE hibernate_sequence;
CREATE SEQUENCE hibernate_sequence START WITH 5000;

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

insert into Abteilung values(0,'Damenmode');
insert into Abteilung values(1,'Herrenmode');
insert into Abteilung values(2,'Kindermode');
insert into Abteilung values(3,'Babymode');

insert into Kategorie values(0,'Babykleidung');
insert into Kategorie values(1,'Accessoires');
insert into Kategorie values(2,'Anzuege');
insert into Kategorie values(3,'Bademode');
insert into Kategorie values(4,'Blazer und Sakkos');
insert into Kategorie values(5,'Blusen');
insert into Kategorie values(6,'Hemden');
insert into Kategorie values(7,'Hosen');
insert into Kategorie values(8,'Jacken und Maentel');
insert into Kategorie values(9,'Jeanshosen');
insert into Kategorie values(10,'Kleider');
insert into Kategorie values(11,'Kostueme und Mehrteiler');
insert into Kategorie values(12,'Nachtwaesche und Bademaentel');
insert into Kategorie values(13,'Pullover und Strickjacken');
insert into Kategorie values(14,'Roecke');
insert into Kategorie values(15,'Shirts');
insert into Kategorie values(16,'Struempfe und Strumpfhosen');
insert into Kategorie values(17,'Sweatshirts');
insert into Kategorie values(18,'Tops');
insert into Kategorie values(19,'Unterwaesche');
insert into Kategorie values(20,'Westen');
insert into Kategorie values(21,'Umstandskleidung');
insert into Kategorie values(22,'Spezielle Anlaesse');

insert into Saison values(0,'Fruejahr');
insert into Saison values(1,'Sommer');
insert into Saison values(2,'Herbst');
insert into Saison values(3,'Winter');

insert into Artikel(id, bezeichnung, groesse, preis, saison, kategorie, abteilung, erstelldatum, aktualisierungsdatum) values(0,'Winterjacke EsIstKalt',54,99.99,3,9,1,'29.10.2012','29.10.2012');
insert into Artikel values(1,'BlueJeans Classic','31/31',49.99,NULL,10,1,29.10.2012,NULL);
insert into Artikel values(2,'Schicker Anzug','52',499.99,NULL,2,1,29.10.2012,NULL);
insert into Artikel values(3,'Lederguertel Active','M',29.95,NULL,1,1,29.10.2012,NULL);
insert into Artikel values(4,'Abendkleid Frischluft','40',99.95,NULL,11,0,29.10.2012,NULL);

insert into Kunde(id, email, name, vorname, strasse, hausnummer, plz, ort, erstelldatum, aktualisirungsdatum) values(0,'fred.ly@googlemail.com','Ly','Fred','Auerbacher Straße','5A','86514','Angelbach',29.10.2012,NULL);
insert into Kunde values(1,'pascal.vetter21@googlemail.com','Vetter','Pascal','Breitenweg','22','94832','Bietigheim',29.10.2012,NULL);
insert into Kunde values(2,'eberhardt.yannick@gmail.com','Eberhardt','Yannick','Crossweg','1','47512','Chemnitz',29.10.2012,NULL);
insert into Kunde values(3,'kristian.sudar@gmail.com','Sudar','Kristian','Drachtengasse','23','21564','Dallau',29.10.2012,NULL);
insert into Kunde values(4,'menges.marc@gmail.com','Menges','Marc','Erlenweg','87B','47862','Eberbach',29.10.2012,NULL);

insert into Bestellung values(0,'Bestellung_K0_0,0',23.01.2012,NULL,29.10.2012,NULL);
insert into Bestellung values(1,'Bestellung_K1_0,0',23.01.2012,NULL,29.10.2012,NULL);
insert into Bestellung values(2,'Bestellung_K2_0,0',23.01.2012,NULL,29.10.2012,NULL);
insert into Bestellung values(3,'Bestellung_K3_0,0',23.01.2012,NULL,29.10.2012,NULL);
insert into Bestellung values(4,'Bestellung_K3_1,0',23.01.2012,NULL,29.10.2012,NULL);

insert into Posten values(0,1,0,0,0,29.10.2012,NULL);
insert into Posten values(1,2,0,1,1,29.10.2012,NULL);
insert into Posten values(2,1,1,3,0,29.10.2012,NULL);
insert into Posten values(3,5,2,4,0,29.10.2012,NULL);
insert into Posten values(4,1,2,1,1,29.10.2012,NULL);
insert into Posten values(5,1,3,2,0,29.10.2012,NULL);
insert into Posten values(6,2,4,4,0,29.10.2012,NULL);

