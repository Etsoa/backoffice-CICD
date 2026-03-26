# 🚀 Guide d'Implémentation et Contribution

## Vue d'ensemble
Ce guide explique comment **ajouter des features**, **modifier la logique**, et **maintenir la qualité** du code.

---

## 1️⃣ Avant de Commencer: Workflow Recommandé

### Étape 1: Lire la Spec
- Lire [02_BUSINESS_RULES.md](02_BUSINESS_RULES.md) pour comprendre la règle métier
- Identifier quelle classe va être affectée (PlanificationService? DTO? Modèle?)
- Vérifier si impact DB (schema change?)

### Étape 2: Créer une Branche
```bash
git checkout -b feature/nom-feature
# ou
git checkout -b bugfix/nom-bug
```

### Étape 3: Écrire / Modifier Code
- Respecter les conventions (voir section 5)
- Ajouter commentaires sur logique complexe
- Tests: manuel avec `sprint7_test_data.sql`

### Étape 4: Valider
```bash
mvn clean compile -DskipTests       # Pas d'erreurs syntaxe?
mvn clean package                    # Build WAR ok?
# Test manuel sur Tomcat
```

### Étape 5: Commit + Push
```bash
git add src/
git commit -m "feat: description courte"
git push origin feature/nom-feature
```

---

## 2️⃣ Ajouter une Nouvelle Règle Métier

### Cas: Augmenter délai d'attente de 30 à 45 minutes

#### Étape A: Modification BD
```sql
-- NO SQL SCHEMA CHANGE NEEDED!
-- Juste update la valeur:
UPDATE configuration_attente 
SET temps_attente_minutes = 45 
WHERE id = 1;
```

#### Étape B: Test du Code
```bash
# Le code utilise getDelaiAttente(), donc change automatique!
# run PlanificationController.genererPlanning(2026-03-19)
# Vérifier: finIntervalle = heureDepart + 45min ✓
```

### Cas: Ajouter carburant "Électrique" comme priorité maximale

#### Étape A: Insert nouvelle valeur
```sql
INSERT INTO type_carburant (code, libelle) VALUES ('El', 'Électrique');
-- Mettre à jour véhicules avec Électrique
UPDATE vehicule SET type_carburant_id = 3 WHERE code = 'V6';
```

#### Étape B: Modifier code
```java
// PlanificationService.getPrioriteCarburant()
private int getPrioriteCarburant(Vehicule v) {
    String code = v.getTypeCarburant() != null ? v.getTypeCarburant().getCode() : "";
    return switch (code) {
        case "El" -> 0;  // ← Priorité maximale (avant Diesel=1)
        case "D" -> 1;
        case "Es" -> 2;
        default -> 3;
    };
}
```

#### Étape C: Tester
```bash
mvn clean compile
# Tester que Vehicle avec Électrique est sélectionné en priorité
```

---

## 3️⃣ Ajouter un Nouveau Champ à DTO

### Cas: Ajouter "Prix du trajet" à ReservationPlanningDTO

#### Étape A: Ajouter Champ
```java
public class ReservationPlanningDTO {
    // ... champs existants ...
    
    // ✅ NOUVEAU
    private double prixTrajet;  // EUR
    
    // ✅ GETTER + SETTER
    public double getPrixTrajet() { return prixTrajet; }
    public void setPrixTrajet(double prixTrajet) { this.prixTrajet = prixTrajet; }
}
```

#### Étape B: Peupler le Champ
```java
// PlanificationService.ajouterReservationAuPlanning()
private void ajouterReservationAuPlanning(...) {
    // ... code existant ...
    
    // ✅ NOUVEAU
    double prix = getDistance(aeroportId, lieuHotelId) * 0.50;  // 0.50€/km
    resaDTO.setPrixTrajet(prix);
}
```

#### Étape C: Afficher en JSP
```jsp
<!-- templates/planification/index.jsp -->
<tr>
    <td><%= reservation.getClient() %></td>
    <td><%= reservation.getNombrePassagers() %></td>
    <td><%= String.format("%.2f € ", reservation.getPrixTrajet()) %></td>
    <!-- ... autres colonnes ... -->
</tr>
```

