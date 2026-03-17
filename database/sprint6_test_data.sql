-- ============================================
-- SPRINT 6 - DONNÉES DE TEST COMPLÈTES ET INDÉPENDANTES
-- Fichier autonome - pas besoin de database.sql
-- ============================================

-- ============================================
-- DONNÉES DE BASE - Types de carburant
-- ============================================

INSERT INTO type_carburant (code, libelle) VALUES
('D', 'Diesel'),
('Es', 'Essence'),
('H', 'Hybride'),
('El', 'Electrique');

-- ============================================
-- DONNÉES DE BASE - Hôtels
-- ============================================

INSERT INTO hotel (libelle) VALUES
('Colbert'),
('Novotel'),
('Ibis'),
('Lokanga');

-- ============================================
-- DONNÉES DE BASE - Lieux (aéroport et destinations)
-- ============================================

INSERT INTO lieu (code, libelle) VALUES
('TNR', 'Ivato'),          -- Aéroport
('COL', 'Colbert'),
('NOV', 'Novotel'),
('IBL', 'Ibis'),
('LOK', 'Lokanga');

-- ============================================
-- DONNÉES DE BASE - Distances
-- ============================================

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

-- ============================================
-- DONNÉES DE BASE - Véhicules
-- ============================================

INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne) VALUES
('VH-2026-001', 4, 1, 60.00),   -- 4 places, Diesel
('VH-2026-002', 8, 1, 55.00),   -- 8 places, Diesel
('VH-2026-003', 5, 2, 65.00),   -- 5 places, Essence
('VH-2026-004', 12, 3, 50.00),  -- 12 places, Hybride
('VH-2026-005', 3, 4, 70.00),   -- 3 places, Electrique
('VH-2026-006', 18, 1, 50.00);  -- 18 places, Diesel

-- ============================================
-- 1. CLIENTS - Quelques profils
-- ============================================

INSERT INTO client (id_client, nom, prenom, email) VALUES
('C001', 'Dupont', 'Jean', 'jean.dupont@email.com'),
('C002', 'Martin', 'Marie', 'marie.martin@email.com'),
('C003', 'Bernard', 'Pierre', 'pierre.bernard@email.com'),
('C004', 'Durand', 'Sophie', 'sophie.durand@email.com'),
('C005', 'Petit', 'Luc', NULL);

-- ============================================
-- 2. RÉSERVATIONS - Scénarios essentiels (10 réservations)
-- ============================================

INSERT INTO reservation (reference, nombre, date, heure, hotel) VALUES
-- Groupement simple (fenêtre 30 min)
(1001, 2, '2026-03-20', '07:00', 1),   -- 2 pers, Colbert
(1002, 3, '2026-03-20', '07:15', 2),   -- 3 pers, Novotel (fenêtre)
(1003, 2, '2026-03-20', '07:25', 3),   -- 2 pers, Ibis (fenêtre)
-- Hors fenêtre
(1004, 4, '2026-03-20', '08:00', 4),   -- 4 pers, Lokanga (nouveau groupe)
-- Dépassement capacité
(2001, 5, '2026-03-21', '08:00', 1),   -- 5 pers
(2002, 4, '2026-03-21', '08:10', 2),   -- 4 pers (5+4=9 > 8 places)
-- Gros groupe
(3001, 12, '2026-03-22', '09:00', 1),  -- 12 pers (VH-2026-004 ou 6)
-- Heures décalées
(4001, 2, '2026-03-23', '06:00', 1),   -- Groupe seul
(4002, 3, '2026-03-23', '12:00', 2),   -- Autre groupe
-- Petite réservation
(5001, 1, '2026-03-24', '10:00', 3);   -- 1 pers seul

-- ============================================
-- 3. TOKENS - Quelques exemples
-- ============================================

INSERT INTO token (valeur_token, date_expiration) VALUES
('TOKEN001VALID', NOW() + INTERVAL '30 days'),
('TOKEN002VALID', NOW() + INTERVAL '7 days'),
('TOKEN003EXPIR', NOW() - INTERVAL '1 day'),
('TOKEN004NEVER', NULL);

-- ============================================
-- 4. PARAMÈTRES - Configuration standard
-- ============================================

INSERT INTO parametre (cle, valeur, description) VALUES
('delai_attente', '30', 'Délai pour regroupement des réservations (minutes)'),
('vitesse_moyenne_defaut', '60', 'Vitesse moyenne par défaut (km/h)'),
('carburant_prioritaire', 'D', 'Carburant prioritaire (D=Diesel)');

