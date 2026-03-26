# 🎯 Règles Métier et Logique de Planification

## Vue d'ensemble
Le système génère un planning journalier pour assigner des réservations clients à des véhicules. Les règles optimisent:
- **Remplissage max des véhicules** (pas de gaspillage de places)
- **Efficacité temps** (regroupement par intervalle d'attente)
- **Priorité aux reportées** (clients restants de groupes précédents)

---

## 1️⃣ Concept Clé: Le Regroupement (Groupe)

### Définition
Un **regroupement** = groupe de réservations **partant au même créneau horaire** dans **UN seul intervalle de temps**.

```
Groupe 1: 08:00 - 08:30  → Réservations Client1[7], Client2[20]
          ↓               → Véhicule3 [12 pers de Client2]
                          → Véhicule4 [8 pers de Client2 + 1 de Client3]
                          ↓ Tous partent à 09:24 (heure max du groupe)

Groupe 2: 09:24 - 09:54  → Réservations Client2[reste 0], Client3[reste 19]
          (heure véhicule dispo)
```

### Propriétés d'un Regroupement
```java
class RegroupementDTO {
    int numeroGroupe;                    // 1, 2, 3, ...
    Time heureDepart;                    // Heure minimale de départ
    Time finIntervalle;                  // heureDepart + délaiAttente
    int delaiAttente;                    // 30 min (config BD)
    List<Reservation> reservations;      // Réservations du groupe
    List<VehiculePlanningDTO> vehiculesAssignes;  // Véhicules utilisés
    int nombreReservationsReportees;     // Nombre de REPORTÉES (reste groupe précédent)
}
```

---

## 2️⃣ Le Délai d'Attente (Configuration Clé)

### Règle
```
délaiAttente = 30 minutes (configuration_attente table)
```

### Utilisation
1. **Création intervalle groupe:**
   ```
   finIntervalle = heureDepart + 30min
   ```
   → Toute réservation entre `heureDepart` et `finIntervalle` entre dans ce groupe

2. **Détermination disponibilité véhicules:**
   ```
   heureLimite = heureDepartGroupe + 30min
   Véhicule dispo si: heureRetour <= heureLimite
   ```

### Pas de Fallback
- **❌ ANCIEN:** Fallback à DB parameter table
- **✅ NOUVEAU:** Récupère uniquement depuis `configuration_attente`
- Si table vide → **Exception**

---

## 3️⃣ Algorithme de Génération (PlanificationService)

### Étape 1: Préparation
```
1. Charger réservations du jour
2. Trier par heure ASC, référence ASC (ordre stable)
3. Initialiser tracking:
   - assignees[] = false pour toutes
   - indicesReportees = set vide
   - heureRetourParVehicule = {} init avec heure_disponibilite
   - nombreTrajetsParVehicule = {} init avec 0
```

### Étape 2: Boucle Principale (Création Groupes)
```java
for (Reservation i : reservations) {
    if (assignees[i]) continue;  // Déjà assignée
    
    // DÉTERMINER HEURE DE DÉPART DU GROUPE
    if (indicesReportees != empty) {
        // IL Y A DES REPORTÉES
        heureDepartGroupe = min(heureRetourParVehicule.values)
        // = heure où le prochain véhicule est libre
    } else {
        // PAS DE REPORTÉES
        heureDepartGroupe = reservations[i].heure
        // = heure de la prochaine réservation non assignée
    }
    
    // CRÉER LE GROUPE
    groupe = creerIntervalleGroupe(i, heureDepartGroupe)
    
    // DÉTERMINER VÉHICULES DISPONIBLES
    vehiculesDisponibles = determinerVehiculesDisponibles(
        heureDepartGroupe, 
        heureRetourParVehicule, 
        delaiAttente
    )
    
    // ASSIGNER LES VÉHICULES
    if (!vehiculesDisponibles.isEmpty()) {
        assignerVehiculesAuGroupe(groupe, vehiculesDisponibles)
        finaliserGroupe(groupe)  // Calc itinéraires
    }
    
    // TRACKER LES REPORTÉES
    mettreAJourIndicesReportees(groupe)
}
```

### Étape 3: Nettoyage
- Retirer groupes vides
- Renuméroter groupes (1, 2, 3...)
- Dedupliquer réservations non assignées

---

## 4️⃣ Création de l'Intervalle de Groupe

### Logique
```
Intervalle = [heureDepartGroupe, heureDepartGroupe + 30min]

Réservations du groupe = {
  1. Toutes REPORTÉES du groupe précédent (priorité absolue)
  2. + Réservations nouvelles qui rentrent dans intervalle
     (par heure croissante)
}
```

### Exemple
```
État: indicesReportees = {1, 4}
      reservations[1] = Client2[8 pers], 09:00
      reservations[2] = Client1[5 pers], 09:10
      reservations[3] = Client3[1 pers], 09:35  ← Hors intervalle!
      reservations[4] = Client4[10 pers], 09:15

groupe.reservations = {
  reservations[1],  ✅ Reportée
  reservations[4],  ✅ Reportée
  reservations[2],  ✅ Dans intervalle [09:24, 09:54]
  // reservations[3] sauté car > finIntervalle
}

groupe.nombreReservationsReportees = 2  ← Nombre de reportées
```

---

## 5️⃣ Assignation Intelligente des Véhicules

### Étape 1: Trier les Réservations
```
Priorité 1: Réservations REPORTÉES (finish previous assignment)
Priorité 2: Réservations NOUVELLES (trier par pax décroissant)

Exemple:
  - Client2[8 pers] - REPORTÉE (priorité 1)
  - Client3[1 pers] - REPORTÉE (priorité 1)
  - Client1[7 pers] - NOUVEAU (priorité 2)
  - Client4[10 pers] - NOUVEAU (priorité 2) ← Plus grand
```

### Étape 2: Trier les Véhicules
```
Priorité 1: Véhicules DÉJÀ UTILISÉS (moins de trajets)
Priorité 2: Véhicules NOUVEAUX (capacité décroissante)

Exemple:
  - Véhicule3 [12 places] - 1 trajet utilisé déjà ✅ Priorité
  - Véhicule1 [5 places] - jamais utilisé
  - Véhicule4 [9 places] - jamais utilisé (+ grand que 5)
```

### Étape 3: Remplir les Véhicules (Greedy Algorithm)
```
Pour chaque véhicule (par ordre de priorité):
  Tant que véhicule a place et réservations restantes:
    
    Choix réservation (algorithme):
    1. Premier passage: prendre réservation par ordre (priorité).
    2. Passage suivants: prendre la PLUS PROCHE (nearest neighbor)
       du lieu actuel du véhicule.
       
    Placer passagers = min(besoin_réservation, place_disponible)
    
    Si reste passagers < besoin:
      → SPLIT: créer nouvelle réservation pour prochain groupe
      → Marquer original comme partiellement assignee
```

### Exemple
```
Groupe 2 - État:
  Réservations: [Client2[8], Client3[1], Client1[7]]
  Véhicules: [Vehicle3(12), Vehicle1(5), Vehicle4(9)]
  
Vehicle3 (12 places):
  1. Client2[8] → prend 8, reste place = 4 ✅
  2. Client3[1] → prend 1, reste place = 3 ✅
  3. Client1[7] → prend 3 (max 3 places), Client1 → split [4 restant]
  → Vehicle3 = {Client2[8], Client3[1], Client1[3]}

Vehicle1 (5 places):
  1. Client1[4 restant] → prend 4, reste place = 1
  2. Client4[10] → prend 1, Client4 → split [9 restant]
  → Vehicle1 = {Client1[4], Client4[1]}

Vehicle4 (9 places):
  1. Client4[9 restant] → prend 9 ✅
  → Vehicle4 = {Client4[9]}
  
Final:
  assignees = {Client2: ✅, Client3: ✅, Client1: ✅, Client4: ✅}
  Vehicle3: 12 places (rempli 100%)
  Vehicle1: 5 places (rempli 100%)
  Vehicle4: 9 places (rempli 100%)
```

---

## 6️⃣ Finalization: Calcul des Itinéraires

### Étape 1: Déterminer l'Heure de Départ du Groupe
```
heureDepartGroupe = max(
  dernière heure de réservation du groupe,
  heure de retour du dernier véhicule utilisé
)

Raison: Tous les véhicules d'un groupe partent au même créneau!
```

### Étape 2: Trier les Hôtels par Proximité (Nearest Neighbor)
```
Pour chaque véhicule:
  Départ: Aéroport TNR
  
  Ordre visite hôtels = nearest neighbor:
    Postion courante = TNR
    Tant qu'hôtels non visités:
      Hôtel suivant = celui le plus proche de position courante
      Distance égale? → ordre alphabétique libellé
      
  Fin: Retour TNR
  
Temps trajet = distance / vitesse_moyenne (vitesse_moyenne du véhicule)
```

### Étape 3: Calculer Horaires et Distances
```
heureCourante = heureDepartGroupe

Pour chaque arrêt hôtel:
  tempsTrajet = distance / vitesse
  heureArrivee = heureCourante + tempsTrajet
  heureCourante = heureArrivee
  distanceTotale += distance
  
heureRetourAeroport = heureCourante + distanceRetour/vitesse
```

---

## 7️⃣ Gestion des Restes et Splits

### Split = Réservation Partiellement Assignée

```js
Réservation originale: Client2[20 pers]
Assignée groupe 1: -12 pers (Vehicle3)
Assignée groupe 1: -8 pers (Vehicle4)

Reste = 20 - 12 - 8 = 0 ✅ Entièrement assignée

---

Réservation originale: Client2[20 pers]
Assignée groupe 1: -12 pers (Vehicle3)
Assignée groupe 1: -5 pers (Vehicle4 partiel)

Reste = 20 - 12 - 5 = 3 pers → SPLIT!

Action:
  1. Créer nouvelle réservation "Client2[3]" (même properties)
  2. Remplacer dans liste reservations: reservations[idx] = Client2[3]
  3. Cette nouvelle réservation sera REPORTÉE au groupe suivant
  4. Ajouter Client2[3] aux "reservationsNonAssignees" du groupe (pour UI)
```

### Non-Assignée = Réservation Jamais Touchée
```
Cas: Aucun véhicule dispo ou capacité insuffisante

Action:
  1. Ajouter aux "reservationsNonAssignees" du groupe (pour UI)
  2. Inclure dans indicesReportees pour groupe suivant
  3. Ne pas modifier la liste principale (reste 20 pers intakt)
```

---

## 8️⃣ Priorités et Critères

### Priorité Carburant (Sélection Véhicule)
```
1. Diesel (D) - priorité maximale
2. Essence (Es) - priorité intermédiaire
3. Autres (H, El) - priorité faible (random si tie)
```

### Priorité Hôtel (Itinéraire)
```
1. Distance minimale depuis position courante
2. Si égale: ordre alphabétique libellé
```

### Vitesse Moyenne
```
✅ Source: Véhicule.vitesseMoyenne (DB)
   Jamais hardcodée!
   
Type: double
Valeur test: 50.0 km/h
```

---

## 9️⃣ Données Test de Référence

**Date:** 2026-03-19  
**Délai Attente:** 30 min  
**Vitesse véhicules:** 50 km/h

### Réservations
| Ref | Client | Pers | Hotel | Heure |
|-----|--------|------|-------|-------|
| #1 | C001 | 7 | Hotel 1 | 09:00 |
| #2 | C002 | 20 | Hotel 2 | 08:00 |
| #3 | C003 | 3 | Hotel 1 | 09:10 |
| #4 | C004 | 6 | Hotel 1 | 09:15 |
| #5 | C005 | 2 | Hotel 1 | 09:20 |
| #6 | C006 | 12 | Hotel 1 | 13:30 |

### Véhicules
| ID | Places | Carburant | Heure Dispo |
|----|--------|-----------|-------------|
| V1 | 5 | Diesel | 00:00 |
| V2 | 5 | Essence | 00:00 |
| V3 | 12 | Diesel | 00:00 |
| V4 | 9 | Diesel | 00:00 |
| V5 | 12 | Essence | 00:00 |

### Résultat Attendu
```
Groupe 1 (08:00-08:30):
  Vehicle3: Client2[12] @ Hôtel2
  
Groupe 2 (09:24-09:54):
  Vehicle3: Client1[7] + Client3[3] + Client4[2]
  Vehicle4: Client2[8] + Client3[1]
  Vehicle1: Client1[5 restant part 1] + Client4[6 part 1]
  Vehicle2: Client1[5 restant part 2] + Client5[3]
  
Groupe 3 (13:00-13:30):
  Vehicle3: Client6[12]
```

---

## 🔟 Points Critiques à Retenir

| Point | Valeur/Règle |
|-------|--------------|
| **Délai d'attente** | Configuration BD (30 min), pas de fallback |
| **Heure de départ groupe** | MAX(dern. résa, retour véhicule) |
| **Vitesse véhicule** | DB, jamais hardcodée |
| **Priorité reportées** | Absolue (avant nouvelles) |
| **Remplissage véhicule** | Greedy (100% ou 0) |
| **Nearest neighbor** | Distance minimale + alpha si égal |
| **Split** | Remplacer résa, créer nouvelle entrée |
| **Non-assignée** | Jamais modifié, reporté exact |

---

**Next:** Lire [03_FRAMEWORK_ARCHITECTURE.md](03_FRAMEWORK_ARCHITECTURE.md) pour comprendre les patterns DTO/Service
