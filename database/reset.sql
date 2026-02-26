\c postgres;
DROP DATABASE IF EXISTS cicd;
CREATE DATABASE cicd;
\c cicd;


DROP TABLE client;
DROP TABLE hotel cascade;
DROP TABLE reservation;
DROP TABLE vehicule;
DROP TABLE token;