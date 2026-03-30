# 🗄️ Schéma Base de Données et Modèles

## Vue d'Ensemble

La BD **PostgreSQL** contient:
- Entités métier: `Reservation`, `Vehicule`, `Hotel`, `Lieu`, `Distance`
- Configuration: `ConfigurationAttente`
- Historique: `Planification`, `Regroupement`, `AssignationVehicule`, etc.

---

## 1️⃣ Entités Principales

### Table: `reservation`
**Représente:** Une demande de transport client

```sql
CREATE TABLE reservation (
    id SERIAL PRIMARY KEY,
    reference INT NOT NULL UNIQUE,      -- Numéro de résa (1, 2, 3...)
    client_id INT NOT NULL,             -- Lien vers Client
    nombre INT NOT NULL,                -- Nb de passagers (1-20+)
    date DATE NOT NULL,                 -- Date du trajet
    heure TIME NOT NULL,                -- Heure RV aéroport
    hotel_id INT NOT NULL,              -- Destination hôtel
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT fk_hotel FOREIGN KEY (hotel_id) REFERENCES hotel(id),
    INDEX idx_date (date),
    INDEX idx_heure (heure)
);
```

**Données Test (sprint7_test_data.sql):**
```
Réf#1: Client1, 7 pers, Hotel1, 09:00
Réf#2: Client2, 20 pers, Hotel2, 08:00   ← Sera split!
Réf#3: Client3, 3 pers, Hotel1, 09:10
Réf#4: Client4, 6 pers, Hotel1, 09:15
Réf#5: Client5, 2 pers, Hotel1, 09:20
Réf#6: Client6, 12 pers, Hotel1, 13:30
```

**Important:** 
- `nombre` = total passagers
- `heure` = heure collecte aéroport
- `reference` est le **numéro humain**, pas l'ID technique

---

### Table: `vehicule`
**Représente:** Véhicule de transport

```sql
CREATE TABLE vehicule (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE,            -- V1, V2, V3...
    place INT NOT NULL,                 -- Capacité (5, 9, 12...)
    type_carburant_id INT,              -- Diesel, Essence, Hybride...
    vitesse_moyenne DOUBLE PRECISION,   -- km/h pour calculs trajet
    heure_disponibilite TIME,           -- Si véhicule libre à heure spécifique
    created_at TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT fk_carburant FOREIGN KEY (type_carburant_id) REFERENCES type_carburant(id),
    INDEX idx_place (place),
    INDEX idx_code (code)
);
```

**Données Test:**
```
ID1: V1, 5 places, Diesel, vitesse=50.0
ID2: V2, 5 places, Essence, vitesse=50.0
ID3: V3, 12 places, Diesel, vitesse=50.0
ID4: V4, 9 places, Diesel, vitesse=50.0
ID5: V5, 12 places, Essence, vitesse=50.0
```

**Important:**
- `vitesse_moyenne` = source unique pour calculs trajet
- `heure_disponibilite` = si null, considéré 00:00 (toujours dispo)
- **Jamais hardcodée à 60.0!**

---

### Table: `hotel`
**Représente:** Hôtel destination

```sql
CREATE TABLE hotel (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL,      -- "Hotel 1", "Hotel 2"...
    created_at TIMESTAMP DEFAULT NOW(),
    
    INDEX idx_libelle (libelle)
);
```

**Données Test:** 2 hôtels
```
ID1: "Hotel 1"
ID2: "Hotel 2"
```

**Important:**
- Lien avec `Lieu` se fait par **matching du libellé** (pas de FK!)
- Raison: Lieu table est pour "TNR", "Hotel 1", "Hotel 2" (aéroport + hôtels)
- Logique: `getLieuIdByHotelId()` cherche `Lieu.libelle = Hotel.libelle`

---

### Table: `lieu`
**Représente:** Point géographique (aéroport ou hôtel)

```sql
CREATE TABLE lieu (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10),                   -- 'TNR' pour aéroport
    libelle VARCHAR(100) NOT NULL,      -- 'TNR', 'Hotel 1', 'Hotel 2'...
    created_at TIMESTAMP DEFAULT NOW(),
    
    INDEX idx_code (code),
    INDEX idx_libelle (libelle)
);
```

**Données Test:**
```
ID1: code='TNR', libelle='TNR'         # Aéroport
ID2: code=null, libelle='Hotel 1'      # Hôtel 1
ID3: code=null, libelle='Hotel 2'      # Hôtel 2
```

**Important:**
- `code` unique = TNR (aéroport)
- Hôtels n'ont pas de code (ils matchent par libellé)
- Séparation: Aéroport vs Hôtels via FK dans Distance

---

### Table: `distance`
**Représente:** Distance entre 2 lieux

