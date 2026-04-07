-- ============================================
-- TONY TEST DATA - Multiple Test Scenarios
-- Base date: 2026-03-25
-- ============================================

-- ============================================
-- CONFIGURATION GLOBALE (PARTAGÉE)
-- ============================================

-- Types de carburant
INSERT INTO type_carburant (code, libelle) VALUES
('D', 'Diesel'),
('Es', 'Essence'),
('H', 'Hybride'),
('El', 'Electrique');

-- Clients
INSERT INTO client (id_client, nom, prenom, email) VALUES
('C001', 'Client', '1', 'client1@example.com'),
('C002', 'Client', '2', 'client2@example.com'),
('C003', 'Client', '3', 'client3@example.com'),
('C004', 'Client', '4', 'client4@example.com'),
('C005', 'Client', '5', 'client5@example.com'),
('C006', 'Client', '6', 'client6@example.com');

-- Lieux
INSERT INTO lieu (code, libelle) VALUES
('TNR', 'Aéroport'),
('H1', 'Hotel1'),
('H2', 'Hotel2');

-- Hôtels
INSERT INTO hotel (libelle) VALUES
('Aéroport Ivato'),
('Hotel1'),
('Hotel2');

-- Configuration du délai d'attente
INSERT INTO configuration_attente (temps_attente_minutes, description, actif) VALUES
(30, 'Délai d''attente par défaut pour regroupement', TRUE);

-- distances
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
(1, 2, 50.00),      -- Aéroport to Hotel1
(2, 1, 50.00);      -- Hotel1 to Aéroport

-- ============================================
-- SCENARIO 1: 3 Clients, 1 Vehicle (Simple)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('Vehicule_S1_V1', 12, 1, 50.00, '10:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(101, 10, '2026-03-25', '08:00:00', 2, 'C001'),
(102, 15, '2026-03-25', '10:10:00', 2, 'C002'),
(103, 8, '2026-03-25', '10:15:00', 2, 'C003');

-- ============================================
-- SCENARIO 2: 6 Clients, 2 Vehicles (Complex)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('Vehicule_S2_V1', 12, 1, 50.00, '00:00:00'),
('Vehicule_S2_V2', 13, 1, 50.00, '00:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(201, 10, '2026-03-25', '08:00:00', 2, 'C001'),
(202, 15, '2026-03-25', '08:10:00', 2, 'C002'),
(203, 8, '2026-03-25', '08:15:00', 2, 'C003'),
(204, 5, '2026-03-25', '10:45:00', 2, 'C004'),
(205, 13, '2026-03-25', '11:00:00', 2, 'C005'),
(206, 13, '2026-03-25', '11:00:00', 2, 'C006');

-- ============================================
-- SCENARIO 3: 3 Clients, 1 Vehicle (Later start)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('Vehicule_S3_V1', 12, 1, 50.00, '10:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(301, 10, '2026-03-25', '10:10:00', 2, 'C001'),
(302, 8, '2026-03-25', '10:15:00', 2, 'C002'),
(303, 15, '2026-03-25', '10:20:00', 2, 'C003');

-- ============================================
-- SCENARIO 4: 4 Clients, 1 Vehicle (Early morning)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('Vehicule_S4_V1', 10, 1, 50.00, '00:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(401, 11, '2026-03-25', '08:00:00', 2, 'C001'),
(402, 1, '2026-03-25', '10:10:00', 2, 'C002'),
(403, 1, '2026-03-25', '10:15:00', 2, 'C003'),
(404, 1, '2026-03-25', '10:35:00', 2, 'C004');

-- ============================================
-- SCENARIO 5: 4 Clients, 1 Vehicle (Different times)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('Vehicule_S5_V1', 10, 1, 50.00, '00:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(501, 10, '2026-03-25', '08:00:00', 2, 'C001'),
(502, 1, '2026-03-25', '10:10:00', 2, 'C002'),
(503, 1, '2026-03-25', '10:15:00', 2, 'C003'),
(504, 1, '2026-03-25', '10:35:00', 2, 'C004');

-- ============================================
-- SCENARIO 6: 4 Clients, 1 Vehicle (With gaps)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('Vehicule_S6_V1', 10, 1, 50.00, '00:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(601, 11, '2026-03-25', '08:00:00', 2, 'C001'),
(602, 1, '2026-03-25', '09:45:00', 2, 'C002'),
(603, 1, '2026-03-25', '09:50:00', 2, 'C003'),
(604, 1, '2026-03-25', '10:30:00', 2, 'C004');

-- ============================================
-- SCENARIO 7: 4 Clients, 1 Vehicle (Simple intervals)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('Vehicule_S7_V1', 10, 1, 50.00, '00:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(701, 11, '2026-03-25', '08:00:00', 2, 'C001'),
(702, 1, '2026-03-25', '09:45:00', 2, 'C002'),
(703, 1, '2026-03-25', '09:50:00', 2, 'C003'),
(704, 1, '2026-03-25', '10:30:00', 2, 'C004');

-- ============================================
-- NOTES SUR L'UTILISATION
-- ============================================
-- Charger ces données: 
-- psql -U user -d cicd -f tony.sql
--
-- Chaque scénario utilise des références de réservation uniques:
--   S1: réf 101-103
--   S2: réf 201-206
--   S3: réf 301-303
--   S4: réf 401-404
--   S5: réf 501-504
--   S6: réf 601-604
--   S7: réf 701-704
--
-- Les véhicules sont dédiés par scénario (pas d'interférence)
--
-- Résultats attendus:
--   S1: 3 clients simple → 1 groupe, 1 véhicule
--   S2: 6 clients complexe → multiple groupes/vehicles
--   S3-S7: Variations pour différents cas d'usage