-- ============================================
-- 5. CONFIGURATION_ATTENTE
-- ============================================

INSERT INTO configuration_attente (temps_attente_minutes, description, actif) VALUES
(30, 'Configuration standard - 30 minutes', TRUE);

-- ============================================
-- 6. PLANIFICATIONS SAUVEGARDÉES
-- ============================================

-- Une planification simple pour test
INSERT INTO planification (date_planification, statut, delai_attente_utilise,
                           nombre_regroupements, nombre_reservations_total,
                           nombre_reservations_assignees, nombre_vehicules_utilises, actif) VALUES
('2026-03-20', 'ACTIVE', 30, 2, 4, 4, 2, TRUE);

-- ============================================
-- 7. REGROUPEMENTS
-- ============================================

INSERT INTO regroupement (planification_id, numero_regroupement, heure_depart_groupe,
                          nombre_reservations, nombre_passagers_total, nombre_vehicules_assignes) VALUES
(1, 1, '07:25', 3, 7, 1),    -- Groupe 1: réservations 1001-1003
(1, 2, '08:00', 1, 4, 1);    -- Groupe 2: réservation 1004

-- ============================================
-- 8. REGROUPEMENT_RESERVATION - Mappings
-- ============================================

INSERT INTO regroupement_reservation (regroupement_id, reservation_id) VALUES
(1, 1), (1, 2), (1, 3),   -- Groupe 1
(2, 4);                     -- Groupe 2

-- ============================================
-- 9. ASSIGNATION_VEHICULE - Véhicules assignés
-- ============================================

INSERT INTO assignation_vehicule (regroupement_id, vehicule_id, numero_ordre_groupe,
                                  nombre_trajet_effectues, heure_depart_aeroport, heure_retour_aeroport,
                                  distance_totale_km, temps_total_minutes, nombre_passagers_transportes) VALUES
(1, 3, 1, 1, '07:00', '08:15', 35.6, 75, 7),
(2, 1, 1, 1, '08:00', '08:50', 37.0, 50, 4);

-- ============================================
-- 10. ITINERAIRE_ARRET - Détails des trajets
-- ============================================

-- Itinéraire groupe 1 (VH-2026-003)
INSERT INTO itineraire_arret (assignation_vehicule_id, ordre_arret, lieu_id, hotel_id,
                              heure_arrivee, heure_depart, nombre_passagers_embarques, distance_depuis_prev_km) VALUES
(1, 1, 1, NULL, '07:00', '07:05', 0, 0),
(1, 2, 2, 1, '07:23', '07:28', 2, 18.5),
(1, 3, 3, 2, '07:31', '07:36', 5, 3.5),
(1, 4, 4, 3, '07:38', '07:43', 7, 2.0),
(1, 5, 1, NULL, '08:00', '08:15', 7, 17.8);

-- Itinéraire groupe 2 (VH-2026-001)
INSERT INTO itineraire_arret (assignation_vehicule_id, ordre_arret, lieu_id, hotel_id,
                              heure_arrivee, heure_depart, nombre_passagers_embarques, distance_depuis_prev_km) VALUES
(2, 1, 1, NULL, '08:00', '08:05', 0, 0),
(2, 2, 5, 4, '08:24', '08:30', 4, 19.3),
(2, 3, 1, NULL, '08:50', '08:50', 4, 19.3);

-- ============================================
-- 11. SUIVI_TRAJET_VEHICULE - Analytics
-- ============================================

INSERT INTO suivi_trajet_vehicule (planification_id, vehicule_id, date_planification,
                                   nombre_regroupements_assignes, nombre_passagers_total,
                                   distance_totale_km, temps_total_heures,
                                   heure_premiere_utilisation, heure_derniere_retour) VALUES
(1, 3, '2026-03-20', 1, 7, 35.6, 1.25, '07:00', '08:15'),
(1, 1, '2026-03-20', 1, 4, 37.0, 0.83, '08:00', '08:50');

-- ============================================
-- FIN DES DONNÉES DE TEST SPRINT 6
-- ============================================
-- Résumé: 
-- ✓ 5 clients
-- ✓ 10 réservations (scénarios clés)
-- ✓ 4 tokens
-- ✓ 3 paramètres essentiels
-- ✓ 1 planification avec 2 regroupements
-- ✓ 2 assignations véhicules
-- ✓ Itinéraires complets
-- ✓ Suivi de trajet pour analytics
-- ============================================
