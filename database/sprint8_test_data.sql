-- ============================================
-- TYPES DE CARBURANT
-- ============================================
INSERT INTO type_carburant (code, libelle) VALUES
('DIESEL', 'Diesel'),
('ESSENCE', 'Essence');

-- ============================================
-- VÉHICULES
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('V1', 8, 1, 60, '08:00:00'),  -- Diesel
('V2', 6, 1, 60, '08:00:00'),  -- Diesel
('V3', 10, 2, 60, '08:30:00'), -- Essence
('V4', 4, 2, 60, '09:00:00');  -- Essence

-- ============================================
-- CLIENTS
-- ============================================
INSERT INTO client (id_client, nom, prenom, email) VALUES
('C001', 'Rakoto', 'Jean', 'jean.rakoto@example.com'),
('C002', 'Rabe', 'Marie', 'marie.rabe@example.com'),
('C003', 'Andrian', 'Paul', 'paul.andrian@example.com'),
('C004', 'Razaf', 'Lina', 'lina.razaf@example.com'),
('C005', 'Ramal', 'David', 'david.ramal@example.com');

-- ============================================
-- HÔTELS
-- ============================================
INSERT INTO hotel (libelle) VALUES
('Hotel Colbert'),
('Hotel Novotel'),
('Hotel Carlton');

-- ============================================
-- LIEUX
-- ============================================
INSERT INTO lieu (code, libelle) VALUES
('TNR', 'Aéroport TNR'),
('H1', 'Hotel Colbert'),
('H2', 'Hotel Novotel'),
('H3', 'Hotel Carlton');

-- ============================================
-- DISTANCES
-- ============================================
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
(1,2,10), (1,3,15), (1,4,20),
(2,3,5), (2,4,12), (3,4,8);

-- ============================================
-- PLANIFICATION
-- ============================================
INSERT INTO planification (date_planification, delai_attente_utilise, nombre_regroupements, nombre_reservations_total) VALUES
('2026-05-01', 30, 0, 0);

-- ============================================
-- RÉSERVATIONS (phase 1 non assignés et phase 2 vols)
-- ============================================
-- Non assignés (anciens)
INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(1001, 3, '2026-05-01', '08:00:00', 1, 'C001'),
(1002, 2, '2026-05-01', '08:05:00', 2, 'C002');

-- Réservations normales
INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(2001, 4, '2026-05-01', '08:10:00', 1, 'C003'),
(2002, 6, '2026-05-01', '08:15:00', 3, 'C004'),
(2003, 1, '2026-05-01', '08:20:00', 2, 'C005');

-- ============================================
-- CONFIGURATION TEMPS D'ATTENTE
-- ============================================
INSERT INTO configuration_attente (temps_attente_minutes, description, actif) VALUES
(30, 'Temps d attente par défaut pour regroupement', TRUE);