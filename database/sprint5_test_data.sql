-- ============================================================================
-- Sprint 5 : Jeu de données de test
-- Gestion du temps d'attente et regroupement des réservations
-- ============================================================================
-- Ce fichier contient des données de test pour démontrer :
-- 1. Le regroupement des réservations par intervalle de temps
-- 2. L'attribution des véhicules (Diesel > Essence > Random)
-- 3. Le départ synchronisé pour TOUS les véhicules d'un même groupe
-- 4. Un groupe peut avoir PLUSIEURS véhicules si capacité insuffisante
-- ============================================================================

\c cicd

-- Nettoyer les données de test existantes
DELETE FROM reservation WHERE reference >= 5000;

-- ============================================================================
-- SCÉNARIO PRINCIPAL : Date 2026-03-18
-- Test avec un groupe nécessitant PLUSIEURS véhicules
-- ============================================================================
-- 
-- Véhicules disponibles (par priorité) :
-- VH-2026-001 : 4 places, Diesel
-- VH-2026-002 : 8 places, Diesel  
-- VH-2026-003 : 5 places, Essence
-- VH-2026-004 : 12 places, Hybride
-- VH-2026-005 : 3 places, Electrique
--
-- RÉSULTAT ATTENDU :
-- 
-- Groupe 1 (07:00-07:30) : 15 personnes total -> NÉCESSITE 2 VÉHICULES
--   - Réservations : 8001 (6 pers, 07:00), 8002 (5 pers, 07:10), 8003 (4 pers, 07:20)
--   - Total : 15 personnes
--   - Heure de départ COMMUNE : 07:20 (dernière résa)
--   - Véhicule 1 : VH-2026-004 (12 pl, Hybride) avec 11 pers (8001+8002)
--   - Véhicule 2 : VH-2026-001 (4 pl, Diesel) avec 4 pers (8003)
--   NOTE: Les 2 véhicules partent à 07:20 !
--
-- Groupe 2 (09:00-09:30) : 5 personnes -> 1 véhicule suffit
--   - Réservations : 8004 (3 pers, 09:00), 8005 (2 pers, 09:15)
--   - Total : 5 personnes
--   - Heure de départ : 09:15
--   - Véhicule : VH-2026-003 (5 pl, Essence)
--
-- Groupe 3 (11:00-11:30) : 7 personnes -> 1 véhicule suffit
--   - Réservations : 8006 (4 pers, 11:00), 8007 (3 pers, 11:25)
--   - Total : 7 personnes
--   - Heure de départ : 11:25
--   - Véhicule : VH-2026-002 (8 pl, Diesel)

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
-- Groupe 1 : 15 personnes -> 2 véhicules, départ commun 07:20
(8001, 6, '2026-03-18', '07:00', 1, 'client8001'),   -- 6 pers, Colbert
(8002, 5, '2026-03-18', '07:10', 2, 'client8002'),   -- 5 pers, Novotel
(8003, 4, '2026-03-18', '07:20', 3, 'client8003'),   -- 4 pers, Ibis

-- Groupe 2 : 5 personnes -> 1 véhicule
(8004, 3, '2026-03-18', '09:00', 4, 'client8004'),   -- 3 pers, Lokanga
(8005, 2, '2026-03-18', '09:15', 1, 'client8005'),   -- 2 pers, Colbert

-- Groupe 3 : 7 personnes -> 1 véhicule
(8006, 4, '2026-03-18', '11:00', 2, 'client8006'),   -- 4 pers, Novotel
(8007, 3, '2026-03-18', '11:25', 3, 'client8007');   -- 3 pers, Ibis

-- ============================================================================
-- SCÉNARIO 2 : Date 2026-03-19
-- Test avec un seul groupe TRÈS chargé (25 personnes -> 2 véhicules minimum)
-- ============================================================================
-- Le plus gros véhicule = 18 places, donc 25 personnes = OBLIGÉ d'avoir 2 véhicules
-- Les 2 véhicules partent à la même heure !

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(9001, 10, '2026-03-19', '08:00', 1, 'client9001'),   -- 10 pers, Colbert
(9002, 8, '2026-03-19', '08:15', 2, 'client9002'),    -- 8 pers, Novotel
(9003, 7, '2026-03-19', '08:25', 3, 'client9003');    -- 7 pers, Ibis
-- Total : 25 personnes dans l'intervalle 08:00-08:30
-- Départ commun : 08:25
-- Véhicule 1 : VH-2026-006 (18 pl) avec 18 pers (9001 + 9002)
-- Véhicule 2 : VH-2026-002 (8 pl) avec 7 pers (9003)
-- LES 2 PARTENT À 08:25 !

-- ============================================================================
-- SCÉNARIO 3 : Date 2026-03-20
-- Test avec 2 groupes, dont un avec 3 véhicules (30+ personnes)
-- ============================================================================

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
-- Groupe 1 (07:00-07:30) : 35 personnes -> 3 véhicules !
(9101, 12, '2026-03-20', '07:00', 1, 'client9101'),   -- 12 pers, Colbert
(9102, 11, '2026-03-20', '07:10', 2, 'client9102'),   -- 11 pers, Novotel
(9103, 8, '2026-03-20', '07:20', 3, 'client9103'),    -- 8 pers, Ibis
(9104, 4, '2026-03-20', '07:25', 4, 'client9104'),    -- 4 pers, Lokanga
-- Total : 35 personnes
-- Véhicule 1 : VH-2026-006 (18 pl)
-- Véhicule 2 : VH-2026-004 (12 pl)
-- Véhicule 3 : VH-2026-002 (8 pl)
-- TOUS PARTENT À 07:25 !

-- Groupe 2 (10:00-10:30) : 6 personnes -> 1 véhicule
(9105, 3, '2026-03-20', '10:00', 1, 'client9105'),    -- 3 pers, Colbert
(9106, 3, '2026-03-20', '10:20', 2, 'client9106');    -- 3 pers, Novotel


--  testez avec 2026-03-19 ou 2026-03-20 pour voir plusieurs véhicules dans le même groupe, tous partant à la même heure
INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(9001, 10, '2026-03-19', '08:00', 1, 'client9001'),
(9002, 8, '2026-03-19', '08:15', 2, 'client9002'),
(9003, 7, '2026-03-19', '08:25', 3, 'client9003'),
(9101, 12, '2026-03-20', '07:00', 1, 'client9101'),
(9102, 11, '2026-03-20', '07:10', 2, 'client9102'),
(9103, 8, '2026-03-20', '07:20', 3, 'client9103'),
(9104, 4, '2026-03-20', '07:25', 4, 'client9104'),
(9105, 3, '2026-03-20', '10:00', 1, 'client9105'),
(9106, 3, '2026-03-20', '10:20', 2, 'client9106');
-- ============================================================================
-- Configuration du temps d'attente
-- ============================================================================

-- S'assurer que la configuration existe
INSERT INTO configuration_attente (temps_attente_minutes, description, actif) 
VALUES (30, 'Délai d''attente par défaut pour regroupement', true)
ON CONFLICT DO NOTHING;

UPDATE configuration_attente SET temps_attente_minutes = 30 WHERE actif = TRUE;
