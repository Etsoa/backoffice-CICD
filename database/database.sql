
\c cicd

-- Table clients
CREATE TABLE client (
    id_client CHAR(4) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);

-- Table hôtels
CREATE TABLE hotel (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- Table réservations
CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    reference INT NOT NULL,
    nombre INT NOT NULL,
    date DATE NOT NULL,
    heure TIME NOT NULL,
    hotel INT NOT NULL,
    FOREIGN KEY (hotel) REFERENCES hotel(id)
);

-- Table véhicules (Sprint 2)
CREATE TABLE vehicule (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    place INT NOT NULL,
    type_carburant VARCHAR(10) NOT NULL CHECK (type_carburant IN ('D', 'Es', 'H', 'El'))
);

-- Table tokens (Sprint 2 - Protection)
CREATE TABLE token (
    id SERIAL PRIMARY KEY,
    valeur_token VARCHAR(16) NOT NULL UNIQUE,
    date_expiration TIMESTAMP NULL
);

-- Initialisation des hôtels
INSERT INTO hotel (libelle) VALUES
('Colbert'),
('Novotel'),
('Ibis'),
('Lokanga');

-- Initialisation des réservations@ -35,13 +35,5 @@ INSERT INTO hotel (libelle) VALUES

-- Initialisation des réservations
INSERT INTO reservation (reference, nombre, date, heure, hotel) VALUES
(4631, 11, '2026-02-05', '00:01', 3),
(4394, 1, '2026-02-05', '23:55', 3),
(8054, 2, '2026-02-09', '10:17', 1),
(1432, 4, '2026-02-01', '15:25', 2),
(7861, 4, '2026-01-28', '07:11', 1),
(3308, 5, '2026-01-28', '07:45', 1);

-- Initialisation des véhicules
INSERT INTO vehicule (reference, place, type_carburant) VALUES
('VH-2026-001', 5, 'Es'),
('VH-2026-002', 7, 'D'),
('VH-2026-003', 5, 'El'),
('VH-2026-004', 4, 'H');




et suqqrimer ca donne ca Erreur: Cannot invoke "java.util.HashMap.get(Object)" because "params" is null