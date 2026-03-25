-- =========================================================================
-- DONNEES DE TEST - SPRINT 7 (Scénario Aléatoire / Spécifique)
-- =========================================================================
-- Nettoyage des tables pour éviter les conflits
TRUNCATE TABLE reservation CASCADE;
TRUNCATE TABLE configuration_attente CASCADE;
TRUNCATE TABLE distance CASCADE;
TRUNCATE TABLE vehicule CASCADE;
TRUNCATE TABLE parametre CASCADE;
TRUNCATE TABLE hotel CASCADE;
TRUNCATE TABLE lieu CASCADE;
TRUNCATE TABLE type_carburant CASCADE;

-- =========================================================================
-- 1. CONFIGURATION DE BASE
-- =========================================================================

-- Types de carburant (Diesel=1, Essence=2)
INSERT INTO type_carburant (id, code, libelle) VALUES 
(1, 'D', 'Diesel'),
(2, 'Es', 'Essence');
ALTER SEQUENCE type_carburant_id_seq RESTART WITH 3;

-- Paramètres globaux
INSERT INTO parametre (cle, valeur, description) VALUES
('temps_arret', '30', 'Temps d''arrêt par défaut'),
('vitesse_moyenne', '50', 'Vitesse moyenne par défaut');

-- Configuration du délai d'attente (30 min)
INSERT INTO configuration_attente (temps_attente_minutes, description, actif) VALUES
(30, 'Configuration Sprint 7', TRUE);

-- =========================================================================
-- 2. INFRASTRUCTURE (Lieux, Hotels, Distances)
-- =========================================================================

-- Lieux (1=Aeroport, 2=Hotel1, 3=Hotel2)
INSERT INTO lieu (id, code, libelle) VALUES 
(1, 'AER', 'Aeroport'), 
(2, 'H1', 'hotel1'), 
(3, 'H2', 'hotel2');
ALTER SEQUENCE lieu_id_seq RESTART WITH 4;

-- Hotels (Liés aux lieux par convention ou mapping application)
-- ID 2 -> Hotel1, ID 3 -> Hotel2
INSERT INTO hotel (id, libelle) VALUES 
(2, 'hotel1'), 
(3, 'hotel2');
ALTER SEQUENCE hotel_id_seq RESTART WITH 4;

-- Distances
-- 1 | hotel1   | hotel1  | 90  <-- Ignoré car incohérent (ou boucle ?), on assume erreur de saisie ou test spécifique.
-- 2 | aeroport | hotel1  | 35
-- 3 | hotel1   | hotel2  | 60
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
(1, 2, 90), -- Aeroport -> Hotel1
(2, 1, 90), -- Hotel1 -> Aeroport (Symétrie)
(2, 3, 60), -- Hotel1 -> Hotel2
(3, 2, 60); -- Hotel2 -> Hotel1 (Symétrie)

-- Ajout distance Aeroport -> Hotel2 (déduite ou requise pour le test)
-- Si Hotel1->Hotel2 = 60, et Aeroport->Hotel1 = 35.
-- On ajoute une distance arbitraire ou on laisse le système chercher un chemin via Hotel1
-- Pour simplifier, on ajoute une connexion directe si nécessaire, sinon on laisse le graphe faire.
-- Le système de calcul d'itinéraire utilise le "plus court chemin" ou les connexions directes.
-- Si id 1 | hotel1 | hotel1 | 90 était Aeroport->Hotel2, alors 90km.
INSERT INTO distance (lieu_depart, lieu_arrivee, km) VALUES
(1, 3, 35), -- Aeroport -> Hotel2
(3, 1, 35); -- Hotel2 -> Aeroport

-- =========================================================================
-- 3. FLOTTE DE VEHICULES
-- =========================================================================
-- vehicule1 | 6  | diesel   | 00:00:00
-- vehicule2 | 8  | diesel   | 00:00:00
-- vehicule3 | 10 | diesel   | 00:00:00
-- vehicule4 | 8  | diesel   | 09:00:00
-- vehicule5 | 12 | essence  | 13:00:00

INSERT INTO vehicule (reference, place, type_carburant, vitesse_moyenne, heure_disponibilite) VALUES
('vehicule1', 5, 1, 50, '09:00:00'),
('vehicule2', 5, 1, 50, '09:00:00'),
('vehicule3', 12, 1, 50, '00:00:00'),
('vehicule4', 9, 1, 50, '09:00:00'),
('vehicule5', 12, 2, 50, '13:00:00');

-- =========================================================================
-- 4. SCENARIO RESERVATIONS
-- =========================================================================
-- Client1     | 7  | 19/02/26   | 09:00:00  | hotel1
-- Client2     | 20 | 19/02/26   | 09:00:00  | hotel2
-- Client3     | 3  | 19/02/26   | 05:00:00  | hotel1
-- Client4     | 10 | 19/02/26   | 06:15:00  | hotel1
-- Client5     | 5  | 19/02/26   | 20:00:00  | hotel1
-- Client6     | 12 | 19/02/26   | 13:30:00  | hotel1

INSERT INTO reservation (reference, client, nombre, date, heure, hotel) VALUES
(1001, 'Client1', 7, '2026-02-19', '09:00:00', 2), -- Hotel1 (ID 2)
(1002, 'Client2', 20, '2026-02-19', '08:00:00', 3), -- Hotel2 (ID 3)
(1003, 'Client3', 3, '2026-02-19', '09:10:00', 2),
(1004, 'Client4', 10, '2026-02-19', '09:15:00', 2),
(1005, 'Client5', 5, '2026-02-19', '09:20:00', 2),
(1006, 'Client6', 12, '2026-02-19', '13:30:00', 2);
