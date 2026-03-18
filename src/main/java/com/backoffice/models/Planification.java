package com.backoffice.models;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "planification")
public class Planification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date_planification", nullable = false, unique = true)
    private Date datePlanification;

    @Column(name = "date_creation", nullable = false)
    private Timestamp dateCreation;

    @Column(name = "date_modification", nullable = false)
    private Timestamp dateModification;

    @Column(name = "statut", nullable = false, length = 20)
    private String statut;  // DRAFT, ACTIVE, ARCHIVED

    @Column(name = "delai_attente_utilise")
    private Integer delaiAttenteUtilise;

    @Column(name = "nombre_regroupements")
    private Integer nombreRegroupements;

    @Column(name = "nombre_reservations_total")
    private Integer nombreReservationsTotal;

    @Column(name = "nombre_reservations_assignees")
    private Integer nombreReservationsAssignees;

    @Column(name = "nombre_vehicules_utilises")
    private Integer nombreVehiculesUtilises;

    @Column(name = "actif", nullable = false)
    private Boolean actif;

    // Constructeurs
    public Planification() {
        this.statut = "DRAFT";
        this.actif = true;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
        this.dateModification = new Timestamp(System.currentTimeMillis());
    }

    public Planification(Date datePlanification, Integer delaiAttenteUtilise) {
        this();
        this.datePlanification = datePlanification;
        this.delaiAttenteUtilise = delaiAttenteUtilise;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDatePlanification() {
        return datePlanification;
    }

    public void setDatePlanification(Date datePlanification) {
        this.datePlanification = datePlanification;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Timestamp getDateModification() {
        return dateModification;
    }

    public void setDateModification(Timestamp dateModification) {
        this.dateModification = dateModification;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Integer getDelaiAttenteUtilise() {
        return delaiAttenteUtilise;
    }

    public void setDelaiAttenteUtilise(Integer delaiAttenteUtilise) {
        this.delaiAttenteUtilise = delaiAttenteUtilise;
    }

    public Integer getNombreRegroupements() {
        return nombreRegroupements;
    }

    public void setNombreRegroupements(Integer nombreRegroupements) {
        this.nombreRegroupements = nombreRegroupements;
    }

    public Integer getNombreReservationsTotal() {
        return nombreReservationsTotal;
    }

    public void setNombreReservationsTotal(Integer nombreReservationsTotal) {
        this.nombreReservationsTotal = nombreReservationsTotal;
    }

    public Integer getNombreReservationsAssignees() {
        return nombreReservationsAssignees;
    }

    public void setNombreReservationsAssignees(Integer nombreReservationsAssignees) {
        this.nombreReservationsAssignees = nombreReservationsAssignees;
    }

    public Integer getNombreVehiculesUtilises() {
        return nombreVehiculesUtilises;
    }

    public void setNombreVehiculesUtilises(Integer nombreVehiculesUtilises) {
        this.nombreVehiculesUtilises = nombreVehiculesUtilises;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    @Override
    public String toString() {
        return "Planification [id=" + id + ", datePlanification=" + datePlanification + ", statut=" + statut
                + ", nombreRegroupements=" + nombreRegroupements + ", nombreVehiculesUtilises=" + nombreVehiculesUtilises + "]";
    }
}
