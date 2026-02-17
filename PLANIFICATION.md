# Système de Planification des Véhicules - Documentation

## Vue d'ensemble

Ce système implémente une solution complète de planification des transferts aéroport-hôtel avec optimisation automatique des véhicules.

## Nouvelles Fonctionnalités

### 1. Modèle de Données

#### Table `lieu` (Lieux)
- **id**: Identifiant unique
- **code**: Code du lieu (ex: TNR pour Ivato)
- **libelle**: Nom complet du lieu

#### Table `distance`
- **id**: Identifiant unique
- **lieu_depart**: Référence au lieu de départ
- **lieu_arrivee**: Référence au lieu d'arrivée
- **km**: Distance en kilomètres

#### Mise à jour Table `vehicule`
- Ajout du champ **vitesse_moyenne** (en km/h)
- Valeur par défaut: 60 km/h

### 2. Règles de Gestion

#### Affectation des Véhicules
Le système respecte les règles suivantes pour l'affectation des véhicules:

1. **Capacité optimale**: 
   - Le véhicule doit avoir au minimum le nombre de places requis
   - Sélection du véhicule avec le nombre de places le plus proche pour éviter le gaspillage
   - Les clients ne sont jamais séparés

2. **Priorité Diesel**:
   - Ordre de priorité: Diesel > Hybride > Essence > Électrique
   - À capacité égale, les véhicules diesel sont toujours privilégiés

3. **Temps d'attente (TA)**:
   - Temps d'attente standard: **30 minutes**
   - Appliqué à chaque réservation

### 3. Calculs Automatiques

#### Heure de départ de l'aéroport
```
Heure départ = Heure RDV - Temps trajet - Temps d'attente
```

#### Heure de retour à l'hôtel
```
Heure retour hôtel = Heure RDV + Temps d'attente + Temps trajet retour
```

#### Temps de trajet
```
Temps trajet (min) = (Distance km / Vitesse moyenne km/h) × 60
```

#### Heure de retour définitif à l'aéroport
L'heure de retour du dernier client servi par le véhicule.

### 4. Interface Utilisateur

#### Page de Planification (`/planification`)

**Fonctionnalités**:
- Sélection de date via calendrier
- Affichage des véhicules utilisés avec leurs caractéristiques
- Liste détaillée des réservations par véhicule
- Informations complètes sur chaque transfert

**Informations affichées par véhicule**:
- Référence du véhicule
- Nombre de places
- Type de carburant
- Vitesse moyenne
- Nombre de réservations

**Informations par réservation**:
- Référence de réservation
- Hôtel de destination
- Nombre de personnes
- Heure de rendez-vous
- Heure de départ de l'aéroport
- Heure de retour prévue
- Distance en km
- Temps d'attente

### 5. Exemple de Données

#### Lieux
```sql
TNR - Ivato (Aéroport)
COL - Colbert
NOV - Novotel
IBL - Ibis
LOK - Lokanga
```

#### Distances (depuis l'aéroport)
```
Ivato → Colbert: 18.5 km
Ivato → Novotel: 16.2 km
Ivato → Ibis: 17.8 km
Ivato → Lokanga: 19.3 km
```

#### Véhicules disponibles
```
VH-2026-001: 5 places, Essence, 55 km/h
VH-2026-002: 7 places, Diesel, 60 km/h
VH-2026-003: 5 places, Électrique, 50 km/h
VH-2026-004: 4 places, Hybride, 58 km/h
VH-2026-005: 12 places, Diesel, 55 km/h
VH-2026-006: 18 places, Diesel, 50 km/h
```

### 6. Architecture Technique

#### Couches

**Modèles** (`com.backoffice.models`):
- `Lieu.java`: Entité JPA pour les lieux
- `Distance.java`: Entité JPA pour les distances
- `Vehicule.java`: Mis à jour avec vitesse_moyenne

**DTOs** (`com.backoffice.dto`):
- `ReservationPlanningDTO.java`: Données enrichies de réservation
- `VehiculePlanningDTO.java`: Planning par véhicule

**Services** (`com.backoffice.service`):
- `PlanificationService.java`: Logique métier complète
  - Récupération des réservations par date
  - Tri des véhicules par priorité
  - Calcul des distances et temps
  - Génération du planning optimisé

**Contrôleurs** (`com.backoffice.controller`):
- `PlanificationController.java`: Endpoint `/planification`

**Vues** (`templates/planification`):
- `index.jsp`: Interface moderne et responsive

### 7. Utilisation

1. **Déployer la base de données**:
   ```bash
   psql -U postgres -f database/reset.sql
   psql -U postgres -f database/database.sql
   ```

2. **Compiler et déployer l'application**:
   ```bash
   mvn clean package
   ```

3. **Accéder à la planification**:
   - URL: `http://localhost:8080/backoffice/planification`
   - Ou via le menu principal de l'application

4. **Sélectionner une date** et cliquer sur "Afficher le Planning"

### 8. Améliorations Futures Possibles

- Export du planning en PDF
- Gestion des conflits horaires
- Optimisation multi-critères (coût, émissions CO2)
- Temps d'attente variable selon l'hôtel
- Alertes en temps réel
- Suivi GPS des véhicules
- Historique des plannings

## Support

Pour toute question ou problème, consulter la documentation technique ou contacter l'équipe de développement.
