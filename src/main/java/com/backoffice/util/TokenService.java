package com.backoffice.util;

import com.backoffice.models.Token;

import jakarta.persistence.EntityManager;

/**
 * Service pour la validation des tokens
 */
public class TokenService {

    /**
     * Vérifie si un token est valide (existe et non expiré)
     * 
     * @param valeurToken La valeur du token à vérifier
     * @return true si le token est valide, false sinon
     */
    public static boolean isTokenValide(String valeurToken) {
        if (valeurToken == null || valeurToken.trim().isEmpty()) {
            return false;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            Token token = em.createQuery(
                    "SELECT t FROM Token t WHERE t.valeurToken = :valeur",
                    Token.class)
                    .setParameter("valeur", valeurToken.trim())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (token == null) {
                return false;
            }

            return token.isValide();
        } finally {
            em.close();
        }
    }

    /**
     * Récupère un token par sa valeur
     * 
     * @param valeurToken La valeur du token
     * @return Le token ou null si non trouvé
     */
    public static Token getToken(String valeurToken) {
        if (valeurToken == null || valeurToken.trim().isEmpty()) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Token t WHERE t.valeurToken = :valeur",
                    Token.class)
                    .setParameter("valeur", valeurToken.trim())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}
