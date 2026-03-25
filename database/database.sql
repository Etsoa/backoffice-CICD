
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

-- Table types de carburant
CREATE TABLE type_carburant (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    libelle VARCHAR(50) NOT NULL
);

-- Table véhicules (Sprint 2)
CREATE TABLE vehicule (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    place INT NOT NULL,
    type_carburant INT NOT NULL,
    vitesse_moyenne DECIMAL(5,2) NOT NULL DEFAULT 60.00, -- en km/h
    FOREIGN KEY (type_carburant) REFERENCES type_carburant(id)
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

-- Table paramètres (configuration)
CREATE TABLE parametre (
    id SERIAL PRIMARY KEY,
    cle VARCHAR(50) NOT NULL UNIQUE,
    valeur VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- Table configuration du temps d'attente (Sprint 5)
-- Permet de configurer le délai d'attente pour le regroupement des réservations
CREATE TABLE configuration_attente (
    id SERIAL PRIMARY KEY,
    temps_attente_minutes INT NOT NULL DEFAULT 30,
    description VARCHAR(255),
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- Initialisation des hôtels
INSERT INTO hotel (libelle) VALUES
('Colbert'),
('Novotel'),
('Ibis'),
('Lokanga');

-- Initialisation des réservations (données de test couvrant tous les scénarios)
INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
-- === Date 2026-01-28 : Fenêtre temporelle + groupement ===
(1001, 4, '2026-01-28', '07:00', 1, 'client1001'),   -- 4 pers, Colbert à 07:00
(1002, 3, '2026-01-28', '07:20', 2, 'client1002'),   -- 3 pers, Novotel à 07:20 (dans fenêtre 07:00+30min → groupement possible)
(1003, 2, '2026-01-28', '07:45', 3, 'client1003'),   -- 2 pers, Ibis à 07:45 (hors fenêtre → nouveau véhicule)
(1004, 11, '2026-01-28', '15:00', 4, 'client1004'),  -- 11 pers, Lokanga à 15:00 (gros véhicule nécessaire)

-- === Date 2026-02-05 : Même heure + multi-hôtels + dépassement capacité ===
(2001, 3, '2026-02-05', '10:00', 1, 'client2001'),   -- 3 pers, Colbert à 10:00
(2002, 1, '2026-02-05', '10:00', 4, 'client2002'),   -- 1 pers, Lokanga à 10:00 (même heure, groupement avec 2001)
(2003, 2, '2026-02-05', '10:15', 3, 'client2003'),   -- 2 pers, Ibis à 10:15 (dans fenêtre, total cumulé 3+1+2=6)
(2004, 4, '2026-02-05', '10:25', 2, 'client2004'),   -- 4 pers, Novotel à 10:25 (dans fenêtre, 6+4=10 → dépasse → nouveau véhicule)
(2005, 7, '2026-02-05', '18:30', 1, 'client2005'),   -- 7 pers, Colbert à 18:30 (fenêtre isolée, véhicule 8 places Diesel)

-- === Date 2026-02-09 : Véhicule seul + priorité Diesel + proximité ===
(3001, 2, '2026-02-09', '08:30', 2, 'client3001'),   -- 2 pers, Novotel à 08:30 (seul → petit véhicule)
(3002, 5, '2026-02-09', '14:00', 1, 'client3002'),   -- 5 pers, Colbert à 14:00
(3003, 4, '2026-02-09', '14:10', 3, 'client3003');   -- 4 pers, Ibis à 14:10 (dans fenêtre, 5+4=9 → dépasse 5 places → nouveau véhicule)

-- Initialisation des lieux
INSERT INTO lieu (code, libelle) VALUES
('TNR', 'Ivato'),          -- Aéroport
('COL', 'Colbert'),
('NOV', 'Novotel'),
('IBL', 'Ibis'),
('LOK', 'Lokanga');

-- Initialisation des distances (une seule direction par paire, la distance est symétrique)
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
(1, 2, 18.5),  -- Ivato <-> Colbert
(1, 3, 16.2),  -- Ivato <-> Novotel
(1, 4, 17.8),  -- Ivato <-> Ibis
(1, 5, 19.3),  -- Ivato <-> Lokanga
(2, 3, 3.5),   -- Colbert <-> Novotel
(2, 4, 2.8),   -- Colbert <-> Ibis
(2, 5, 4.0),   -- Colbert <-> Lokanga
(3, 4, 2.0),   -- Novotel <-> Ibis
(3, 5, 5.2),   -- Novotel <-> Lokanga
(4, 5, 3.6);   -- Ibis <-> Lokanga

-- Initialisation des types de carburant
INSERT INTO type_carburant (code, libelle) VALUES
('D', 'Diesel'),
('Es', 'Essence'),
('H', 'Hybride'),
('El', 'Electrique');

-- Initialisation des véhicules
-- Ordre de priorité : Diesel (type 1), Essence (type 2), puis autres (Hybride 3, Electrique 4)
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne) VALUES
('VH-2026-001', 4, 1, 60.00),   -- 4 places, Diesel
('VH-2026-002', 8, 1, 55.00),   -- 8 places, Diesel (priorite)
('VH-2026-003', 5, 2, 65.00),   -- 5 places, Essence
('VH-2026-004', 12, 3, 50.00),  -- 12 places, Hybride
('VH-2026-005', 3, 4, 70.00),   -- 3 places, Electrique
('VH-2026-006', 18, 1, 50.0);   -- 18 places, Diesel (gros vehicule)

-- Initialisation des paramètres
INSERT INTO parametre (cle, valeur, description) VALUES
('delai_attente', '30', 'Délai d''attente en minutes avant le départ du véhicule');

-- Initialisation de la configuration du temps d'attente (Sprint 5)
INSERT INTO configuration_attente (temps_attente_minutes, description, actif) VALUES
(30, 'Configuration par défaut - 30 minutes de délai pour le regroupement des réservations', TRUE);
