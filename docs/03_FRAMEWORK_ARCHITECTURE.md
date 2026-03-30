# 🏗️ Architecture et Patterns du Framework

## Vue d'ensemble
Le projet suit un pattern **MVC Service-DTO** avec **JPA/Hibernate** pour la persistance. Les actions métier complexes sont encapsulées dans des **Services**, les transferts de données utilisent des **DTOs**, et la presentation se fait en **JSP**.

---

## 1️⃣ Pattern: Service Layer

### Concept
Toute la logique métier réside dans les **Services**, pas dans les Contrôleurs ni les Modèles JPA.

### Exemple: PlanificationService
```java
public class PlanificationService {
    
    // ✅ TOUS les calculs ici
    public List<RegroupementDTO> genererRegroupements(Date date) {
        // Logique complète de génération
    }
    
    public List<VehiculePlanningDTO> genererPlanning(Date date) {
        // Transforme regroupements en planning
    }
    
    // Méthodes utilitaires (encapsulées)
    private Time determinerHeureDepartGroupe(...) { }
    private void assignerVehiculesAuGroupe(...) { }
    private void calculerItineraire(...) { }
}
```

### Avantages
- **Testabilité:** Services = Pure logic, facile à tester
- **Réutilisabilité:** N appels contrôleurs peuvent utiliser le même service
- **Maintenabilité:** Changes métier = 1 place

---

## 2️⃣ Pattern: DTO (Data Transfer Object)

### Concept
Les **Modèles JPA** ne sorient **JAMAIS** du serveur directement. On crée des **DTOs** qui:
- Contiennent uniquement les données nécessaires pour la UI
- Facilitent les transformations
- Découplent Persistance et Présentation

### Hiérarchie DTOs - Planification

```
VehiculePlanningDTO (1 véhicule pour 1 groupe)
├── Vehicule vehicule              (ref au modèle)
├── List<ReservationPlanningDTO>   (réservations assignées)
├── Time heureDepart               (groupe entier part même heure)
├── Time heureRetourAeroport       (quand retour à TNR)
├── double distanceTotale          (km totaux)
└── ... autres metadata

ReservationPlanningDTO (1 réservation dans 1 véhicule)
├── Reservation reservation         (data source)
├── String hotelLibelle            (nom hôtel pour UI)
├── Integer lieuHotelId            (ID pour calculs distance)
├── Time heureDepart               (départ de l'aéroport)
├── Time heureRetour               (arrivée hôtel)
├── double distanceKm              (distance TNR → hôtel)
├── int tempsAttenteMin            (30min par défaut)
└── int nombrePassagers            (pax dans cette portion)

RegroupementDTO (1 groupe pour 1 date)
├── int numeroGroupe               (1, 2, 3...)
├── Time heureDepart               (heure minimale groupe)
├── Time finIntervalle             (+ 30min délai)
├── List<Reservation>              (réservations du groupe)
├── List<VehiculePlanningDTO>      (véhicules assignés)
├── List<Reservation> nonAssignees (reste pour groupe suivant)
└── int nombreReservationsReportees (count de reportées)
```

### Création d'un DTO

```java
// ❌ MAUVAIS: Direct du modèle
Reservation r = reservationRepo.findById(1);
return r;  // Model s'échappe!

// ✅ BON: Via DTO
Reservation r = reservationRepo.findById(1);
ReservationPlanningDTO dto = new ReservationPlanningDTO();
dto.setReservation(r);
dto.setHotelLibelle(getHotelLibelle(r.getHotel()));
// ... populate autres champs
return dto;
```

---

## 3️⃣ Pattern: TrackingData (Accumulator)

### Concept
Lors de la génération de planning, on maintient 4 maps de tracking pour **éviter de repasser par la BD** à chaque calcul:

```java
class TrackingData {
    boolean[] assignees;                      // [i] = assignée?
    Map<Integer, Time> heureRetourParVehicule;    // V→heure
    Map<Integer, Integer> nombreTrajetsParVehicule; // V→count
    Set<Integer> indicesReportees;            // Indices à retenter
}
```

### Utilisation
```java
// Initialisée une fois au début
TrackingData tracking = initializerTrackingMaps(tousVehicules, reservations.size());

// Mise à jour à chaque loop
for (int i = 0; i < reservations.size(); i++) {
    if (tracking.assignees[i]) continue;  // Skip si déjà assignée
    
    // ... traite groupe ...
    
    // Update après assignation
    tracking.assignees[originalIdx] = true;
    tracking.heureRetourParVehicule.put(v.getId(), newReturnTime);
    tracking.nombreTrajetsParVehicule.put(v.getId(), count + 1);
}
```

