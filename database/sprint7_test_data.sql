-- ==============================================================================
-- SPRINT 7 - JEU DE DONNÉES COMPLET ET AUTONOME
-- ==============================================================================
-- Ce fichier contient TOUTES les données (structure et data) nécessaires pour 
-- tester la répartition intelligente des passagers (Sprint 7).
-- DATE DU SCÉNARIO DE TEST : 2026-05-01
-- ==============================================================================

-- 1. NETTOYAGE PRÉALABLE (Pour repartir d'une base propre pour ce scénario)
--    Attention : Supprime toutes les données existantes et remet les séquences à 1.
TRUNCATE TABLE
	assignation_vehicule,
	regroupement_reservation,
	regroupement,
	planification,
	reservation,
	vehicule,
	distance,
	lieu,
	hotel,
	type_carburant,
	configuration_attente,
	parametre
RESTART IDENTITY CASCADE;

-- Assurer la présence du champ d'heure de disponibilité véhicule
ALTER TABLE vehicule
	ADD COLUMN IF NOT EXISTS heure_disponibilite TIME NOT NULL DEFAULT '00:00:00';

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
INSERT INTO lieu (code, libelle) VALUES
('TNR', 'Ivato Aéroport'),
('COL', 'Colbert'),
('NOV', 'Novotel'),
('IBS', 'Ibis');

-- Distances entre l'aéroport (code TNR) et les lieux hôtels (codes COL/NOV/IBS)
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
((SELECT id FROM lieu WHERE code = 'TNR'), (SELECT id FROM lieu WHERE code = 'COL'), 90.0),
((SELECT id FROM lieu WHERE code = 'TNR'), (SELECT id FROM lieu WHERE code = 'NOV'), 35.0),
((SELECT id FROM lieu WHERE code = 'COL'), (SELECT id FROM lieu WHERE code = 'NOV'), 60.0);

-- ==============================================================================
-- 3. VÉHICULES SPÉCIFIQUES DU SCÉNARIO SPRINT 7
-- ==============================================================================
-- On a besoin de :
--  - V1 : 8 places (Diesel - Prioritaire)
--  - V2 : 3 places (Diesel)
--
INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES 
('V1-SPRINT1', 5, (SELECT id FROM type_carburant WHERE code = 'D'), 50.0, '00:00:00'),
('V2-SPRINT7', 5, (SELECT id FROM type_carburant WHERE code = 'Es'), 50.0, '00:00:00'),
('V3-SPRINT7', 12, (SELECT id FROM type_carburant WHERE code = 'D'), 50.0, '00:00:00'),
('V4-SPRINT7', 9, (SELECT id FROM type_carburant WHERE code = 'D'), 50.0, '00:00:00'),
('V5-SPRINT7', 12, (SELECT id FROM type_carburant WHERE code = 'Es'), 50.0, '13:00:00');

-- Update explicite: V5 ne devient disponible qu'à 13:00
UPDATE vehicule
SET heure_disponibilite = '13:00:00'
WHERE reference = 'V5-SPRINT7';

-- ==============================================================================
-- 4. RÉSERVATIONS DU SCÉNARIO SPRINT 7
-- ==============================================================================
-- Date : 2026-05-01
-- Heure : 08:00 (Toutes arrivent en même temps -> Même groupe)
-- Référence | Nb Pers | Hôtel
-- 7001      | 6       | Colbert
-- 7002      | 4       | Novotel
-- 7003      | 3       | Ibis
-- -----------------------------------
-- TOTAL     | 13 Personnes

INSERT INTO reservation (reference, nombre, date, heure, hotel) VALUES
(7001, 7, '2026-03-19', '09:00:00', (SELECT id FROM hotel WHERE libelle = 'Colbert')), 
(7002, 20, '2026-03-19', '08:00:00', (SELECT id FROM hotel WHERE libelle = 'Novotel')), 
(7003, 3, '2026-03-19', '09:10:00', (SELECT id FROM hotel WHERE libelle = 'Colbert')), 
(7004, 10, '2026-03-19', '09:15:00', (SELECT id FROM hotel WHERE libelle = 'Colbert')), 
(7005, 5, '2026-03-19', '09:20:00', (SELECT id FROM hotel WHERE libelle = 'Colbert')), 
(7006, 12, '2026-03-19', '13:30:00', (SELECT id FROM hotel WHERE libelle = 'Colbert')); 

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