---

## 4️⃣ Modifier la Logique d'Assignation

### Cas: Prioriser réservations VIP avant autres

#### Étape A: Ajouter Champ Modèle
```sql
ALTER TABLE reservation ADD COLUMN is_vip BOOLEAN DEFAULT FALSE;
```

#### Étape B: Modifier DTO
```java
public class ReservationPlanningDTO {
    // ... existant ...
    private boolean vip;
    public boolean isVip() { return vip; }
    public void setVip(boolean vip) { this.vip = vip; }
}
```

#### Étape C: Modifier Tri Assignation
```java
// PlanificationService.trierReservationsParPriorite()
private void trierReservationsParPriorite(List<Reservation> reservationsGroupe, int nombreReportees) {
    Set<Integer> idsReservationsReportees = new HashSet<>();
    for (int i = 0; i < Math.min(nombreReportees, reservationsGroupe.size()); i++) {
        idsReservationsReportees.add(reservationsGroupe.get(i).getId());
    }

    reservationsGroupe.sort((r1, r2) -> {
        boolean r1Reportee = idsReservationsReportees.contains(r1.getId());
        boolean r2Reportee = idsReservationsReportees.contains(r2.getId());
        
        if (r1Reportee && !r2Reportee) return -1;
        if (!r1Reportee && r2Reportee) return 1;
        
        // ✅ NOUVEAU: Priorité VIP
        if (r1.isVip() && !r2.isVip()) return -1;
        if (!r1.isVip() && r2.isVip()) return 1;
        
        // Existant: par pax décroissant
        return Integer.compare(r2.getNombre(), r1.getNombre());
    });
}
```

#### Étape D: Tester
```bash
mvn clean compile
# Test manuel: 
#   - Créer 2 réservations (1 VIP, 1 normal)
#   - Vérifier que VIP est assignée en priorité
```

---

## 5️⃣ Conventions Code à Respecter

### Nommage

#### Classes
```java
// Service
public class PlanificationService { }       // ✓
public class PlanService { }                // ✗ Trop court

// DTO
public class VehiculePlanningDTO { }        // ✓
public class VehiculeDTO { }                // Acceptable
public class Vehicule_Planning { }          // ✗ Pas de snake_case

// Modèle JPA
public class Reservation { }                // ✓
public class Resa { }                       // ✗ Trop court
```

#### Méthodes Privées
```java
// Extraire logique complexe en fonctions privées
private void creerGroupes(...) { }          // ✓ Descriptif
private void crierGroupes(...) { }          // ✗ Typo
private void a() { }                        // ✗ Pas clair

// Nommer par intention
private boolean hasPassagersRemaining(...) { }   // ✓
private void fillVehicles(...) { }              // ✗ Anglais, pas français
```

### Longueur Méthode
```
✅ Idéal: 5-30 lignes
⚠️ Warning: 30-100 lignes (refactoriser?)
❌ Trop long: >100 lignes (MUST refactor)
```

### Commentaires
```java
// ✅ Commentaire UTILE: Explique POURQUOI
// Les reportées doivent être triées AVANT les nouvelles (règle métier)

// ❌ Commentaire INUTILE: Redondant avec code
int distance = 5;  // Mettre distance à 5

// ✅ Commentaire pour logique non-évidente
// Nearest neighbor: chercher hôtel le plus proche (optimisation itinéraire)
ReservationPlanningDTO plusProche = trouverReservationLaPlusProche(...);

// ❌ Commentaire Obvious
if (x == null) {  // Vérifier si x null
    return;
}
```

### Format Code
```java
// ✅ Bonne indentation (4 espaces)
if (condition) {
    action();
} else {
    otherAction();
}

// ✅ Espaces autour opérateurs
int result = value1 + value2;

// ✗ Pas de ligne > 120 caractères (readability)

// ✓ Utiliser streams si lisible
List<Integer> ids = reservations.stream()
    .map(Reservation::getId)
    .collect(Collectors.toList());
```

