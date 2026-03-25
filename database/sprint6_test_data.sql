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
('VH-2026-006', 18, 1, 50.00),  -- 18 places, Diesel
('VH-2026-007', 3, 1, 60.00);   -- 3 places, Diesel (same capacity as VH-005)

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
-- 2. RÉSERVATIONS - Scénarios essentiels (17 réservations)
-- ============================================
-- 20/03: Groupement simple (fenêtre 30 min) - 1001-1003, puis hors fenêtre - 1004
-- 21/03: Dépassement capacité - 2001-2002 (5 + 4 = 9 > 8)
-- 22/03: Gros groupe - 3001 (12 pers)
-- 23/03: Heures décalées - 4001 (06:00), 4002 (12:00)
-- 24/03: Petite réservation - 5001 (1 pers)
-- 25/03: Réservations reportées + réutilisation véhicule - 6001-6004
-- 26/03: Nombre de trajets - réutilisation multi-groupe - 7001-7003

INSERT INTO reservation (reference, nombre, date, heure, hotel, client) VALUES
(1001, 2, '2026-03-20', '07:00', 1, 'client1001'),
(1002, 3, '2026-03-20', '07:15', 2, 'client1002'),
(1003, 2, '2026-03-20', '07:25', 3, 'client1003'),
(1004, 4, '2026-03-20', '08:00', 4, 'client1004'),
(2001, 5, '2026-03-21', '08:00', 1, 'client2001'),
(2002, 4, '2026-03-21', '08:10', 2, 'client2002'),
(3001, 12, '2026-03-22', '09:00', 1, 'client3001'),
(4001, 2, '2026-03-23', '06:00', 1, 'client4001'),
(4002, 3, '2026-03-23', '12:00', 2, 'client4002'),
(5001, 1, '2026-03-24', '10:00', 3, 'client5001'),
(6001, 15, '2026-03-25', '09:10', 1, 'client6001'),
(6002, 18, '2026-03-25', '09:00', 2, 'client6002'),
(6003, 3, '2026-03-25', '10:30', 3, 'client6003'),
(6004, 4, '2026-03-25', '10:40', 4, 'client6004'),
(7001, 3, '2026-03-26', '09:00', 1, 'client7001'),
(7002, 3, '2026-03-26', '10:00', 2, 'client7002'),
(7003, 2, '2026-03-26', '11:00', 3, 'client7003');

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
-- FIN DES DONNÉES DE TEST SPRINT 6
-- ============================================
-- Résumé: 
-- ✓ 5 clients
-- ✓ 17 réservations (scénarios clés - généreront des planifications auto)
--   - 20/03: 4 réservations (2 groupes simples)
--   - 21/03: 2 réservations (1 groupe avec dépassement capacité)
--   - 22/03: 1 réservation (gros groupe)
--   - 23/03: 2 réservations (heures décalées)
--   - 24/03: 1 réservation (simple)
--   - 25/03: 4 réservations (réservations REPORTÉES + réutilisation véhicule)
--   - 26/03: 3 réservations (nombre de trajets - comparaison VH-005 vs VH-007)
-- ✓ 7 véhicules (dont VH-007 avec même capacité que VH-005 pour tester critères)
-- ✓ 4 tokens
-- ✓ 3 paramètres essentiels + 1 configuration_attente
-- 
-- NOTE IMPORTANTE:
-- ✓ Les planifications, regroupements, assignations et itinéraires
--   sont GÉNÉRÉS AUTOMATIQUEMENT par le code via genererRegroupements()
--   et sauvegarderPlanification(). Ne pas les insérer manuellement!
-- ✓ Appeler genererOuGenerer(Date) pour générer la planification du jour
-- 
-- SCÉNARIO 25/03 (Réservations reportées + réutilisation de véhicule):
-- 
-- Groupe 1 (09:00-09:30):
--   - Réservation 6002 (18 pers, 09:00) → VH-2026-006 (18 places), revient ~10:15
--   - Réservation 6001 (15 pers, 09:10) → Seul VH-006 peut prendre 15 pers (VH-004 = 12 < 15)
--     MAIS VH-006 occupé par 6002 → 6001 est REJECTÉE → REPORTÉE
-- 
-- Groupe 2 (10:30-11:00):
--   - Réservation 6001 REPORTÉE (15 pers) → VH-2026-006 revenu à 10:15, DISPONIBLE ✓ → peut être ASSIGNÉE
--   - Réservation 6003 (3 pers, 10:30) → autre véhicule
--   - Réservation 6004 (4 pers, 10:40) → autre véhicule
-- 
-- SCÉNARIO 26/03 (Nombre de trajets - VH-005 vs VH-007, même capacité 3 places):
-- 
-- Groupe 1 (09:00-09:30):
--   - Réservation 7001 (3 pers, 09:00) → VH-2026-005 (3 places), 1er TRAJET, revient ~09:50
-- 
-- Groupe 2 (10:00-10:30):
--   - Réservation 7002 (3 pers, 10:00) → VH-007 (nouveau, 3 places, 0 trajets) vs VH-005 (1 trajet)
--     Capacité: égale (3 pers = 3 places) → Nombre de trajets: 0 < 1 → VH-007 gagne
--     → VH-2026-007 utilisé, 1er TRAJET, revient ~10:50
-- 
-- Groupe 3 (11:00-11:30):
--   - Réservation 7003 (2 pers, 11:00) → VH-005 revenu ~10:30 (1 trajet) vs VH-007 revenu ~10:50 (1 trajet)
--     Capacité: VH-005=3 places, écart=1 | VH-007=3 places, écart=1 (égal)
--     Trajets: VH-005=1  | VH-007=1 (égal)
--     Carburant: VH-007 Diesel prioritaire sur VH-005 Électrique → VH-007 choisi (2e TRAJET)
-- ============================================
