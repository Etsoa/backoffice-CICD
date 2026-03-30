\c postgres;
DROP DATABASE IF EXISTS cicd;
CREATE DATABASE cicd;
\c cicd;


DROP TABLE IF EXISTS itineraire_arret;
DROP TABLE IF EXISTS assignation_vehicule;
DROP TABLE IF EXISTS suivi_trajet_vehicule;
DROP TABLE IF EXISTS regroupement_reservation;
DROP TABLE IF EXISTS regroupement;
DROP TABLE IF EXISTS planification;
DROP TABLE IF EXISTS distance;
DROP TABLE IF EXISTS lieu;
DROP TABLE IF EXISTS token;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS vehicule;
DROP TABLE IF EXISTS hotel CASCADE;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS type_carburant;
DROP TABLE IF EXISTS parametre;
DROP TABLE IF EXISTS configuration_attente;
