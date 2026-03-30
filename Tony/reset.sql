DELETE FROM drive_dev.disponibilites;
ALTER SEQUENCE IF EXISTS drive_dev.disponibilites_id_seq RESTART WITH 1;

DELETE FROM drive_staging.disponibilites;
ALTER SEQUENCE IF EXISTS drive_staging.disponibilites_id_seq RESTART WITH 1;

DELETE FROM drive_prod.disponibilites;
ALTER SEQUENCE IF EXISTS drive_prod.disponibilites_id_seq RESTART WITH 1;

-- =====================================================
-- SCHEMA : drive_dev
-- =====================================================

-- Reset planification (snapshot + details)
DELETE FROM drive_dev.plan_non_assignees;
DELETE FROM drive_dev.plan_etapes;
DELETE FROM drive_dev.plan_clients;
DELETE FROM drive_dev.plan_trajets;
DELETE FROM drive_dev.planifications;

ALTER SEQUENCE IF EXISTS drive_dev.planifications_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.plan_trajets_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.plan_clients_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.plan_etapes_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.plan_non_assignees_id_seq RESTART WITH 1;

DELETE FROM drive_dev.reservations;
DELETE FROM drive_dev.distances;
DELETE FROM drive_dev.disponibilites;
DELETE FROM drive_dev.configurations;
DELETE FROM drive_dev.vehicules;
DELETE FROM drive_dev.tokens;
DELETE FROM drive_dev.hotels;

ALTER SEQUENCE IF EXISTS drive_dev.hotels_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.reservations_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.vehicules_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.distances_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.disponibilites_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.configurations_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS drive_dev.tokens_id_seq RESTART WITH 1;

-- HOTELS

INSERT INTO drive_dev.hotels (code, nom) VALUES ('TNR', 'Aeroport Ivato');
INSERT INTO drive_dev.hotels (code, nom) VALUES ('HT1', 'Hotel 1');

-- DISTANCES (bidirectionnelles)

INSERT INTO drive_dev.distances (id_from, id_to, km) VALUES (1, 2, 50);
INSERT INTO drive_dev.distances (id_from, id_to, km) VALUES (2, 1, 50);


-- CONFIGURATIONS

INSERT INTO drive_dev.configurations (cle, valeur) VALUES ('tempsAttente', '30');
INSERT INTO drive_dev.configurations (cle, valeur) VALUES ('vitesseMoyenne', '50');
INSERT INTO drive_dev.configurations (cle, valeur) VALUES ('aeroport', 'TNR');

-- VEHICULES

INSERT INTO drive_dev.vehicules (reference, nb_places, type_carburant) VALUES ('Vehicule 1', 10, 'D');
-- DISPONIBILITES (dateHeure)
-- Les vehicules sont disponibles toute la journee de test

INSERT INTO drive_dev.disponibilites (id_vehicule, date_heure_debut, date_heure_fin)
VALUES 
 (1, '2026-03-25 10:00:00', '2026-03-25 23:59:59');

-- TOKEN

INSERT INTO drive_dev.tokens (token, date_heure_expiration) VALUES ('szjS1VSGyuJisimK', '2027-12-31 23:59:59');

-- RESERVATIONS

INSERT INTO drive_dev.reservations (id_client, nb_passager, date_heure_arrivee, id_hotel)
VALUES ('C1', 10, '2026-03-25 08:00:00', 2);
INSERT INTO drive_dev.reservations (id_client, nb_passager, date_heure_arrivee, id_hotel)
VALUES ('C2', 15, '2026-03-25 10:10:00', 2);
INSERT INTO drive_dev.reservations (id_client, nb_passager, date_heure_arrivee, id_hotel)
VALUES ('C3', 8, '2026-03-25 10:15:00', 2);
