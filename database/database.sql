-- Création de la base
CREATE DATABASE cicd;
\c cicd
-- Table clients
CREATE TABLE client (
    id_client CHAR(4) PRIMARY KEY,  -- 4 chiffres
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);

-- Table hôtels
CREATE TABLE hotel (
    id_hotel SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    adresse VARCHAR(200)
);

-- Table réservations
CREATE TABLE reservation (
    id_reservation SERIAL PRIMARY KEY,
    id_client CHAR(4) NOT NULL,
    nb_passager INT NOT NULL,
    date_heure_arrivee TIMESTAMP NOT NULL,
    id_hotel INT NOT NULL,
    FOREIGN KEY (id_client) REFERENCES client(id_client),
    FOREIGN KEY (id_hotel) REFERENCES hotel(id_hotel)
);

-- Initialisation des clients
INSERT INTO client (id_client, nom, prenom, email) VALUES
('0001', 'Rakoto', 'Jean', 'jean.rakoto@email.com'),
('0002', 'Rabe', 'Mina', 'mina.rabe@email.com'),
('0003', 'Andrian', 'Faly', 'faly.andrian@email.com');

-- Initialisation des hôtels
INSERT INTO hotel (nom, adresse) VALUES
('Hotel A', 'Rue de la Paix, Antananarivo'),
('Hotel B', 'Rue du Commerce, Antsirabe'),
('Hotel C', 'Avenue des Plaines, Fianarantsoa');

-- Initialisation des réservations
INSERT INTO reservation (id_client, nb_passager, date_heure_arrivee, id_hotel) VALUES
('0001', 2, '2026-02-10 14:00:00', 1),
('0002', 4, '2026-02-11 16:30:00', 2),
('0003', 1, '2026-02-12 10:15:00', 3);