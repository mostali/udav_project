--asd;
--DROP TABLE IF EXISTS web_usr;
--CREATE TABLE IF NOT EXISTS web_usr(id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT,  login TEXT, phc TEXT, net TEXT, sid INTEGER, roles TEXT  )
CREATE TABLE IF NOT EXISTS appusr (DTYPE varchar(31) not null, id bigint not null, alias varchar(255), first_name varchar(255), last_name varchar(255), login varchar(255), net varchar(255), nid bigint, phc varchar(255), roles varchar(255), sid bigint, primary key (id));
CREATE TABLE IF NOT EXISTS  hibernate_sequence (next_val bigint);
INSERT INTO hibernate_sequence(next_val) VALUES(1)

