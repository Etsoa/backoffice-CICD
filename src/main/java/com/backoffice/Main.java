package com.backoffice;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        // Récupérer le port depuis Railway (variable PORT) ou utiliser 8080 par défaut
        String portEnv = System.getenv("PORT");
        int port = portEnv != null ? Integer.parseInt(portEnv) : 8080;

        System.out.println("Démarrage du serveur sur le port: " + port);

        // Créer et configurer Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Active le connecteur par défaut

        // Configurer le contexte de l'application
        String webappDir = new File("src/main/webapp").getAbsolutePath();
        String contextPath = "";
        
        Context ctx = tomcat.addWebapp(contextPath, webappDir);
        
        // Ajouter les classes compilées au classpath
        File classesDir = new File("target/classes");
        if (classesDir.exists()) {
            StandardRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(
                resources, "/WEB-INF/classes", classesDir.getAbsolutePath(), "/"
            ));
            ctx.setResources(resources);
        }

        // Démarrer Tomcat
        tomcat.start();
        System.out.println("Serveur démarré avec succès sur http://localhost:" + port);
        System.out.println("Contexte: " + contextPath);
        
        // Garder le serveur actif
        tomcat.getServer().await();
    }
}
