package com.backoffice.util;

import java.sql.Timestamp;
import java.util.Calendar;

import com.backoffice.models.Token;

import jakarta.persistence.EntityManager;

/**
 * Classe utilitaire pour générer et insérer des tokens dans la base de données.
 * Pas d'interface graphique - insertion via main uniquement.
 */
public class TokenGenerator {

    /**
     * Génère et insère un token avec une durée de validité en jours
     * @param joursValidite Nombre de jours de validité (0 ou négatif = jamais expiré)
     * @return Le token généré
     */
    public static Token genererEtInsererToken(int joursValidite) {
        EntityManager em = JPAUtil.getEntityManager();
        Token token = new Token();
        
        try {
            // Générer la valeur du token (14-16 chiffres)
            token.setValeurToken(Token.genererToken());
            
            // Calculer la date d'expiration
            if (joursValidite > 0) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, joursValidite);
                token.setDateExpiration(new Timestamp(cal.getTimeInMillis()));
            } else {
                // null = token qui n'expire jamais
                token.setDateExpiration(null);
            }
            
            em.getTransaction().begin();
            em.persist(token);
            em.getTransaction().commit();
            
            System.out.println("Token généré avec succès:");
            System.out.println("  - ID: " + token.getId());
            System.out.println("  - Valeur: " + token.getValeurToken());
            System.out.println("  - Expiration: " + (token.getDateExpiration() != null ? token.getDateExpiration() : "JAMAIS"));
            
            return token;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erreur lors de la génération du token: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    /**
     * Génère et insère un token avec une valeur spécifique
     * @param valeurToken La valeur du token (14-16 chiffres)
     * @param joursValidite Nombre de jours de validité (0 ou négatif = jamais expiré)
     * @return Le token généré
     */
    public static Token insererTokenAvecValeur(String valeurToken, int joursValidite) {
        EntityManager em = JPAUtil.getEntityManager();
        Token token = new Token();
        
        try {
            token.setValeurToken(valeurToken);
            
            if (joursValidite > 0) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, joursValidite);
                token.setDateExpiration(new Timestamp(cal.getTimeInMillis()));
            } else {
                token.setDateExpiration(null);
            }
            
            em.getTransaction().begin();
            em.persist(token);
            em.getTransaction().commit();
            
            System.out.println("Token inséré avec succès:");
            System.out.println("  - ID: " + token.getId());
            System.out.println("  - Valeur: " + token.getValeurToken());
            System.out.println("  - Expiration: " + (token.getDateExpiration() != null ? token.getDateExpiration() : "JAMAIS"));
            
            return token;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erreur lors de l'insertion du token: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    /**
     * Vérifie si un token est valide
     * @param valeurToken La valeur du token à vérifier
     * @return true si le token est valide, false sinon
     */
    public static boolean verifierToken(String valeurToken) {
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            Token token = em.createQuery(
                    "SELECT t FROM Token t WHERE t.valeurToken = :valeur",
                    Token.class)
                    .setParameter("valeur", valeurToken)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            
            if (token == null) {
                System.out.println("Token non trouvé");
                return false;
            }
            
            boolean valide = token.isValide();
            System.out.println("Token " + (valide ? "VALIDE" : "EXPIRÉ"));
            return valide;
        } finally {
            em.close();
        }
    }

    /**
     * Point d'entrée principal pour générer des tokens
     */
    public static void main(String[] args) {
        System.out.println("=== Générateur de Tokens ===\n");
        
        // Exemple 1: Générer un token valide 30 jours
        System.out.println("1. Génération d'un token valide 30 jours:");
        Token token30j = genererEtInsererToken(30);
        
        System.out.println();
        
        // Exemple 2: Générer un token qui n'expire jamais
        System.out.println("2. Génération d'un token permanent (jamais expiré):");
        Token tokenPermanent = genererEtInsererToken(0);
        
        System.out.println();
        
        // Exemple 3: Insérer un token avec une valeur spécifique
        System.out.println("3. Insertion d'un token avec valeur spécifique:");
        Token tokenSpecifique = insererTokenAvecValeur("12345678901234", 365);
        
        System.out.println();
        
        // Vérifier les tokens
        System.out.println("4. Vérification des tokens:");
        verifierToken(token30j.getValeurToken());
        verifierToken(tokenPermanent.getValeurToken());
        verifierToken("invalide");
        
        System.out.println("\n=== Fin ===");
        
        // Fermer la factory
        JPAUtil.close();
    }
}
