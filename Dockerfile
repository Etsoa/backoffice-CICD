# === STAGE 1: BUILD ===
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Installer Maven
RUN apt-get update && apt-get install -y maven && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copier pom.xml d'abord (cache des dépendances)
COPY pom.xml .

# Copier le framework JAR (scope system, nécessaire pour la compilation)
COPY src/main/webapp/WEB-INF/lib/ ./src/main/webapp/WEB-INF/lib/

# Télécharger les dépendances
RUN mvn dependency:resolve -DincludeScope=system || true

# Copier tout le source
COPY src ./src

# Compiler le projet
RUN mvn clean package -DskipTests

# === STAGE 2: RUNTIME ===
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copier le JAR compilé (contient Main.class + dépendances, PAS les classes projet)
COPY --from=build /app/target/backoffice-CICD.jar app.jar

# Copier les templates webapp (JSP, web.xml, etc.)
COPY --from=build /app/src/main/webapp ./webapp

# Copier les classes compilées du projet (contrôleurs, modèles, etc.)
COPY --from=build /app/target/classes ./classes

# Railway fournit PORT dynamiquement
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
