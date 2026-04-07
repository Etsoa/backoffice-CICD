-- ============================================
-- TEST2 TEST DATA - Multiple Test Scenarios
-- Base date: 2026-04-02
-- Extracted from test-2.ods
-- ============================================

-- ============================================
-- CONFIGURATION GLOBALE (PARTAGÉE)
-- ============================================

-- Types de carburant
INSERT INTO type_carburant (code, libelle) VALUES
('D', 'Diesel'),
('Es', 'Essence');

-- Clients
INSERT INTO client (id_client, nom, prenom, email) VALUES
('C001', 'Client', '1', 'client1@example.com'),
('C002', 'Client', '2', 'client2@example.com'),
('C003', 'Client', '3', 'client3@example.com'),
('C004', 'Client', '4', 'client4@example.com');

-- Lieux
INSERT INTO lieu (code, libelle) VALUES
('TNR', 'Aeroport Ivato'),
('H1', 'Hotel 1'),
('H2', 'Hotel 2');

-- ============================================
-- Hôtels
-- ============================================
INSERT INTO hotel (libelle) VALUES
('Aeroport Ivato'),
('Hotel 1'),
('Hotel 2');

-- Configuration du délai d'attente
INSERT INTO configuration_attente (temps_attente_minutes, description, actif) VALUES
(30, 'Délai d''attente par défaut pour regroupement', TRUE);

-- Distances (Common for all scenarios)
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
-- From Aeroport Ivato (TNR) to hotels
(1, 2, 90.00),      -- TNR to Hotel 1
(1, 3, 65.00),      -- TNR to Hotel 2
-- Between hotels
(2, 3, 10.00),      -- Hotel 1 to Hotel 2
(3, 2, 10.00),      -- Hotel 2 to Hotel 1
-- Return paths
(2, 1, 90.00),      -- Hotel 1 to TNR
(3, 1, 65.00);      -- Hotel 2 to TNR

