
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

-- Initialisation des hôtels
INSERT INTO hotel (libelle) VALUES
('Colbert'),
('Novotel'),
('Ibis'),
('Lokanga');

-- Initialisation des réservations
INSERT INTO reservation (reference, nombre, date, heure, hotel) VALUES
(4631, 11, '2026-02-05', '00:01', 3),
(4394, 1, '2026-02-05', '23:55', 3),
(8054, 2, '2026-02-09', '10:17', 1),
(1432, 4, '2026-02-01', '15:25', 2),
(7861, 4, '2026-01-28', '07:11', 1),
(3308, 5, '2026-01-28', '07:45', 1),
(4484, 13, '2026-02-28', '08:25', 2),
(9687, 8, '2026-02-28', '13:00', 2),
(6302, 7, '2026-02-15', '13:00', 1),
(8640, 1, '2026-02-18', '22:55', 4);