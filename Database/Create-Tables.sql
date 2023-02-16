DROP DATABASE IF EXISTS `jobboard`;
CREATE DATABASE `jobboard`; 
USE jobboard;

CREATE TABLE USERS (

Email VARCHAR(64) UNIQUE NOT NULL,
Password VARCHAR(16) NOT NULL,
AccountType ENUM('CONTRACTOR', 'CONTRACTEE') NOT NULL, 
Token CHAR(16) UNIQUE NOT NULL, -- Unsure if this should go into another table called user_tokens, i think not as token is directly dependant on and only on user, plus it is apart of user, cant exist without it

CONSTRAINT PK PRIMARY KEY(EMAIL),
CONSTRAINT PWL CHECK(LENGTH(PASSWORD) > 5)

);

CREATE TABLE PASSWORD_RESET_TOKENS ( -- Will get auto-deleted by our model running a detached thread every 10 minutes

Email VARCHAR(32) UNIQUE NOT NULL,
Token CHAR(5) UNIQUE NOT NULL,

CONSTRAINT PK PRIMARY KEY(Email),
CONSTRAINT FKE FOREIGN KEY(Email) REFERENCES USERS(Email) ON UPDATE NO ACTION ON DELETE NO ACTION

);

INSERT INTO USERS VALUES('Admin@admin.com', 'password', 'CONTRACTOR', '666');

CREATE TABLE CONTRACT ( -- Considered only making a single contract table where we would store the contract status, contractee, deadline, completion date, and link, but realized it would be similar speed & less clearer due to redundant data which would all be null by default

JobID INT AUTO_INCREMENT UNIQUE NOT NULL,
Description TEXT NOT NULL,
Contractor VARCHAR(32) NOT NULL, 
Length INT NOT NULL,

CONSTRAINT PK PRIMARY KEY(JobID),
CONSTRAINT FKU FOREIGN KEY(Contractor) REFERENCES USERS(Email) ON UPDATE CASCADE ON DELETE CASCADE,
CONSTRAINT L CHECK (Length >= 1)

); -- Only thing slower with this approach would be getting unoccupied contracts as we would need a NOT IN ACTIVE_CONTRACTS & NOT IN COMPLETED_CONTRACTS which is alot slower, rest would be very similar

CREATE TABLE ACTIVE_CONTRACTS (

JobID INT UNIQUE NOT NULL,
Contractee VARCHAR(32) NOT NULL,
Deadline DATE NOT NULL,

CONSTRAINT PK PRIMARY KEY(JobID),
CONSTRAINT FKAJOBID FOREIGN KEY(JobID) REFERENCES CONTRACT(JobID) ON UPDATE NO ACTION ON DELETE NO ACTION

);

CREATE TABLE COMPLETED_CONTRACTS ( 

JobID INT UNIQUE NOT NULL,
Contractee VARCHAR(32) NOT NULL,
CompletionDate DATE NOT NULL,
Link TEXT,

CONSTRAINT PK PRIMARY KEY(JobID),
CONSTRAINT FKCJOBID FOREIGN KEY(JobID) REFERENCES CONTRACT(JobID) ON UPDATE NO ACTION ON DELETE NO ACTION,
CONSTRAINT FKContractee FOREIGN KEY(Contractee) REFERENCES USERS(Email) ON UPDATE NO ACTION ON DELETE NO ACTION

);
INSERT INTO CONTRACT VALUES (1, "SAMPLE", "Admin@admin.com", 5);
INSERT INTO CONTRACT VALUES (2, "SAMPLE", "Admin@admin.com", 10);