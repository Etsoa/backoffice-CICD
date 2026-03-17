package com.backoffice.models;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "suivi_trajet_vehicule")
public class SuiviTrajetVehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "planification_id", nullable = false)
    private Planification planification;

    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @Column(name = "date_planification", nullable = false)
    private Date datePlanification;

    @Column(name = "nombre_regroupements_assignes")
    private Integer nombreRegroupementsAssignes;

    @Column(name = "nombre_passagers_total")
    private Integer nombrePassagersTotal;

    @Column(name = "distance_totale_km", precision = 10, scale = 2)
    private Double distanceTotaleKm;

    @Column(name = "temps_total_heures", precision = 5, scale = 2)
    private Double tempsTotalHeures;

    @Column(name = "heure_premiere_utilisation")
    private Time heurePremiereUtilisation;

    @Column(name = "heure_derniere_retour")
    private Time heureDerniereRetour;

    @Column(name = "date_creation", nullable = false)
    private Timestamp dateCreation;

    // Constructeurs
    public SuiviTrajetVehicule() {
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public SuiviTrajetVehicule(Planification planification, Vehicule vehicule, Date datePlanification) {
        this();
        this.planification = planification;
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

    public Planification getPlanification() {
        return planification;
    }

    public void setPlanification(Planification planification) {
        this.planification = planification;
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

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "SuiviTrajetVehicule [id=" + id + ", vehicule=" + vehicule.getReference() + ", datePlanification="
                + datePlanification + ", nombreRegroupementsAssignes=" + nombreRegroupementsAssignes
                + ", nombrePassagersTotal=" + nombrePassagersTotal + ", distanceTotaleKm=" + distanceTotaleKm + "]";
    }
}
