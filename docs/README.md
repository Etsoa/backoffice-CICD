# 📚 Documentation Complète - Index Maître

Bienvenue dans la documentation du projet **Backoffice CICD**! Ce document centralise tout et guide vers les sections pertinentes selon votre besoin.

---

## 🎯 Par Rôle/Besoin

### Je suis une **IA/Dev** recevant ce projet pour la PREMIÈRE FOIS
**Ordre de lecture recommandé:**
1. ⭐ **[01_PROJECT_STRUCTURE.md](01_PROJECT_STRUCTURE.md)** - Comprendre la structure
2. ⭐ **[02_BUSINESS_RULES.md](02_BUSINESS_RULES.md)** - Apprendre les règles métier
3. **[03_FRAMEWORK_ARCHITECTURE.md](03_FRAMEWORK_ARCHITECTURE.md)** - Patterns utilisés
4. **[04_DATABASE_SCHEMA.md](04_DATABASE_SCHEMA.md)** - Modèles BD
5. **[05_IMPLEMENTATION_GUIDE.md](05_IMPLEMENTATION_GUIDE.md)** - Comment contribuer

**Temps:** ~2-3 heures de lecture complète

---

### Je dois **AJOUTER une feature**
1. Lire la spec (doc utilisateur ou issue)
2. Consulter [02_BUSINESS_RULES.md](02_BUSINESS_RULES.md) section "Point Critique" si feature touche logique
3. Consulter [05_IMPLEMENTATION_GUIDE.md](05_IMPLEMENTATION_GUIDE.md) section "Ajouter une Nouvelle Règle" ou équivalent
4. Implémenter + Tester
5. Vérifier checklist qualité [05_IMPLEMENTATION_GUIDE.md#checklist-qualité-code]

---

### Je dois **FIXER un bug**
1. Identifier le composant (réservation? véhicule? planification?)
2. Utiliser [01_PROJECT_STRUCTURE.md](01_PROJECT_STRUCTURE.md) pour localiser le fichier
3. Consulter [02_BUSINESS_RULES.md](02_BUSINESS_RULES.md) pour comprendre le comportement attendu
4. Utiliser [05_IMPLEMENTATION_GUIDE.md#debugging-workflow](05_IMPLEMENTATION_GUIDE.md#8️⃣-debugging-workflow) pour débugger
5. Tester avec `sprint7_test_data.sql`

---

### Je dois **DÉPLOYER en production**
1. Lire [05_IMPLEMENTATION_GUIDE.md#déployer-sur-production](05_IMPLEMENTATION_GUIDE.md#9️⃣-déployer-sur-production)
2. Vérifier checklist [05_IMPLEMENTATION_GUIDE.md#checklist-qualité-code]
3. Exécuter les steps de déploiement

---

### Je dois **MODIFIER l'algorithme de planification**
1. Document clé: **[02_BUSINESS_RULES.md](02_BUSINESS_RULES.md)**
   - Section "Algorithme de Génération" → Comprendre workflow
   - Section "Assignation Intelligente" → Logique véhicule
   - Section "Finalization" → Itinéraires
2. Identifier exactement quelle étape modifier
3. Consulter [05_IMPLEMENTATION_GUIDE.md#modifier-la-logique-dassignation](05_IMPLEMENTATION_GUIDE.md#4️⃣-modifier-la-logique-dassignation)
4. Implémenter + Tester sur `sprint7_test_data.sql`

---

### Je dois **MODIFIER la base de données**
1. Consulter [04_DATABASE_SCHEMA.md](04_DATABASE_SCHEMA.md)
2. Si modification table existante: [04_DATABASE_SCHEMA.md#6️⃣-scripts-utiles](04_DATABASE_SCHEMA.md#6️⃣-scripts-utiles)
3. Si nouvelle table: [05_IMPLEMENTATION_GUIDE.md#ajouter-une-table-bd](05_IMPLEMENTATION_GUIDE.md#6️⃣-ajouter-une-table-bd)
4. Mettre à jour modèle JPA correspondant
5. Créer/Modifier service method pour accéder données

---

### Je dois **COMPRENDRE le flow application**
```
Utilisateur → PlanificationController.genererPlanning()
             ↓
             PlanificationService.genererRegroupements()
             ├─ Charge réservations (getReservationsByDate)
             ├─ Charge véhicules (getAllVehicules)
             ├─ Charge config (getDelaiAttente)
             └─ Boucle création groupes:
                ├─ Crée intervalle (creerIntervalleGroupe)
                ├─ Assigne véhicules (assignerVehiculesAuGroupe)
                ├─ Calcule itinéraires (calculerItineraire)
                └─ Finalise groupe (finaliserGroupe)
             ↓
             RegroupementDTOs retournées
             ↓
             PlanificationController retourne VehiculePlanningDTOs
             ↓
             JSP affiche planning
```

**Détail:** Lire [02_BUSINESS_RULES.md#3️⃣-algorithme-de-génération](02_BUSINESS_RULES.md#3️⃣-algorithme-de-génération)

---

## 🗺️ Structure Documentation

```
docs/
├── 01_PROJECT_STRUCTURE.md       # Architecture, packages, layout
├── 02_BUSINESS_RULES.md          # Règles métier, algorithmes
├── 03_FRAMEWORK_ARCHITECTURE.md  # Patterns, DTOs, service layer
├── 04_DATABASE_SCHEMA.md         # Modèles JPA, tables, requêtes clés
├── 05_IMPLEMENTATION_GUIDE.md    # Comment contribuer, ajouter features
└── README.md (ce fichier)
```

---

## 📍 Index par Concept

### Regroupement (Groupe)
- Concept: [02_BUSINESS_RULES.md#1️⃣-concept-clé-le-regroupement-groupe](02_BUSINESS_RULES.md#1️⃣-concept-clé-le-regroupement-groupe)
- Code: `RegroupementDTO` [03_FRAMEWORK_ARCHITECTURE.md#2️⃣-pattern-dto](03_FRAMEWORK_ARCHITECTURE.md#2️⃣-pattern-dto)
- Implémentation: `creerIntervalleGroupe()` dans PlanificationService

### Délai d'Attente
- Règle: [02_BUSINESS_RULES.md#2️⃣-le-délai-dattente-configuration-clé](02_BUSINESS_RULES.md#2️⃣-le-délai-dattente-configuration-clé)
- BD: `configuration_attente` table [04_DATABASE_SCHEMA.md#table-configuration_attente-](04_DATABASE_SCHEMA.md#table-configuration_attente-)
- Code: `getDelaiAttente()` PlanificationService

### Assignation Intelligente
- Règle: [02_BUSINESS_RULES.md#5️⃣-assignation-intelligente-des-véhicules](02_BUSINESS_RULES.md#5️⃣-assignation-intelligente-des-véhicules)
- Code: `assignerVehiculesAuGroupe()` PlanificationService
- Patterns: [03_FRAMEWORK_ARCHITECTURE.md](03_FRAMEWORK_ARCHITECTURE.md)

### Priorités
- Reportées: [02_BUSINESS_RULES.md#7️⃣-gestion-des-restes-et-splits](02_BUSINESS_RULES.md#7️⃣-gestion-des-restes-et-splits)
- Carburant: [02_BUSINESS_RULES.md#8️⃣-priorités-et-critères](02_BUSINESS_RULES.md#8️⃣-priorités-et-critères)
- Code: Voir `trierReservationsParPriorite()` et `trierVehiculesParPriorite()`

### Nearest Neighbor
- Concept: [02_BUSINESS_RULES.md#6️⃣-finalization-calcul-des-itinéraires](02_BUSINESS_RULES.md#6️⃣-finalization-calcul-des-itinéraires)
- Pattern: [03_FRAMEWORK_ARCHITECTURE.md#6️⃣-pattern-nearest-neighbor](03_FRAMEWORK_ARCHITECTURE.md#6️⃣-pattern-nearest-neighbor)
- Code: `calculerItineraire()` PlanificationService

### DTOs
- Vue générale: [03_FRAMEWORK_ARCHITECTURE.md#2️⃣-pattern-dto](03_FRAMEWORK_ARCHITECTURE.md#2️⃣-pattern-dto)
- VehiculePlanningDTO: [03_FRAMEWORK_ARCHITECTURE.md#hiérarchie-dtos---planification](03_FRAMEWORK_ARCHITECTURE.md#hiérarchie-dtos---planification)
- Code: Les fichiers dans `src/main/java/com/backoffice/dto/`

---

## 🔧 Résolution Courante de Problèmes

| Problème | Section |
|----------|---------|
| "Planning affiche 0 groupes" | [05_IMPLEMENTATION_GUIDE.md#problème-planning-génère-0-groupes](05_IMPLEMENTATION_GUIDE.md#problème-planning-génère-0-groupes) |
| "Distance toujours 0" | [05_IMPLEMENTATION_GUIDE.md#problème-distance-toujours-0](05_IMPLEMENTATION_GUIDE.md#problème-distance-toujours-0) |
| "Véhicule jamais assigné" | [05_IMPLEMENTATION_GUIDE.md#problème-véhicule-jamais-assigné](05_IMPLEMENTATION_GUIDE.md#problème-véhicule-jamais-assigné) |
| "Vitesse hardcodée à 60 km/h" | [04_DATABASE_SCHEMA.md#table-vehicule](04_DATABASE_SCHEMA.md#table-vehicule) |
| Je dois ajouter feature | [05_IMPLEMENTATION_GUIDE.md#2️⃣-ajouter-une-nouvelle-règle-métier](05_IMPLEMENTATION_GUIDE.md#2️⃣-ajouter-une-nouvelle-règle-métier) |
| Je dois modifier algorithme | [05_IMPLEMENTATION_GUIDE.md#4️⃣-modifier-la-logique-dassignation](05_IMPLEMENTATION_GUIDE.md#4️⃣-modifier-la-logique-dassignation) |

---

## 📊 Données Test de Référence

**All test data:** [04_DATABASE_SCHEMA.md#5️⃣-données-test-script-insert](04_DATABASE_SCHEMA.md#5️⃣-données-test-script-insert)

**Date:** 2026-03-19  
**6 réservations**, **5 véhicules**, **2 hôtels**  
**Résultat attendu:**
- Groupe 1 (08:00): Véhicule3 = Client2[12]
- Groupe 2 (09:24): Véhicule3, 4, 1, 2 = Splits Client2, split Client1, Client3-5
- Groupe 3 (13:00): Véhicule3 = Client6[12]

---

## ⚡ Quick Links

| Fichier | Quand | Pourquoi |
|---------|-------|---------|
| [01_PROJECT_STRUCTURE.md](01_PROJECT_STRUCTURE.md) | Première fois | Structure projet |
| [02_BUSINESS_RULES.md](02_BUSINESS_RULES.md) | Modifier logique | Comprendre règles |
| [03_FRAMEWORK_ARCHITECTURE.md](03_FRAMEWORK_ARCHITECTURE.md) | Developer patterns | Patterns utilisés |
| [04_DATABASE_SCHEMA.md](04_DATABASE_SCHEMA.md) | Query/schema | Modèles BD |
| [05_IMPLEMENTATION_GUIDE.md](05_IMPLEMENTATION_GUIDE.md) | Ajouter feature | Step-by-step |

---

## 🚀 Commandes Quick-Start

```bash
# Build
mvn clean compile

# Test sur données test
mvn clean package -DskipTests
# → Déployer target/backoffice.war sur Tomcat
# → Accèder: http://localhost:8080/backoffice/planification?date=2026-03-19

# Debug
mvn clean compile -X  # Verbose output

# Charger données test
psql -U user -d backoffice -f database/sprint7_test_data.sql
```

---

## 📝 Notes Importantes

### Centre Névralgique: PlanificationService
- Fichier: `src/main/java/com/backoffice/service/PlanificationService.java`
- Méthode principale: `genererRegroupements(Date date)`
- Toute la logique présente ici!

### Ne **JAMAIS** hardcoder:
- ✗ `vitesse = 60.0;` ← Doit venir de Vehicule.vitesseMoyenne
- ✗ `delaiAttente = 30;` ← Doit venir de ConfigurationAttente
- ✓ Utiliser configuration BD!

### Respect des DTOs:
- Modèles JPA **JAMAIS** sortent de la couche service
- Toujours utiliser DTOs pour présentation
- Découple persistance et UI

---

## ✅ Checklist pour Nouvelle IA

Si vous êtes une IA recevant ce projet:

- [ ] Lire [01_PROJECT_STRUCTURE.md](01_PROJECT_STRUCTURE.md) (15 min)
- [ ] Lire [02_BUSINESS_RULES.md](02_BUSINESS_RULES.md) (45 min)
- [ ] Lire [03_FRAMEWORK_ARCHITECTURE.md](03_FRAMEWORK_ARCHITECTURE.md) (30 min)
- [ ] Parcourir [04_DATABASE_SCHEMA.md](04_DATABASE_SCHEMA.md) (20 min)
- [ ] Consulter [05_IMPLEMENTATION_GUIDE.md](05_IMPLEMENTATION_GUIDE.md) au besoin
- [ ] Compiler code: `mvn clean compile` ✓
- [ ] Charger test data: `psql ... sprint7_test_data.sql` ✓
- [ ] Vous êtes **prêt à contribuer** 🚀

---

## 📞 Support / Questions

Si bloqué:
1. Consulter la section "Index par Concept" ci-dessus
2. Utiliser Ctrl+F pour chercher mot-clé
3. Consulter [05_IMPLEMENTATION_GUIDE.md#8️⃣-debugging-workflow](05_IMPLEMENTATION_GUIDE.md#8️⃣-debugging-workflow)
4. Si toujours bloqué, créer une issue avec logs/contexte

---

**Dernière mise à jour:** Mars 2026  
**Version:** 1.0  
**Auteur:** Équipe Développement