-- ============================================
-- SCENARIO 1: 1 Vehicle, 4 Small Reservations
-- Date: 2026-04-02
-- Véhicule: V1 (20 places, dispo 00:00)
-- Réservations: C1-C4 (5 pax chacun, heures: 00:15, 00:20, 00:30, 00:35)
-- Attendu: 1 groupe, 1 véhicule, tous ensemble
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('V1', 10, 1, 60.00, '00:00:00'),
('V2', 8, 1, 60.00, '08:00:00'),
('V3', 8, 2, 60.00, '08:00:00'),
('V4', 12, 2, 60.00, '09:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(1001, 20, '2026-04-02', '06:00:00', 2, 'C001'),
(1002, 6, '2026-04-02', '08:15:00', 2, 'C002'),
(1003, 10, '2026-04-02', '09:00:00', 2, 'C003'),
(1004, 6, '2026-04-02', '09:10:00', 3, 'C004');

-- ============================================
-- SCENARIO 2: 2 Vehicles, 3 Reservations
-- Date: 2026-04-02
-- Véhicules: V1 (10 places, dispo 08:00), V2 (10 places, dispo 10:00)
-- Réservations: C1 (14 à 08:00), C2 (7 à 10:10), C3 (4 à 10:20)
-- Attendu: C1 split (10 V1 + 4 V2), C2 et C3 regroupés
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('V1_S2', 10, 1, 50.00, '08:00:00'),
('V2_S2', 10, 1, 50.00, '10:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(2001, 14, '2026-04-02', '08:00:00', 2, 'C001'),
(2002, 7, '2026-04-02', '10:10:00', 2, 'C002'),
(2003, 4, '2026-04-02', '10:20:00', 2, 'C003');

-- ============================================
-- SCENARIO 3: 3 Vehicles, 3 Reservations
-- Date: 2026-04-02
-- Véhicules: V1 (10 places, 08:00), V2 (10 places, 10:00), V3 (4 places, 10:10)
-- Réservations: C1 (14 à 08:00), C2 (7 à 10:10), C3 (4 à 10:20)
-- Attendu: C1 -> V1+V2, avec V3 disponible mais pas utilisé pour C1
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('V1_S3', 10, 1, 50.00, '08:00:00'),
('V2_S3', 10, 1, 50.00, '10:00:00'),
('V3_S3', 4, 1, 50.00, '10:10:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(3001, 14, '2026-04-02', '08:00:00', 2, 'C001'),
(3002, 7, '2026-04-02', '10:10:00', 2, 'C002'),
(3003, 4, '2026-04-02', '10:20:00', 2, 'C003');

-- ============================================
-- SCENARIO 4: 4 Vehicles with Fuel Types, 1 Large Reservation
-- Date: 2026-04-02
-- Véhicules: V1 (10, 07:00 D), V2 (10, 10:00 ES), V3 (4, 10:00 D), V4 (10, 10:00 D)
-- Réservation: C1 (20 à 08:00)
-- Attendu: C1 -> V1 (10) + V4 (10) with Diesel priority
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('V1_S4', 10, 1, 50.00, '07:00:00'),
('V2_S4', 10, 2, 50.00, '10:00:00'),
('V3_S4', 4, 1, 50.00, '10:00:00'),
('V4_S4', 10, 1, 50.00, '10:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(4001, 20, '2026-04-02', '08:00:00', 2, 'C001');

-- ============================================
-- SCENARIO 5: 4 Vehicles with Mixed Fuel, 4 Reservations
-- Date: 2026-04-02
-- Véhicules: V1 (10, 07:00 ES), V2 (10, 10:00 ES), V3 (4, 10:00 D), V4 (10, 10:00 D)
-- Réservations: C1 (15 à 08:00), C2 (5 à 10:10), C3 (4 à 10:00), C4 (10 à 08:00)
-- Attendu: Complex assignment with fuel type consideration
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('V1_S5', 10, 2, 50.00, '07:00:00'),
('V2_S5', 10, 2, 50.00, '10:00:00'),
('V3_S5', 4, 1, 50.00, '10:00:00'),
('V4_S5', 10, 1, 50.00, '10:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(5001, 15, '2026-04-02', '08:00:00', 2, 'C001'),
(5002, 5, '2026-04-02', '10:10:00', 2, 'C002'),
(5003, 4, '2026-04-02', '10:00:00', 2, 'C003'),
(5004, 10, '2026-04-02', '08:00:00', 2, 'C004');

-- ============================================
-- SCENARIO 6: Waiting Time Calculation Test
-- Date: 2026-04-02
-- Véhicule: V1 (20 places, dispo 08:00)
-- Réservations: C1 (10 à 08:20), C2 (10 à 08:40)
-- Attendu: Test if C1 and C2 groupe together considering 30min waiting time
-- (08:20 + 30min = 08:50, so C2 at 08:40 should be included in first groupe)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('V1_S6', 20, 1, 50.00, '08:00:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(6001, 10, '2026-04-02', '08:20:00', 2, 'C001'),
(6002, 10, '2026-04-02', '08:40:00', 2, 'C002');

-- ============================================
-- SCENARIO 7: Tony Final Test (Real Use Case)
-- Date: 2026-03-25
-- Véhicules: V1 (12 places, Diesel, dispo 11:45), V2 (14 places, Diesel, dispo 12:14)
-- Réservations: 
--   C1 (7 à 08:00), C2 (10 à 12:00), C3 (7 à 12:10), C4 (3 à 12:20)
-- Attendu: 
--   Groupe 1 (08:00): V1 -> C1 (7 pax)
--   Groupe 2 (12:20): V2 -> C2 (10 pax), V1 -> C3 (2 pax - split de 7)
--   Groupe 3 (14:10): V1 -> C4 (3 pax), C3 reste (5 pax)
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('Vehicule1_Tony', 12, 1, 50.00, '11:45:00'),
('Vehicule2_Tony', 14, 1, 50.00, '12:14:00');

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(7001, 7, '2026-03-25', '08:00:00', 2, 'C001'),
(7002, 10, '2026-03-25', '12:00:00', 2, 'C002'),
(7003, 7, '2026-03-25', '12:10:00', 2, 'C003'),
(7004, 3, '2026-03-25', '12:20:00', 2, 'C004');

-- ============================================
-- NOTES SUR L'UTILISATION
-- ============================================
-- Charger ces données: 
-- psql -U user -d cicd -f test2.sql
--
-- Chaque scénario utilise des références de réservation uniques:
--   S1: réf 1001-1004
--   S2: réf 2001-2003
--   S3: réf 3001-3003
--   S4: réf 4001
--   S5: réf 5001-5004
--   S6: réf 6001-6002
--   S7 (Tony): réf 7001-7004 (date 2026-03-25)
--
-- Les véhicules sont dédiés par scénario (noms: V*_S*) pour éviter interférences
--
-- Objectifs de test:
--   S1: Groupe simple, tous à la fois
--   S2: Split et regroupement de réservations
--   S3: Choix de véhicule optimisé
--   S4: Priorité type carburant (Diesel > Essence)
--   S5: Assignment complexe multi-véhicules
--   S6: Validation temps d'attente et regroupement
--   S7 (Tony): Cas réel complexe avec splits et multi-groupes
