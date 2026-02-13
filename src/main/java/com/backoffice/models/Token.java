package com.backoffice.models;

import java.sql.Timestamp;
import java.util.Random;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "valeur_token", nullable = false, unique = true, length = 16)
    private String valeurToken;

    @Column(name = "date_expiration")
    private Timestamp dateExpiration;

    public Token() {
    }

    public Token(String valeurToken, Timestamp dateExpiration) {
        this.valeurToken = valeurToken;
        this.dateExpiration = dateExpiration;
    }

    /**
     * Génère automatiquement un token de 14 à 16 chiffres
     */
    public static String genererToken() {
        Random random = new Random();
        // Générer entre 14 et 16 chiffres
        int longueur = 14 + random.nextInt(3); // 14, 15 ou 16
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longueur; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Vérifie si le token est expiré
     * @return true si expiré, false sinon (null = jamais expiré)
     */
    public boolean isExpire() {
        if (dateExpiration == null) {
            return false; // Token sans expiration = jamais expiré
        }
        return new Timestamp(System.currentTimeMillis()).after(dateExpiration);
    }

    /**
     * Vérifie si le token est valide (non expiré)
     */
    public boolean isValide() {
        return !isExpire();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValeurToken() {
        return valeurToken;
    }

    public void setValeurToken(String valeurToken) {
        this.valeurToken = valeurToken;
    }

    public Timestamp getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Timestamp dateExpiration) {
        this.dateExpiration = dateExpiration;
    }
}
