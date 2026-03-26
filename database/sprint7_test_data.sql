-- ============================================
-- SPRINT 7 - TEST DATA
-- Data adapted from chad folder for database.sql structure
-- ============================================

-- ============================================
-- Types de carburant
-- ============================================
INSERT INTO type_carburant (code, libelle) VALUES
('D', 'Diesel'),
('Es', 'Essence'),
('H', 'Hybride'),
('El', 'Electrique');

-- ============================================
-- Clients
-- ============================================
INSERT INTO client (id_client, nom, prenom, email) VALUES
('C001', 'Dupont', 'Jean', 'jean.dupont@example.com'),
('C002', 'Martin', 'Marie', 'marie.martin@example.com'),
('C003', 'Bernard', 'Pierre', 'pierre.bernard@example.com'),
('C004', 'Dubois', 'Luc', 'luc.dubois@example.com'),
('C005', 'Moreau', 'Sophie', 'sophie.moreau@example.com'),
('C006', 'Laurent', 'Claire', 'claire.laurent@example.com');

-- ============================================
-- Lieux (Includes Aeroport and Hotels)
-- ============================================
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

-- ============================================
-- Véhicules
-- ============================================
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('vehicule1', 5, 1, 60.00, '09:00:00'),    -- Diesel, disponible à 09:00
('vehicule2', 5, 2, 60.00, '09:00:00'),    -- Essence, disponible à 09:00
('vehicule3', 12, 1, 60.00, '07:00:00'),   -- Diesel, disponible à 07:00
('vehicule4', 9, 1, 60.00, '09:00:00'),    -- Diesel, disponible à 09:00
('vehicule5', 12, 2, 60.00, '13:00:00');   -- Essence, disponible à 13:00

-- ============================================
-- Distances entre lieux
-- ============================================
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
-- From Aeroport Ivato (TNR) to hotels
(1, 2, 90.00),      -- TNR to Hotel 1
(1, 3, 35.00),      -- TNR to Hotel 2
-- Between hotels
(2, 3, 60.00),      -- Hotel 1 to Hotel 2
(3, 2, 60.00),      -- Hotel 2 to Hotel 1
-- Return paths
(2, 1, 90.00),      -- Hotel 1 to TNR
(3, 1, 35.00);      -- Hotel 2 to TNR

-- ============================================
-- Réservations (2026-03-19)
-- ============================================
INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(1, 7, '2026-03-19', '09:00:00', 2, 'C001'),
(2, 20, '2026-03-19', '08:00:00', 3, 'C002'),
(3, 3, '2026-03-19', '09:10:00', 2, 'C003'),
(4, 10, '2026-03-19', '09:15:00', 2, 'C004'),
(5, 5, '2026-03-19', '09:20:00', 2, 'C005'),
(6, 12, '2026-03-19', '13:30:00', 2, 'C006');

-- ============================================
-- Configuration du délai d'attente (Sprint 5)
-- ============================================
INSERT INTO configuration_attente (temps_attente_minutes, description, actif) VALUES
(30, 'Délai d''attente par défaut pour regroupement', TRUE);

-- ============================================
-- Paramètres globaux
-- ============================================
INSERT INTO parametre (cle, valeur, description) VALUES
('delai_attente', '30', 'Délai d''attente en minutes pour le regroupement des réservations'),
('vitesse_moyenne', '60', 'Vitesse moyenne des véhicules en km/h'),
('temps_service_hotel', '30', 'Temps de service par hôtel en minutes');
