# 📁 Structure du Projet Backoffice

## Vue d'ensemble
Projet **Jakarta EE** avec **JPA/Hibernate** pour la gestion des réservations et de la planification de véhicules. Architecture **MVC** avec couches Service/DAO.

## Architecture Globale

```
backoffice-CICD/
├── src/main/java/com/backoffice/
│   ├── Main.java                          # Point d'entrée (Tomcat)
│   ├── controller/                         # Contrôleurs REST/JSP
│   │   ├── ClientController.java
│   │   ├── HotelController.java
│   │   ├── ReservationController.java
│   │   ├── VehiculeController.java
│   │   ├── PlanificationController.java    # 📌 Planification des véhicules
│   │   └── ControllerTest.java
│   ├── service/                            # Logique métier
│   │   ├── PlanificationService.java       # 🔥 CŒUR: Génération planification
│   │   ├── ReservationService.java
│   │   └── ...
│   ├── dto/                                # Data Transfer Objects
│   │   ├── RegroupementDTO.java            # Groupes de réservations
│   │   ├── ReservationPlanningDTO.java     # Réservation pour planning
│   │   ├── VehiculePlanningDTO.java        # Véhicule avec réservations
│   │   ├── PlanificationDetailDTO.java
│   │   └── ...
│   ├── models/                             # Entités JPA
│   │   ├── Reservation.java
│   │   ├── Vehicule.java
│   │   ├── Hotel.java
│   │   ├── Lieu.java
│   │   ├── Distance.java
│   │   ├── ConfigurationAttente.java       # ⚙️ Délai d'attente entre groupes
│   │   ├── Planification.java
│   │   ├── Regroupement.java
│   │   └── ...
│   ├── filter/
│   │   └── CorsFilter.java                 # Gestion CORS
│   └── util/
│       └── JPAUtil.java                    # Gestion EntityManager
│
├── src/main/resources/
│   ├── application.properties               # Config Base de Données
│   ├── persistence.xml                      # Config JPA/Hibernate
│
├── src/main/webapp/
│   ├── templates/
│   │   ├── index.jsp
│   │   ├── planification/
│   │   │   ├── index.jsp                   # 📊 Affichage planning
│   │   │   ├── vehicules_non_assignes.jsp
│   │   │   └── reservations_non_assignees.jsp
│   │   ├── reservations/
│   │   ├── vehicules/
│   │   └── hotels/
│   └── WEB-INF/
│       ├── web.xml                         # Config Tomcat
│       └── lib/                            # Dépendances JARs
│
├── database/                                # Scripts SQL
│   ├── database.sql                        # Schéma BD
│   ├── sprint7_test_data.sql               # 📍 Données de test
│   ├── reset.sql
│   └── update_*.sql
│
├── pom.xml                                 # Maven: Dépendances
├── Dockerfile                              # Docker: Conteneurisation
└── docs/                                   # 📚 Documentation
```

## Stack Technologique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| **Serveur Web** | Apache Tomcat | 8.0+ |
| **Framework Web** | Jakarta EE | 8.0+ |
| **ORM** | Hibernate + JPA | 5.3+ |
| **Base de Données** | PostgreSQL | 12+ |
| **Build Tool** | Maven | 3.6+ |
| **Langage** | Java | 11+ |
| **Template View** | JSP | 2.3+ |

## Packages et Responsabilités

### 📦 `controller/`
- **PlanificationController** → Endpoint `/planification` pour affichage planning
- Récupère date du formulaire → appelle `PlanificationService` → retourne DTOs
- Gère affichage véhicules / réservations non assignées

### 📦 `service/`
- **PlanificationService** → Cœur de la logique de planification
  - Génère les regroupements (groupes de réservations)
  - Assigne véhicules selon règles de priorité
  - Calcule itinéraires (nearest neighbor)
  - Gère les restes et les reportées
- **ReservationService** → Gestion des réservations
- **VehiculeService** → Gestion des véhicules

### 📦 `dto/`
- **RegroupementDTO** → Groupe (intervalle horaire) avec réservations
- **VehiculePlanningDTO** → Véhicule + ses réservations + itinéraire
- **ReservationPlanningDTO** → 1 réservation dans planning (horaires + hôtel)
- **PlanificationDetailDTO** → Détail complet pour UI

### 📦 `models/`
- **Reservation** → client_id, nb_pers, date, heure, hotel_id
- **Vehicule** → place, vitesse_moyenne, type_carburant, heure_disponibilité
- **ConfigurationAttente** → temps_attente_minutes (délai entre groupes)
- **Distance** → km entre 2 lieux
- **Hotel** → libellé, lié à Lieu via matching de nom
- **Lieu** → aéroport TNR + hôtels (utilisé pour calculs distance)

## Convention de Nommage

### Fichiers Java
- Classes: `PascalCase` (ex: `PlanificationService`)
- Interfaces: `PascalCase` avec `I` prefix optionnel
- Packages: `lowercase.hierarchy` (ex: `com.backoffice.service`)

### Bases de Données
- Tables: `snake_case` en français
- Colonnes: `snake_case` en français
- Clés étrangères: `[table]_id`
- Timestamps: `created_at`, `updated_at`

### URL Routes
- `/planification` → Affichage du planning
- `/reservations` → CRUD réservations
- `/vehicules` → CRUD véhicules
- `/hotels` → CRUD hôtels

## Configuration Environnement

### `application.properties`
```properties
# Database PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/backoffice
spring.datasource.username=user
spring.datasource.password=password

# Hibernate
hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
hibernate.format_sql=true
```

### `persistence.xml`
- Définit la `PersistenceUnit` pour JPA
- Configure Hibernate (dialect, show_sql, hbm2ddl.auto)
- Listes les entités JPA

### `web.xml`
- Déclare servlets Tomcat
- Configure filtres (CORS)
- Points d'entrée JSP

## Cycles de Build

### Maven Clean Build
```bash
mvn clean compile
```
- Nettoie `/target`
- Compile Java → `.class`
- Copie resources

### Deployment Tomcat
```bash
mvn package
# Génère WAR, déployé dans Tomcat
```

### Docker Build
```bash
docker build -t backoffice:latest .
docker run -p 8080:8080 backoffice:latest
```

## Points d'Entrée Critiques

| Fichier | Responsabilité |
|---------|-----------------|
| **PlanificationController** | Endpoint rendering planning |
| **PlanificationService.genererRegroupements()** | 🔥 Génération complète |
| **PlanificationService.assignerVehiculesAuGroupe()** | Assignation intelligente |
| **src/main/webapp/templates/planification/index.jsp** | Vue affichage |
| **database/sprint7_test_data.sql** | Données test de référence |

---

**Next:** Lire [BUSINESS_RULES.md](02_BUSINESS_RULES.md) pour comprendre la logique métier
