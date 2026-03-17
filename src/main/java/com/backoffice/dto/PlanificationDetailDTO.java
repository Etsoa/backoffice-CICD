package com.backoffice.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.backoffice.models.Reservation;

/**
 * DTO pour représenter une planification complète avec tous les détails.
 * Utilisé pour afficher la planification chargée ou générée.
 * Contient tous les regroupements, véhicules assignés et itinéraires.
 */
public class PlanificationDetailDTO {
    
    private Integer id;                          // ID de la planification en BD
    private Date datePlanification;              // Date de la planification
    private String statut;                       // DRAFT, ACTIVE, ARCHIVED
    private Integer delaiAttenteUtilise;         // Délai d'attente appliqué (minutes)
    
    // Statistiques
    private Integer nombreRegroupements;         // Nombre total de groupes
    private Integer nombreReservationsTotal;     // Total réservations
    private Integer nombreReservationsAssignees; // Réservations assignées
    private Integer nombreVehiculesUtilises;     // Nombre de véhicules utilisés
    
    private List<RegroupementDetailDTO> regroupements;
    private List<Reservation> reservationsNonAssignees; // Réservations sans véhicule
    
    public PlanificationDetailDTO() {
        this.regroupements = new ArrayList<>();
        this.reservationsNonAssignees = new ArrayList<>();
        this.statut = "DRAFT";
    }

    public PlanificationDetailDTO(Date datePlanification, Integer delaiAttenteUtilise) {
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

    public List<RegroupementDetailDTO> getRegroupements() {
        return regroupements;
    }

    public void setRegroupements(List<RegroupementDetailDTO> regroupements) {
        this.regroupements = regroupements;
    }

    public void ajouterRegroupement(RegroupementDetailDTO regroupement) {
        this.regroupements.add(regroupement);
    }

    public List<Reservation> getReservationsNonAssignees() {
        return reservationsNonAssignees;
    }

    public void setReservationsNonAssignees(List<Reservation> reservationsNonAssignees) {
        this.reservationsNonAssignees = reservationsNonAssignees;
    }

    public void ajouterReservationNonAssignee(Reservation reservation) {
        this.reservationsNonAssignees.add(reservation);
    }

    @Override
    public String toString() {
        return "PlanificationDetailDTO [datePlanification=" + datePlanification + ", statut=" + statut
                + ", nombreRegroupements=" + nombreRegroupements + ", nombreReservationsTotal="
                + nombreReservationsTotal + ", nombreVehiculesUtilises=" + nombreVehiculesUtilises + "]";
    }
}