```sql
CREATE TABLE distance (
    id SERIAL PRIMARY KEY,
    lieu_depart_id INT NOT NULL,       -- Départ (aéroport ou hôtel)
    lieu_arrivee_id INT NOT NULL,      -- Arrivée
    km DOUBLE PRECISION NOT NULL,      -- Distance (35.0, 90.0, etc.)
    created_at TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT fk_lieu_depart FOREIGN KEY (lieu_depart_id) REFERENCES lieu(id),
    CONSTRAINT fk_lieu_arrivee FOREIGN KEY (lieu_arrivee_id) REFERENCES lieu(id),
    CONSTRAINT uq_distance UNIQUE (lieu_depart_id, lieu_arrivee_id),
    INDEX idx_lieux (lieu_depart_id, lieu_arrivee_id)
);
```

**Données Test:**
```
TNR → Hotel1: 90 km
TNR → Hotel2: 35 km
Hotel1 → Hotel2: N/A (différents hôtels)
Hotel1 → TNR: 90 km (symétrique)
Hotel2 → TNR: 35 km (symétrique)
```

**Important:**
- Distance **symétrique** (aller-retour même km)
- `getDistance()` cherche dans **2 sens:**
  ```sql
  (lieu_depart = A AND lieu_arrivee = B) OR
  (lieu_depart = B AND lieu_arrivee = A)
  ```

---

