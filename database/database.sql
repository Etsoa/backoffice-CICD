
\c cicd

-- Table clients
CREATE TABLE client (
    id_client CHAR(4) PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);

-- Table hôtels
CREATE TABLE hotel (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- Table réservations
CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    reference INT NOT NULL,
    nombre INT NOT NULL,
    date DATE NOT NULL,
    heure TIME NOT NULL,
    hotel INT NOT NULL,
    client VARCHAR(255),
    FOREIGN KEY (hotel) REFERENCES hotel(id)
);

-- Table types de carburant
CREATE TABLE type_carburant (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    libelle VARCHAR(50) NOT NULL
);

-- Table véhicules (Sprint 2)
CREATE TABLE vehicule (
    id SERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL UNIQUE,
    place INT NOT NULL,
    type_carburant INT NOT NULL,
    vitesse_moyenne DECIMAL(5,2) NOT NULL DEFAULT 60.00, -- en km/h
    heure_disponibilite TIME NOT NULL DEFAULT '00:00:00',
    FOREIGN KEY (type_carburant) REFERENCES type_carburant(id)
);

-- Table lieux (Sprint 3 - Planification)
CREATE TABLE lieu (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

-- Table distances (Sprint 3 - Planification)
CREATE TABLE distance (
    id SERIAL PRIMARY KEY,
    lieu_depart INT NOT NULL,
    lieu_arrivee INT NOT NULL,
    km DECIMAL(6,2) NOT NULL,
    FOREIGN KEY (lieu_depart) REFERENCES lieu(id),
    FOREIGN KEY (lieu_arrivee) REFERENCES lieu(id)
);

-- Table tokens (Sprint 2 - Protection)
CREATE TABLE token (
    id SERIAL PRIMARY KEY,
    valeur_token VARCHAR(16) NOT NULL UNIQUE,
    date_expiration TIMESTAMP NULL
);

-- Table paramètres (configuration)
CREATE TABLE parametre (
    id SERIAL PRIMARY KEY,
    cle VARCHAR(50) NOT NULL UNIQUE,
    valeur VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- Table configuration du temps d'attente (Sprint 5)
-- Permet de configurer le délai d'attente pour le regroupement des réservations
CREATE TABLE configuration_attente (
    id SERIAL PRIMARY KEY,
    temps_attente_minutes INT NOT NULL DEFAULT 30,
    description VARCHAR(255),
    actif BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================
-- TABLES POUR LA PERSISTENCE DES PLANIFICATIONS
-- Sprint 5 - Sauvegarde des planifications
-- ============================================

-- ============================================
-- TABLE PRINCIPALE : PLANIFICATION
-- ============================================
-- Une planification = une date donnée avec tous les regroupements et assignations
CREATE TABLE planification (
    id SERIAL PRIMARY KEY,
    date_planification DATE NOT NULL UNIQUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) DEFAULT 'DRAFT',  -- DRAFT | ACTIVE | ARCHIVED
    delai_attente_utilise INT,  -- délai appliqué pour cette planification (en minutes)
    nombre_regroupements INT,
    nombre_reservations_total INT,
    nombre_reservations_assignees INT,
    nombre_vehicules_utilises INT,
    actif BOOLEAN DEFAULT TRUE
);

-- ============================================
-- TABLE : REGROUPEMENTS (groupes de réservations)
-- ============================================
-- Chaque regroupement = ensemble de réservations groupées avec délai d'attente
CREATE TABLE regroupement (
    id SERIAL PRIMARY KEY,
    planification_id INT NOT NULL,
    numero_regroupement INT NOT NULL,  -- 1, 2, 3... (ordre du groupe dans la journée)
    heure_depart_groupe TIME NOT NULL,  -- heure de départ commune (dernière réservation du groupe)
    nombre_reservations INT NOT NULL,
    nombre_passagers_total INT NOT NULL,
    nombre_vehicules_assignes INT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (planification_id) REFERENCES planification(id) ON DELETE CASCADE,
    UNIQUE(planification_id, numero_regroupement)
);

-- ============================================
-- TABLE : MAPPING REGROUPEMENT-RESERVATIONS
-- ============================================
-- Associe les réservations à leurs regroupements
CREATE TABLE regroupement_reservation (
    id SERIAL PRIMARY KEY,
    regroupement_id INT NOT NULL,
    reservation_id INT NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (regroupement_id) REFERENCES regroupement(id) ON DELETE CASCADE,
    FOREIGN KEY (reservation_id) REFERENCES reservation(id),
    UNIQUE(regroupement_id, reservation_id)
);

-- ============================================
-- TABLE : ASSIGNATION VEHICULE AU REGROUPEMENT
-- ============================================
-- Un regroupement peut avoir plusieurs véhicules (ex: capacité dépassée)
CREATE TABLE assignation_vehicule (
    id SERIAL PRIMARY KEY,
    regroupement_id INT NOT NULL,
    vehicule_id INT NOT NULL,
    numero_ordre_groupe INT NOT NULL,  -- 1er, 2e véhicule assigné au groupe
    -- Statistiques du voyage
    nombre_trajet_effectues INT DEFAULT 1,  -- nombre de trajets effectués par ce véhicule aujourd'hui
    heure_depart_aeroport TIME NOT NULL,
    heure_retour_aeroport TIME NOT NULL,
    distance_totale_km DECIMAL(8, 2) NOT NULL,
    temps_total_minutes INT NOT NULL,
    nombre_passagers_transportes INT NOT NULL,
    -- Traçabilité
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (regroupement_id) REFERENCES regroupement(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicule_id) REFERENCES vehicule(id),
    UNIQUE(regroupement_id, numero_ordre_groupe)
);

-- ============================================
-- TABLE : ITINERAIRE (détail des arrêts/étapes)
-- ============================================
-- Chaque étape du parcours d'un véhicule
-- Ex: Aéroport -> Hôtel Colbert -> Hôtel Novotel -> Aéroport
CREATE TABLE itineraire_arret (
    id SERIAL PRIMARY KEY,
    assignation_vehicule_id INT NOT NULL,
    ordre_arret INT NOT NULL,  -- 1=départ aéroport, 2=1er hôtel, 3=2e hôtel, etc.
    lieu_id INT NOT NULL,  -- aéroport (TNR) ou lieu de l'hôtel
    hotel_id INT,  -- NULL si c'est l'aéroport, sinon l'id de l'hôtel
    heure_arrivee TIME NOT NULL,  -- heure prévue d'arrivée
    heure_depart TIME NOT NULL,  -- heure prévue de départ
    nombre_passagers_embarques INT,  -- nombre de passagers cumulé à cet arrêt
    distance_depuis_prev_km DECIMAL(8, 2),  -- distance depuis l'arrêt précédent
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignation_vehicule_id) REFERENCES assignation_vehicule(id) ON DELETE CASCADE,
    FOREIGN KEY (lieu_id) REFERENCES lieu(id),
    FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);

-- ============================================
-- TABLE : SUIVI DES TRAJETS VEHICULE (Analytics)
-- ============================================
-- Historique des trajets effectués par chaque véhicule pour analytics
CREATE TABLE suivi_trajet_vehicule (
    id SERIAL PRIMARY KEY,
    planification_id INT NOT NULL,
    vehicule_id INT NOT NULL,
    date_planification DATE NOT NULL,
    nombre_regroupements_assignes INT,
    nombre_passagers_total INT,
    distance_totale_km DECIMAL(10, 2),
    temps_total_heures DECIMAL(5, 2),
    heure_premiere_utilisation TIME,
    heure_derniere_retour TIME,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (planification_id) REFERENCES planification(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicule_id) REFERENCES vehicule(id),
    UNIQUE(planification_id, vehicule_id)
);

-- ============================================
-- INDEX POUR OPTIMISER LES REQUÊTES
-- ============================================
CREATE INDEX idx_planif_date ON planification(date_planification);
CREATE INDEX idx_planif_statut ON planification(statut, actif);
CREATE INDEX idx_planif_date_statut ON planification(date_planification, statut);

CREATE INDEX idx_regroup_planif ON regroupement(planification_id);
CREATE INDEX idx_regroup_numero ON regroupement(planification_id, numero_regroupement);

CREATE INDEX idx_regroup_resa_regroupement ON regroupement_reservation(regroupement_id);
CREATE INDEX idx_regroup_resa_reservation ON regroupement_reservation(reservation_id);

CREATE INDEX idx_assign_regroup ON assignation_vehicule(regroupement_id);
CREATE INDEX idx_assign_vehicule ON assignation_vehicule(vehicule_id);
CREATE INDEX idx_assign_numero_ordre ON assignation_vehicule(regroupement_id, numero_ordre_groupe);

CREATE INDEX idx_itineraire_assign ON itineraire_arret(assignation_vehicule_id);
CREATE INDEX idx_itineraire_ordre ON itineraire_arret(assignation_vehicule_id, ordre_arret);

CREATE INDEX idx_suivi_planif ON suivi_trajet_vehicule(planification_id);
CREATE INDEX idx_suivi_vehicule ON suivi_trajet_vehicule(vehicule_id);