### Bénéfices
- **Performance:** Pas de requêtes BD dans boucle
- **Cohérence:** Tous les calculs utilisent même state
- **Simplicité:** Passer 1 objet vs 10 paramètres

---

## 4️⃣ Pattern: EntityManager avec Try-With-Resources

### Concept
JPA manage un **EntityManager** (session BD). On l'utilise avec `try-with-resources` pour auto-close:

```java
// ✅ Bonne pratique
public List<Vehicule> getAllVehicules() {
    try (EntityManager em = JPAUtil.getEntityManager()) {
        return em.createQuery("SELECT v FROM Vehicule v", Vehicule.class)
                 .getResultList();
    }  // em.close() auto si exception
}

// ❌ Mauvais: Peut leak connection
EntityManager em = JPAUtil.getEntityManager();
List<Vehicule> v = em.createQuery(...).getResultList();
// Oublie em.close()? LEAK!
```

### Cycles DB dans le Code

```
PlanificationService.genererRegroupements()
├─ 1x getReservationsByDate()      [SELECT * FROM reservation WHERE date=?]
├─ 1x getAllVehicules()            [SELECT * FROM vehicule]
├─ 1x getDelaiAttente()            [SELECT * FROM configuration_attente]
├─ 1x getAeroportId()              [SELECT id FROM lieu WHERE code='TNR']
│                                  (Cachet après 1ère requête!)
└─ N x getLieuIdByHotelId()        [1x par hôtel unique]
   N x getDistance()               [1x pour chaque paire (lieu1, lieu2)]
   N x getHotelLibelle()           [1x par hôtel unique]
```

**Optimisation:** Cacher `aeroportId` (static) pour éviter 3 requêtes par génération!

---

## 5️⃣ Pattern: Comparateur pour Tri Complexe

### Concept
Quand on a 3+ critères de tri, on enchaîne les `Integer.compare()`:

```java
// Tri: 1) Reportées d'abord, 2) Puis par pax décroissant
reservationsGroupe.sort((r1, r2) -> {
    boolean r1Reportee = idsReservationsReportees.contains(r1.getId());
    boolean r2Reportee = idsReservationsReportees.contains(r2.getId());
    
    if (r1Reportee && !r2Reportee) return -1;  // r1 avant
    if (!r1Reportee && r2Reportee) return 1;   // r2 avant
    
    // Sinon par pax décroissant
    return Integer.compare(r2.getNombre(), r1.getNombre());
});

// Résultat: [reportée1, reportée2, nouveau1 (plus grand), nouveau2 (plus petit)]
```

### Règles
- `return -1` → premier objet prioritaire
- `return 1` → deuxième objet prioritaire
- `return 0` → égalité

---

## 6️⃣ Pattern: Nearest Neighbor (Optimisation Itinéraire)

### Concept
Pour trouver le meilleur *next hop*, on cherche **l'hôtel le plus proche**:

```java
private ReservationPlanningDTO trouverReservationLaPlusProche(
        List<ReservationPlanningDTO> reservations, Integer posId) {
    ReservationPlanningDTO plusProche = null;
    double distMin = Double.MAX_VALUE;

    for (ReservationPlanningDTO rp : reservations) {
        double dist = getDistance(posId, rp.getLieuHotelId());
        if (dist < distMin) {
            distMin = dist;
            plusProche = rp;
        } else if (dist == distMin && plusProche != null) {
            // À distance égale, alphabétique
            if (comparerLibellesAlphabetiquement(...) < 0) {
                plusProche = rp;
            }
        }
    }
    return plusProche;
}
```

### Résultat
```
Itinéraire optimal générée:
  Aéroport → Hôtel C (10 km) → Hôtel A (5 km) → Hôtel B (3 km) → Aéroport
  Distance totale = 10 + 5 + 3 + N = plus efficace
```

---

## 7️⃣ Pattern: Marquer Temporairement (Assignation)

### Concept
Pendant remplissage d'un véhicule, on **marque temporairement** une réservation comme assignée:

```java
while (capaciteRestante > 0) {
    Reservation r = trouverPremiereReservationRestante(...);
    if (r == null) break;
    
    int besoin = remainingPax.get(r);
    int aPrendre = Math.min(besoin, capaciteRestante);
    
    // ⚠️ MARQUER TEMPORAIREMENT
    tracking.assignees[reservations.indexOf(r)] = true;
    
    ajouterReservationAuPlanning(vp, r, aeroportId, delaiAttente, aPrendre);
    remainingPax.put(r, besoin - aPrendre);
    capaciteRestante -= aPrendre;
}
```