### Table: `configuration_attente` ⚙️
**Représente:** Configuration système (délai d'attente)

```sql
CREATE TABLE configuration_attente (
    id SERIAL PRIMARY KEY,
    temps_attente_minutes INT NOT NULL DEFAULT 30,  -- délaiAttente
    actif BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    
    INDEX idx_actif (actif)
);
```

**Données Test:**
```
ID1: temps_attente_minutes=30, actif=true
```

**Important:**
- **1 seul enregistrement actif attendu**
- `getDelaiAttente()` récupère le dernier `WHERE actif=true`
- Pas de fallback! Si vide → Exception
- Utilisé pour:
  1. Intervalle groupe: `finIntervalle = heureDepart + 30min`
  2. Limite disponibilité véhicule: `heureLimite = heureDepartGroupe + 30min`

---

## 2️⃣ Entités d'Historique (Persistance)

### Table: `planification`
**Représente:** Une génération de planning (snapshot)

```sql
CREATE TABLE planification (
    id SERIAL PRIMARY KEY,
    date_planning DATE NOT NULL,        -- Date de la planification
    date_generation TIMESTAMP DEFAULT NOW(),
    status VARCHAR(20),                 -- 'ACTIVE', 'ARCHIVED'
    
    CONSTRAINT uq_date_status UNIQUE (date_planning, status)
);
```

---

### Table: `regroupement`
**Représente:** Un groupe (intervalle horaire)

```sql
CREATE TABLE regroupement (
    id SERIAL PRIMARY KEY,
    planification_id INT NOT NULL,
    numero_regroupement INT,            -- 1, 2, 3...
    heure_depart TIME,
    heure_fin_intervalle TIME,
    delai_attente_minutes INT,
    
    CONSTRAINT fk_planification FOREIGN KEY (planification_id) REFERENCES planification(id)
);
```

---

### Table: `assignation_vehicule`
**Représente:** Assignation véhicule ↔ réservations

```sql
CREATE TABLE assignation_vehicule (
    id SERIAL PRIMARY KEY,
    regroupement_id INT NOT NULL,
    vehicule_id INT NOT NULL,
    heure_depart TIME,
    heure_retour_aeroport TIME,
    distance_totale DOUBLE PRECISION,
    
    CONSTRAINT fk_regroupement FOREIGN KEY (regroupement_id) REFERENCES regroupement(id),
    CONSTRAINT fk_vehicule FOREIGN KEY (vehicule_id) REFERENCES vehicule(id)
);
```

---

## 3️⃣ Requêtes Clés (Utilisées dans Code)

### Récupérer réservations du jour
```sql
SELECT r FROM Reservation r 
WHERE r.date = :date 
ORDER BY r.heure ASC, r.reference ASC
```
**Code:** `PlanificationService.getReservationsByDate(date)`

### Récupérer tous les véhicules
```sql
SELECT v FROM Vehicule v 
ORDER BY v.place ASC
```
**Code:** `PlanificationService.getAllVehicules()`

### Récupérer délai d'attente
```sql
SELECT c FROM ConfigurationAttente c 
WHERE c.actif = true 
ORDER BY c.id DESC
LIMIT 1
```
**Code:** `PlanificationService.getDelaiAttente()`

### Récupérer ID aéroport
```sql
SELECT l.id FROM Lieu l 
WHERE l.code = 'TNR'
```
**Code:** `PlanificationService.getAeroportId()` (cached)

### Récupérer ID lieu par hôtel
```sql
SELECT l.id FROM Lieu l 
WHERE LOWER(l.libelle) = LOWER(:libelle)
```
**Code:** `PlanificationService.getLieuIdByHotelId(hotelId)`

### Récupérer distance
```sql
SELECT d.km FROM Distance d 
WHERE (d.lieuDepart.id = :a AND d.lieuArrivee.id = :b) 
   OR (d.lieuDepart.id = :b AND d.lieuArrivee.id = :a)
```
**Code:** `PlanificationService.getDistance(id1, id2)`

---

## 4️⃣ Diagramme Relationnel

```
                    reservation
                    -----------
                    id (PK)
                    reference (1, 2, 3...)
     ┌──────────────┼──────────────┐
     │              │              │
  client_id      hotel_id       (autres)
     │              │
  client          hotel ──┐ (matching par libellé)
                        │
                      lieu (Hotel 1, Hotel 2)
                        │
                    distance ──── lieu (TNR aéroport)
                        │
                        └─ distance km

                    vehicule
                    --------
                    id (PK)
     ┌──────────────┼──────────────┐
     │              │              │
  place      vitesse_moyenne   type_carburant
     │              │
     └──────────────┴──────────────┘
                    │
          (assignation dans planning)
```

---

## 5️⃣ Données Test: Script INSERT

**File:** `database/sprint7_test_data.sql`

```sql
-- Insertion Lieux
INSERT INTO lieu (code, libelle) VALUES ('TNR', 'TNR');
INSERT INTO lieu (libelle) VALUES ('Hotel 1'), ('Hotel 2');

-- Insertion Distances
INSERT INTO distance (lieu_depart_id, lieu_arrivee_id, km) VALUES
    (1, 2, 90.0),   -- TNR → Hotel 1
    (1, 3, 35.0),   -- TNR → Hotel 2
    (2, 1, 90.0),   -- Hotel 1 → TNR
    (3, 1, 35.0);   -- Hotel 2 → TNR

-- Insertion Hôtels
INSERT INTO hotel (libelle) VALUES ('Hotel 1'), ('Hotel 2');

-- Insertion Véhicules
INSERT INTO vehicule (code, place, type_carburant_id, vitesse_moyenne) VALUES
    ('V1', 5, 1, 50.0),   -- Diesel
    ('V2', 5, 2, 50.0),   -- Essence
    ('V3', 12, 1, 50.0),  -- Diesel
    ('V4', 9, 1, 50.0),   -- Diesel
    ('V5', 12, 2, 50.0);  -- Essence

-- Insertion Configuration Attente
INSERT INTO configuration_attente (temps_attente_minutes, actif) VALUES (30, TRUE);

-- Insertion Réservations (DATE = 2026-03-19)
INSERT INTO reservation (reference, client_id, nombre, date, heure, hotel_id) VALUES
    (1, 1, 7,  '2026-03-19', '09:00', 1),
    (2, 2, 20, '2026-03-19', '08:00', 2),  -- SPLIT!
    (3, 3, 3,  '2026-03-19', '09:10', 1),
    (4, 4, 6,  '2026-03-19', '09:15', 1),
    (5, 5, 2,  '2026-03-19', '09:20', 1),
    (6, 6, 12, '2026-03-19', '13:30', 1);
```

---

## 6️⃣ Checklist Intégrité BD

Avant de lancer génération, vérifier:

- [ ] `configuration_attente` a 1 row avec `temps_attente_minutes=30`
- [ ] `vehicule` table complète et `vitesse_moyenne` pas null
- [ ] `distance` symétrique (si (A→B, 90km) existe, (B→A, 90km) aussi)
- [ ] `lieu` contient TNR + tous les hôtels
- [ ] `hotel.libelle` = matching `lieu.libelle` (case-sensitive!)
- [ ] `reservation.hotel_id` valid (FK exists)
- [ ] Pas de `heure_disponibilite` null sur véhicules (default 00:00)

---

## 7️⃣ Erreurs Courantes

| Erreur | Cause | Fix |
|--------|-------|-----|
| `NullPointerException` lors `getDelaiAttente()` | Pas de row dans `configuration_attente` | INSERT configuration_attente row |
| Distance toujours 0 | Mismatch `hotel.libelle` ≠ `lieu.libelle` | Vérifier casse exacte |
| Vitesse toujours 60 | Hardcodée (ancien code) | Retirer hardcode, utiliser `vehicule.vitesse_moyenne` |
| Groupe création échoue | `lieu` manquante pour hôtel | INSERT tous les hôtels dans `lieu` |
| Véhicules jamais dispo | `heure_disponibilite` mal défini | NULL ou 00:00 (toujours dispo) |

---

## 8️⃣ Scripts Utiles

### Reset Complete
```bash
psql -U user -d backoffice -f database/reset.sql
psql -U user -d backoffice -f database/database.sql
psql -U user -d backoffice -f database/sprint7_test_data.sql
```

### Vérifier Data
```sql
SELECT * FROM configuration_attente;
SELECT COUNT(*) FROM reservation WHERE date='2026-03-19';
SELECT * FROM vehicule;
SELECT * FROM distance;
```

### Nettoyer History (Planning)
```sql
DELETE FROM assignation_vehicule;
DELETE FROM regroupement;
DELETE FROM planification;
COMMIT;
```

---

**Next:** Lire [05_IMPLEMENTATION_GUIDE.md](05_IMPLEMENTATION_GUIDE.md) pour ajouter features