---

## 6️⃣ Ajouter une Table BD

### Cas: Ajouter tracking "temps_service" par hôtel

#### Étape A: Créer Migration SQL
```sql
-- File: database/migration_add_temps_service.sql
CREATE TABLE temps_service_hotel (
    id SERIAL PRIMARY KEY,
    hotel_id INT NOT NULL,
    minutes INT NOT NULL DEFAULT 15,
    
    CONSTRAINT fk_hotel FOREIGN KEY (hotel_id) REFERENCES hotel(id),
    UNIQUE (hotel_id)
);

INSERT INTO temps_service_hotel (hotel_id, minutes) VALUES
    (1, 15),
    (2, 20);
```

#### Étape B: Créer Modèle JPA
```java
@Entity
@Table(name = "temps_service_hotel")
public class TempsServiceHotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
    
    private Integer minutes;
    
    // Getters/Setters
    public Integer getMinutes() { return minutes; }
    public void setMinutes(Integer minutes) { this.minutes = minutes; }
}
```

#### Étape C: Ajouter Service Method
```java
public class PlanificationService {
    
    public int getTempsServiceHotel(Integer hotelId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Integer> query = em.createQuery(
                "SELECT t.minutes FROM TempsServiceHotel t WHERE t.hotel.id = :hotelId",
                Integer.class
            );
            query.setParameter("hotelId", hotelId);
            List<Integer> result = query.getResultList();
            return result.isEmpty() ? 0 : result.get(0);
        }
    }
}
```

#### Étape D: Utiliser en Calcul Itinéraire
```java
private void calculerHorairesEtDistances(...) {
    // ... existant ...
    for (ReservationPlanningDTO rp : ordreDepot) {
        // ... temps trajet ...
        
        // ✅ NOUVEAU: Ajouter temps service hôtel
        int tempsService = getTempsServiceHotel(rp.getLieuHotelId());  // 15-20 min
        heureArrivee = ajouterMinutes(heureArrivee, tempsService);    // Attendre dépôt clients
        
        // Prochain segment part après service
        heureCourante = heureArrivee;
    }
}
```

---

## 7️⃣ Tester Localement

### Setup Test Environment

```bash
# 1. Pré-requis
# - PostgreSQL running
# - Tables créées

# 2. Charger données test
psql -U user -d backoffice -f database/sprint7_test_data.sql

# 3. Build et Deploy
mvn clean package
# Déployer WAR dans Tomcat

# 4. Tester Planning
# http://localhost:8080/backoffice/planification/index.jsp?date=2026-03-19
```

### Test Manual Points

```
[ ] Groupe 1 créé correctement (08:00-08:30)?
[ ] Véhicule3 assigné au Groupe 1?
[ ] Client2[20] splité 12+8?
[ ] Groupe 2 créé avec reportées?
[ ] Itinéraire Nearest Neighbor OK?
[ ] Heure retour correcte?
[ ] Distance totale calculée?
[ ] Réservations non assignées affichées?
```

### Test Automatisé (Future)

```java
// TODO: Ajouter tests JUnit
@Test
public void testGenererRegroupements_SplitReservation() {
    Date date = Date.valueOf("2026-03-19");
    List<RegroupementDTO> regroupements = service.genererRegroupements(date);
    
    // Assert Groupe 1
    assertEquals(1, regroupements.get(0).getNumeroGroupe());
    assertEquals(Time.valueOf("08:00:00"), regroupements.get(0).getHeureDepart());
    
    // Assert split
    RegroupementDTO groupe2 = regroupements.get(1);
    assertTrue(groupe2.getReservationsNonAssignees().stream()
        .map(r -> r.getNombre())
        .reduce(0, Integer::sum) > 0);
}
```

---

## 8️⃣ Debugging Workflow

### Problème: Planning génère 0 groupes