### Pourquoi?
Si split, l'index original reste en `tracking.assignees[idx]=true`. Après création nouvelleRésa (reste), elle n'a pas d'index encore.

---

## 8️⃣ Pattern: Refactorisation Fonctionnelle

### Concept
Les **longues méthodes** (> 30 lignes) sont divisées en **sous-fonctions** privées:

```java
// 🔴 Avant: 150 lignes
public List<RegroupementDTO> genererRegroupements(Date date) {
    // Tout fait ici...
    for (...) {
        // 50 lignes de boucle
    }
    // 30 lignes de nettoyage
}

// 🟢 Après: 15 lignes
public List<RegroupementDTO> genererRegroupements(Date date) {
    trierReservationsParHeure(reservations);
    creerGroupes(reservations, ...);
    nettoyerEtRenumeroterGroupes(regroupements, ...);
    return regroupements;
}

// Chaque sous-fonction privée: ~20 lignes max
private void crierGroupes(...) { }
private void nettoyerEtRenumeroterGroupes(...) { }
```

### Bénéfices
- **Lisibilité:** Main logic évidente
- **Testabilité:** Chaque step peut être testé
- **Réutilisabilité:** Sous-fonctions peut être appelée ailleurs
- **Maintenabilité:** Bug = chercher dans 1 fonction, pas 150 lignes

---

## 9️⃣ Pattern: Configuration vs Hardcoding

### ✅ CORRECT
```java
// En BD: configuration_attente.temps_attente_minutes = 30
int delaiAttente = getDelaiAttente();
// Usage: delaiAttente utilisé partout
```

### ❌ INCORRECT
```java
// Hardcodé!
int delaiAttente = 30;
// Difficult changer later!

// Hardcoded vitesse:
double vitesse = 60.0;  // Devrait venir du véhicule!
vitesse = vehicule.getVitesseMoyenne();  // ✅
```

---

## 🔟 Pattern: Cache pour Données Statiques

### Concept
Certaines données ne changent **pas** dans 1 génération (ex: ID aéroport):

```java
private static Integer cacheAeroportId = null;

public Integer getAeroportId() {
    if (cacheAeroportId != null) {
        return cacheAeroportId;  // Retourner cache
    }
    // Requête BD
    cacheAeroportId = result;
    return cacheAeroportId;
}
```

### Contexte
- Génération 1 planning = ~1 sec
- Appels `getAeroportId()` = 3+ fois
- **Cache = 3 requêtes BD économisées!**

---

## Exception Handling

### Pattern: Try-With-Resources + Logs
```java
public List<Reservation> getReservationsByDate(Date date) {
    try (EntityManager em = JPAUtil.getEntityManager()) {
        return em.createQuery("SELECT r FROM Reservation r WHERE r.date = :date", 
                            Reservation.class)
                 .setParameter("date", date)
                 .getResultList();
    } catch (Exception e) {
        System.err.println("Erreur récupération réservations: " + e.getMessage());
        return new ArrayList<>();  // Retourner list vide
    }
}
```

### Politiques
- **Pas d'exceptions lancées** hors du service
- **Retourner Empty List** vs null (NullPointerException risk)
- **Logger** toute erreur BD

---

## Conventions Nommage

### DTO
```java
// public class [Entity]DTO ou [Entity]DetailDTO
RegroupementDTO              // Groupe min data
VehiculePlanningDTO          // Véhicule pour planning
ReservationPlanningDTO       // Réservation pour planning
PlanificationDetailDTO       // Détail complet
```

### Service Methods
```java
// Getters
get[Entity]ById()            // Récupérer 1 entité
getAll[Entities]()           // Lister entités
get[Property]()              // Récupérer propriété

// Créateurs
creer[Entity]()              // Créer entité
generer[Concept]()           // Générer structure complexe
determiner[Choice]()         // Calculer et choisir

// Metteurs à jour
mettreAJour[Concept]()       // Update structure
finaliser[Stage]()           // End-of-stage processing
nettoyer[Collection]()       // Cleanup
```

---

## Lifecycle Entité JPA

```
1. Transient: new Reservation() - pas en DB
2. Managed: em.persist(r) - en session, en DB après flush
3. Detached: em.close() - hors session, toujours en DB
4. Removed: em.remove(r) - marquée deletion

Implication pour code:
- getReservationsByDate() retourne DETACHED entities
- Modifications après close?  LAZY loading fails!
- Solution: Charger toutes data avant em.close()
```

---

**Next:** Lire [04_DATABASE_SCHEMA.md](04_DATABASE_SCHEMA.md) pour modèles BD
