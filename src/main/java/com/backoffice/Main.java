package com.backoffice;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        String portEnv = System.getenv("PORT");
        int port = portEnv != null ? Integer.parseInt(portEnv) : 8080;

        System.out.println("Démarrage du serveur sur le port: " + port);

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();

        // Déterminer les chemins (dev local vs Docker)
        String webappDir;
        String classesPath;
        if (new File("src/main/webapp").exists()) {
            webappDir = new File("src/main/webapp").getAbsolutePath();
            classesPath = new File("target/classes").getAbsolutePath();
        } else {
            webappDir = new File("webapp").getAbsolutePath();
            classesPath = new File("classes").getAbsolutePath();
        }

        // addWebapp lit web.xml → FrontServlet, CorsFilter, welcome-file, context-params
        // + initialise JSP (JasperInitializer) automatiquement
        Context ctx = tomcat.addWebapp("", webappDir);

        // Ajouter les classes compilées dans WEB-INF/classes
        // Les classes projet sont EXCLUES du fat JAR (sauf Main.class)
        // → elles n'existent QUE ici → un seul classloader → pas de ClassCastException
        File classesDir = new File(classesPath);
        if (classesDir.exists()) {
            StandardRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(
                resources, "/WEB-INF/classes", classesDir.getAbsolutePath(), "/"
            ));
            ctx.setResources(resources);
        }

        tomcat.start();
        System.out.println("Serveur démarré sur http://localhost:" + port);
        tomcat.getServer().await();
    }
}
