CREATE TABLE TAB_MASTER ( ID CHAR NOT NULL, NAME VARCHAR(20), PRIMARY KEY (ID) )
CREATE TABLE TAB_CHILD  ( CHILDID CHARACTER NOT NULL, MASTERREF CHAR, PRIMARY KEY (CHILDID), FOREIGN KEY (MASTERREF) REFERENCES TAB_MASTER(ID) )