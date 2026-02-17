
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
    type_carburant VARCHAR(10) NOT NULL CHECK (type_carburant IN ('D', 'Es', 'H', 'El')),
    vitesse_moyenne DECIMAL(5,2) NOT NULL DEFAULT 60.00 -- en km/h
);

-- Table lieux (Sprint 3 - Planification)
CREATE TABLE lieu (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

-- Table distances (Sprint 3 - Planification)
CREATE TABLE distance (
    id SERIAL PRIMARY KEY,
    lieu_depart INT NOT NULL,
    lieu_arrivee INT NOT NULL,
    km DECIMAL(6,2) NOT NULL,
    FOREIGN KEY (lieu_depart) REFERENCES lieu(id),
    FOREIGN KEY (lieu_arrivee) REFERENCES lieu(id)
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

-- Initialisation des lieux
INSERT INTO lieu (code, libelle) VALUES
('TNR', 'Ivato'),          -- Aéroport
('COL', 'Colbert'),
('NOV', 'Novotel'),
('IBL', 'Ibis'),
('LOK', 'Lokanga');

-- Initialisation des distances (de l'aéroport vers les hôtels)
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
(1, 2, 18.5),  -- Ivato -> Colbert
(1, 3, 16.2),  -- Ivato -> Novotel
(1, 4, 17.8),  -- Ivato -> Ibis
(1, 5, 19.3),  -- Ivato -> Lokanga
(2, 1, 18.5),  -- Colbert -> Ivato
(3, 1, 16.2),  -- Novotel -> Ivato
(4, 1, 17.8),  -- Ibis -> Ivato
(5, 1, 19.3);  -- Lokanga -> Ivato

-- Initialisation des véhicules
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne) VALUES
('VH-2026-001', 5, 'Es', 55.0),
('VH-2026-002', 7, 'D', 60.0),
('VH-2026-003', 5, 'El', 50.0),
('VH-2026-004', 4, 'H', 58.0),
('VH-2026-005', 12, 'D', 55.0),
('VH-2026-006', 18, 'D', 50.0);
