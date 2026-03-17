package com.backoffice.dto;

import java.sql.Date;
import java.sql.Time;

import com.backoffice.models.Vehicule;

/**
 * DTO pour représenter le suivi des trajets d'un véhicule pour une planification donnée.
 * Contient les statistiques aggregées du véhicule pour la journée (distance totale, passagers, etc.).
 */
public class SuiviTrajetVehiculeDTO {
    
    private Integer id;                      // ID du suivi en BD
    private Vehicule vehicule;               // Le véhicule
    private Date datePlanification;          // Date de la planification
    private Integer nombreRegroupementsAssignes; // Nombre de groupes assignés
    private Integer nombrePassagersTotal;    // Total de passagers transportés
    private Double distanceTotaleKm;         // Distance totale parcourue
    private Double tempsTotalHeures;         // Temps total en heures
    private Time heurePremiereUtilisation;   // Heure du 1er départ
    private Time heureDerniereRetour;        // Heure du dernier retour
    
    public SuiviTrajetVehiculeDTO() {
    }
    
    public SuiviTrajetVehiculeDTO(Vehicule vehicule, Date datePlanification) {
        this.vehicule = vehicule;
        this.datePlanification = datePlanification;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Date getDatePlanification() {
        return datePlanification;
    }

    public void setDatePlanification(Date datePlanification) {
        this.datePlanification = datePlanification;
    }

    public Integer getNombreRegroupementsAssignes() {
        return nombreRegroupementsAssignes;
    }

    public void setNombreRegroupementsAssignes(Integer nombreRegroupementsAssignes) {
        this.nombreRegroupementsAssignes = nombreRegroupementsAssignes;
    }

    public Integer getNombrePassagersTotal() {
        return nombrePassagersTotal;
    }

    public void setNombrePassagersTotal(Integer nombrePassagersTotal) {
        this.nombrePassagersTotal = nombrePassagersTotal;
    }

    public Double getDistanceTotaleKm() {
        return distanceTotaleKm;
    }

    public void setDistanceTotaleKm(Double distanceTotaleKm) {
        this.distanceTotaleKm = distanceTotaleKm;
    }

    public Double getTempsTotalHeures() {
        return tempsTotalHeures;
    }

    public void setTempsTotalHeures(Double tempsTotalHeures) {
        this.tempsTotalHeures = tempsTotalHeures;
    }

    public Time getHeurePremiereUtilisation() {
        return heurePremiereUtilisation;
    }

    public void setHeurePremiereUtilisation(Time heurePremiereUtilisation) {
        this.heurePremiereUtilisation = heurePremiereUtilisation;
    }

    public Time getHeureDerniereRetour() {
        return heureDerniereRetour;
    }

    public void setHeureDerniereRetour(Time heureDerniereRetour) {
        this.heureDerniereRetour = heureDerniereRetour;
    }

    /**
     * Retourne le taux d'utilisation du véhicule (passagers / capacité)
     */
    public double getTauxUtilisation() {
        if (vehicule == null || vehicule.getPlace() == null || nombrePassagersTotal == null) {
            return 0.0;
        }
        return (double) nombrePassagersTotal / vehicule.getPlace();
    }

    /**
     * Retourne la distance moyenne par passage
     */
    public double getDistanceMoyenneParPassager() {
        if (nombrePassagersTotal == null || nombrePassagersTotal == 0 || distanceTotaleKm == null) {
            return 0.0;
        }
        return distanceTotaleKm / nombrePassagersTotal;
    }

    @Override
    public String toString() {
        return "SuiviTrajetVehiculeDTO [vehicule=" + vehicule.getReference() + ", datePlanification="
                + datePlanification + ", nombreRegroupementsAssignes=" + nombreRegroupementsAssignes
                + ", nombrePassagersTotal=" + nombrePassagersTotal + ", distanceTotaleKm=" + distanceTotaleKm
                + ", tempsTotalHeures=" + tempsTotalHeures + "]";
    }
}