```java
// 1. Vérifier données
SELECT COUNT(*) FROM reservation WHERE date='2026-03-19';
// → Si 0, charger test data!

// 2. Vérifier logs
System.out.println("Num réservations: " + reservations.size());
System.out.println("Num véhicules: " + tousVehicules.size());

// 3. Vérifier configuration
SELECT * FROM configuration_attente WHERE actif = TRUE;
// → Si vide, INSERT row!

// 4. Stepping
// Dans IDE: Ajouter breakpoint à genererRegroupements()
// F5 Step-into, vérifier état à chaque étape
```

### Problème: Véhicule jamais assigné

```
1. Vérifier capacité > reservations besoin
2. Vérifier heure disponibilité
3. Vérifier dans boucle assignation que véhicule entre dans candidats
4. Logger: System.out.println("Vehicule " + v.getId() + " dispo? " + dispo)
```

### Problème: Distance toujours 0

```sql
-- Vérifier données
SELECT * FROM distance;
-- Vérifier matching libellé
SELECT l.id, l.libelle FROM lieu l;
SELECT h.id, h.libelle FROM hotel h;
-- Doivent être exacts (case-sensitive!)

-- Vérifier FK
SELECT d.km FROM distance d 
WHERE d.lieu_depart_id = (SELECT id FROM lieu WHERE libelle='Hotel 1')
  AND d.lieu_arrivee_id = (SELECT id FROM lieu WHERE code='TNR');
```

---

## 9️⃣ Déployer sur Production

### Checklist Pre-Deploy

- [ ] Lire toute la spec 2 fois
- [ ] Code runs localement sans erreur
- [ ] Tests manuels sur tous les cas
- [ ] Pas de commentaires inutiles
- [ ] Maven compile/package ok
- [ ] Pas de hardcoding
- [ ] Configuration en BD, pas en code
- [ ] Performance ok (pas de N+1 queries)

### Deploy Steps

```bash
# 1. Build production WAR
mvn clean package -DskipTests

# 2. Backup BD
pg_dump backoffice > backup_$(date +%Y%m%d).sql

# 3. Run migrations (si schema changes)
psql -U user -d backoffice -f migration.sql

# 4. Stop current Tomcat
sudo systemctl stop tomcat

# 5. Deploy WAR
cp target/backoffice.war /var/lib/tomcat/webapps/

# 6. Start Tomcat
sudo systemctl start tomcat

# 7. Verify
# http://prod-server:8080/backoffice/planification/
# Tester date=2026-03-19
curl -X GET "http://localhost:8080/backoffice/planification?date=2026-03-19" | grep "Groupe"
```

---

## 🔟 Checklist Qualité Code

Avant le commit final:

```
Code Quality:
- [ ] Pas de syntax errors (mvn compile ok)
- [ ] Pas de unused imports
- [ ] Pas de Hardcoding valeurs (sauf constantes)
- [ ] Noms variables clairs (>= 3 caractères)
- [ ] Méthodes <= 50 lignes
- [ ] Commentaires expliquent POURQUOI, pas QUOI

Logique:
- [ ] Respect des règles métier [02_BUSINESS_RULES.md]
- [ ] Test manuel sur sprint7_test_data.sql ok
- [ ] Edge cases gérés (null checks, empty lists)
- [ ] Pas de N+1 queries

Git:
- [ ] Commit message descriptif
- [ ] 1 feature = 1 commit (ou quelques petits)
- [ ] Pas de merge conflicts

Documentation:
- [ ] Ajouter section dans .md si feature new
- [ ] Expliquer logique complexe en commentaire
```

---

**Pour Questions/Help:**
1. Consulter [01_PROJECT_STRUCTURE.md] pour trouver le bon fichier
2. Consulter [02_BUSINESS_RULES.md] pour règles métier
3. Consulter [03_FRAMEWORK_ARCHITECTURE.md] pour patterns
4. Consulter [04_DATABASE_SCHEMA.md] pour BD
5. Créer issue si besoin de gros refactor
