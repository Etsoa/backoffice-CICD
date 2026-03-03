# GUIDE DE DÉPLOIEMENT SUR RAILWAY

## MODIFICATIONS EFFECTUÉES

### 1. JPAUtil.java
- Ajout de la détection de `DATABASE_URL` (variable Railway)
- Priorité: Railway > application.properties > fallback local

### 2. Main.java (NOUVEAU)
- Classe principale qui démarre Tomcat Embedded
- Lit le `PORT` depuis les variables d'environnement Railway
- Fallback sur port 8080 en local

### 3. pom.xml
- Changement de packaging: `war` → `jar`
- Ajout de Tomcat Embedded (tomcat-embed-core, tomcat-embed-jasper)
- Ajout du maven-shade-plugin pour créer un JAR exécutable
- Servlet API scope changé: `provided` → `compile`

### 4. Dockerfile (NOUVEAU)
- Image: eclipse-temurin:17-jdk
- Build Maven automatique dans le conteneur
- Démarrage via `java -jar target/backoffice-CICD.jar`

### 5. .dockerignore (NOUVEAU)
- Exclut target/ et fichiers inutiles du contexte Docker

---

## ÉTAPES DE DÉPLOIEMENT

### ÉTAPE 1: COMPILER ET TESTER EN LOCAL

```bash
# Compiler le projet
mvn clean package

# Vérifier que le JAR est créé
ls target/backoffice-CICD.jar

# Tester en local (avec PostgreSQL local actif)
java -jar target/backoffice-CICD.jar
```

Ouvrir: http://localhost:8080

---

### ÉTAPE 2: POUSSER SUR GITHUB

```bash
git add .
git commit -m "Configure for Railway deployment"
git push origin main
```

---

### ÉTAPE 3: CRÉER UN PROJET RAILWAY

1. Aller sur https://railway.app/
2. Cliquer sur **New Project**
3. Choisir **Deploy from GitHub Repo**
4. Sélectionner votre repository `backoffice-CICD`

Railway détectera automatiquement le Dockerfile et lancera le build.

---

### ÉTAPE 4: AJOUTER POSTGRESQL SUR RAILWAY

1. Dans votre projet Railway, cliquer sur **New**
2. Choisir **Database** > **PostgreSQL**
3. Railway créera automatiquement la variable `DATABASE_URL`

**IMPORTANT**: Cette variable sera automatiquement injectée dans votre service backoffice.

---

### ÉTAPE 5: CONFIGURER LES VARIABLES D'ENVIRONNEMENT

Railway injecte automatiquement:
- `PORT` (ex: 3000, 8080, etc.)
- `DATABASE_URL` (ex: postgresql://user:pass@host:5432/railway)

Votre code Java lit déjà ces variables via:
- `System.getenv("PORT")` dans Main.java
- `System.getenv("DATABASE_URL")` dans JPAUtil.java

**Aucune configuration manuelle nécessaire** ✓

---

### ÉTAPE 6: INITIALISER LA BASE DE DONNÉES

Railway crée une base PostgreSQL vide. Vous devez charger votre schéma:

1. Dans Railway, ouvrir le service **PostgreSQL**
2. Cliquer sur **Connect** > copier la **DATABASE_URL**
3. Utiliser un client PostgreSQL (DBeaver, pgAdmin, ou CLI):

```bash
psql "postgresql://user:pass@host:5432/railway"
```

4. Exécuter vos scripts SQL:

```sql
\i database/database.sql
```

Ou copier-coller le contenu de `database/database.sql` dans la console.

---

### ÉTAPE 7: VÉRIFIER LE DÉPLOIEMENT

1. Aller dans **Deployments** sur Railway
2. Attendre que le statut soit **Success** ✓
3. Cliquer sur le service pour voir l'URL publique
4. Visiter l'URL (ex: https://backoffice-cicd-production.up.railway.app)

---

### ÉTAPE 8: CONSULTER LES LOGS

En cas d'erreur:
1. Ouvrir le service dans Railway
2. Cliquer sur **View Logs**
3. Chercher les erreurs Java/Hibernate

Erreurs courantes:
- **Database connection failed**: Vérifier que PostgreSQL est bien ajouté au projet
- **Port already in use**: Railway gère ça automatiquement
- **ClassNotFoundException**: Vérifier que maven-shade-plugin a bien packagé les dépendances

---

## COMMANDES UTILES

### Compiler localement
```bash
mvn clean package
```

### Tester le JAR en local
```bash
java -jar target/backoffice-CICD.jar
```

### Tester avec un PORT personnalisé
```bash
PORT=9090 java -jar target/backoffice-CICD.jar
```

### Tester avec une DATABASE_URL custom
```bash
DATABASE_URL=postgresql://user:pass@localhost:5432/test java -jar target/backoffice-CICD.jar
```

---

## NOTES IMPORTANTES

1. **Framework JAR inclus**: Le JAR `framework-sprint-1.jar` doit être dans `src/main/webapp/WEB-INF/lib/` pour que Maven le trouve (scope=system)

2. **Hibernate auto-update**: Le paramètre `hibernate.hbm2ddl.auto=update` créera automatiquement les tables si elles n'existent pas

3. **Show SQL désactivé**: En production, `hibernate.show_sql=false` pour éviter les logs verbeux

4. **Port dynamique**: Railway change le PORT à chaque redéploiement, votre code s'adapte automatiquement

5. **Logs**: Utiliser `System.out.println()` pour logger, Railway capture stdout

---

## ROLLBACK / MODIFICATION

Pour mettre à jour le code:
1. Modifier le code en local
2. `git commit && git push`
3. Railway redéploie automatiquement

Pour revenir en arrière:
1. Aller dans **Deployments**
2. Cliquer sur un déploiement précédent
3. Cliquer sur **Redeploy**

---

## TROUBLESHOOTING

### "Class com.backoffice.Main not found"
→ Vérifier que maven-shade-plugin a bien configuré le MANIFEST.MF

### "Connection refused to database"
→ Vérifier que PostgreSQL est ajouté au projet Railway
→ Vérifier que DATABASE_URL est bien injectée (voir Variables)

### "Port 8080 already in use"
→ Ne devrait jamais arriver sur Railway (PORT dynamique)
→ En local, tuer le processus: `lsof -ti:8080 | xargs kill` (Mac/Linux)

### "Template not found: reservations/list.jsp"
→ Vérifier que `src/main/webapp/` est bien présent dans le JAR
→ Vérifier Main.java ligne `webappDir`

---

FIN DU GUIDE
