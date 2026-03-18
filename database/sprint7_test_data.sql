-- ==============================================================================
-- SPRINT 7 - JEU DE DONNÉES COMPLET ET AUTONOME
-- ==============================================================================
-- Ce fichier contient TOUTES les données (structure et data) nécessaires pour 
-- tester la répartition intelligente des passagers (Sprint 7).
-- DATE DU SCÉNARIO DE TEST : 2026-05-01
-- ==============================================================================

-- 1. NETTOYAGE PRÉALABLE (Pour repartir d'une base propre pour ce scénario)
--    Attention : Supprime toutes les données existantes.
DELETE FROM assignation_vehicule;
DELETE FROM regroupement_reservation;
DELETE FROM regroupement;
DELETE FROM planification;
DELETE FROM reservation;
DELETE FROM vehicule;
DELETE FROM distance;
DELETE FROM lieu;
DELETE FROM hotel;
DELETE FROM type_carburant;
DELETE FROM configuration_attente;
DELETE FROM parametre;

-- ==============================================================================
-- 2. CONFIGURATION ET RÉFÉRENTIELS
-- ==============================================================================

-- Configuration du délai d'attente (30 min pour regrouper R1, R2, R3)
INSERT INTO configuration_attente (temps_attente_minutes, description, actif) VALUES
(30, 'Standard Sprint 7', TRUE);

INSERT INTO parametre (cle, valeur, description) VALUES 
('delai_attente', '30', 'Délai par défaut'),
('carburant_prioritaire', 'D', 'Diesel prioritaire');

-- Types de carburant (Diesel, Essence)
INSERT INTO type_carburant (code, libelle) VALUES
('D', 'Diesel'),
('Es', 'Essence');

-- Hôtels (Destinations possibles)
INSERT INTO hotel (libelle) VALUES
('Colbert'),
('Novotel'),
('Ibis');

-- Lieux (Aéroport + Localisation Hôtels)
-- Note: Les IDs sont auto-incrémentés, on suppose ici ordre d'insertion 1, 2, 3...
INSERT INTO lieu (code, libelle) VALUES
('TNR', 'Ivato Aéroport'),  -- 1
('COL', 'Colbert'),         -- 2
('NOV', 'Novotel'),         -- 3
('IBS', 'Ibis');            -- 4

-- Distances entre Aéroport (1) et Hôtels (2,3,4)
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
(1, 2, 15.0), -- TNR -> Colbert (15km)
(1, 3, 12.0), -- TNR -> Novotel (12km)
(1, 4, 10.0); -- TNR -> Ibis (10km)

-- ==============================================================================
-- 3. VÉHICULES SPÉCIFIQUES DU SCÉNARIO SPRINT 7
-- ==============================================================================
-- On a besoin de :
--  - V1 : 8 places (Diesel - Prioritaire)
--  - V2 : 3 places (Diesel)
--
-- Les IDs de type_carburant dépendent de l'ordre d'insertion ci-dessus.
-- 'D' est le premier inséré -> ID 1.

INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne) VALUES 
('V1-SPRINT7', 8, 1, 60.0), -- V1 : 8 pl, Diesel (ID 1)
('V2-SPRINT7', 3, 1, 60.0); -- V2 : 3 pl, Diesel (ID 1)

-- ==============================================================================
-- 4. RÉSERVATIONS DU SCÉNARIO SPRINT 7
-- ==============================================================================
-- Date : 2026-05-01
-- Heure : 08:00 (Toutes arrivent en même temps -> Même groupe)
-- Référence | Nb Pers | Hôtel
-- 7001      | 6       | Colbert (ID 1)
-- 7002      | 4       | Novotel (ID 2)
-- 7003      | 3       | Ibis (ID 3)
-- -----------------------------------
-- TOTAL     | 13 Personnes

INSERT INTO reservation (reference, nombre, date, heure, hotel) VALUES
(7001, 6, '2026-05-01', '08:00:00', 1), -- R1
(7002, 4, '2026-05-01', '08:00:00', 2), -- R2
(7003, 3, '2026-05-01', '08:00:00', 3); -- R3

-- ==============================================================================
-- VÉRIFICATION DU RÉSULTAT ATTENDU
-- ==============================================================================
-- Après execution de la plannification pour le 2026-05-01 :
--
-- [V1-SPRINT7] (Capacité 8) :
--   - Chargé à : 8 / 8 (PLEIN)
--   - Contient : R1 (6 pers) + Partie de R2 (2 pers)
--
-- [V2-SPRINT7] (Capacité 3) :
--   - Chargé à : 3 / 3 (PLEIN)
--   - Contient : Reste de R2 (2 pers) + Partie de R3 (1 pers)
--
-- [NON ASSIGNÉ] :
--   - Reste de R3 (2 pers)
--   - Devrait être reporté au prochain créneau (08:30 si délai 30min).
--
-- ==============================================================================
