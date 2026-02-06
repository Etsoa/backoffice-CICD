package com.backoffice.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JPAUtil {

    private static EntityManagerFactory emf;

    static {
        try {
            Properties props = new Properties();
            InputStream is = JPAUtil.class.getClassLoader().getResourceAsStream("application.properties");
            if (is != null) {
                props.load(is);
                is.close();
            }

            Map<String, String> config = new HashMap<>();
            config.put("jakarta.persistence.jdbc.driver", props.getProperty("db.driver", "org.postgresql.Driver"));
            config.put("jakarta.persistence.jdbc.url", props.getProperty("db.url", "jdbc:postgresql://localhost:5432/cicd"));
            config.put("jakarta.persistence.jdbc.user", props.getProperty("db.username", "postgres"));
            config.put("jakarta.persistence.jdbc.password", props.getProperty("db.password", "postgres"));
            config.put("hibernate.dialect", props.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
            config.put("hibernate.hbm2ddl.auto", props.getProperty("hibernate.hbm2ddl.auto", "update"));
            config.put("hibernate.show_sql", props.getProperty("hibernate.show_sql", "true"));
            config.put("hibernate.format_sql", props.getProperty("hibernate.format_sql", "true"));

            emf = Persistence.createEntityManagerFactory("backoffice-pu", config);
        } catch (Exception e) {
            throw new RuntimeException("Erreur initialisation JPA: " + e.getMessage(), e);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
