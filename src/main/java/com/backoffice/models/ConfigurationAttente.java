package com.backoffice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Configuration du temps d'attente pour le regroupement des réservations.
 * Sprint 5 - Gestion du temps d'attente et regroupement des réservations.
 * 
 * Cette table permet de configurer le délai d'attente (en minutes) 
 * utilisé pour regrouper les réservations dans un même intervalle.
 * 
 * Exemple: Si un vol est prévu à 7h35 et le temps_attente_minutes = 30,
 * alors toutes les réservations entre 7h35 et 8h05 seront regroupées.
 */
@Entity
@Table(name = "configuration_attente")
public class ConfigurationAttente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "temps_attente_minutes", nullable = false)
    private Integer tempsAttenteMinutes;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    public ConfigurationAttente() {
    }

    public ConfigurationAttente(Integer tempsAttenteMinutes) {
        this.tempsAttenteMinutes = tempsAttenteMinutes;
        this.actif = true;
    }

    public ConfigurationAttente(Integer tempsAttenteMinutes, String description) {
        this.tempsAttenteMinutes = tempsAttenteMinutes;
        this.description = description;
        this.actif = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTempsAttenteMinutes() {
        return tempsAttenteMinutes;
    }

    public void setTempsAttenteMinutes(Integer tempsAttenteMinutes) {
        this.tempsAttenteMinutes = tempsAttenteMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}
