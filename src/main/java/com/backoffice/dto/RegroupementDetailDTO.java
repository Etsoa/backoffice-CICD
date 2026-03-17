package com.backoffice.dto;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.backoffice.models.Reservation;

/**
 * DTO enrichi pour représenter un regroupement avec tous les détails.
 * Extensions du RegroupementDTO avec les assignations de véhicules complètes.
 */
public class RegroupementDetailDTO {
    
    private Integer id;                      // ID du regroupement en BD
    private Integer numeroRegroupement;      // Numéro du groupe (1, 2, 3, etc.)
    private Time heureDebut;                 // Heure de la première réservation
    private Time heureFin;                   // Fin de l'intervalle (heureDebut + délai)
    private Time heureDepart;                // Heure de départ commune (dernière résa)
    private Integer delaiAttenteMinutes;     // Délai appliqué pour ce groupe
    
    // Statistiques
    private Integer nombreReservations;      // Nombre de réservations
    private Integer nombrePassagersTotal;    // Total de passagers
    private Integer nombreVehiculesAssignes; // Nombre de véhicules utilisés
    
    private List<Reservation> reservations;  // Réservations du groupe
    private List<AssignationVehiculeDTO> vehiculesAssignes; // Véhicules avec détails
    private List<Reservation> reservationsNonAssignees;    // Sans véhicule dispo
    
    public RegroupementDetailDTO() {
        this.reservations = new ArrayList<>();
        this.vehiculesAssignes = new ArrayList<>();
        this.reservationsNonAssignees = new ArrayList<>();
    }

    public RegroupementDetailDTO(Integer numeroRegroupement, Time heureDebut, Time heureFin) {
        this();
        this.numeroRegroupement = numeroRegroupement;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumeroRegroupement() {
        return numeroRegroupement;
    }

    public void setNumeroRegroupement(Integer numeroRegroupement) {
        this.numeroRegroupement = numeroRegroupement;
    }

    public Time getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(Time heureDebut) {
        this.heureDebut = heureDebut;
    }

    public Time getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(Time heureFin) {
        this.heureFin = heureFin;
    }

    public Time getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(Time heureDepart) {
        this.heureDepart = heureDepart;
    }

    public Integer getDelaiAttenteMinutes() {
        return delaiAttenteMinutes;
    }

    public void setDelaiAttenteMinutes(Integer delaiAttenteMinutes) {
        this.delaiAttenteMinutes = delaiAttenteMinutes;
    }

    public Integer getNombreReservations() {
        return nombreReservations;
    }

    public void setNombreReservations(Integer nombreReservations) {
        this.nombreReservations = nombreReservations;
    }

    public Integer getNombrePassagersTotal() {
        return nombrePassagersTotal;
    }

    public void setNombrePassagersTotal(Integer nombrePassagersTotal) {
        this.nombrePassagersTotal = nombrePassagersTotal;
    }

    public Integer getNombreVehiculesAssignes() {
        return nombreVehiculesAssignes;
    }

    public void setNombreVehiculesAssignes(Integer nombreVehiculesAssignes) {
        this.nombreVehiculesAssignes = nombreVehiculesAssignes;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void ajouterReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public List<AssignationVehiculeDTO> getVehiculesAssignes() {
        return vehiculesAssignes;
    }

    public void setVehiculesAssignes(List<AssignationVehiculeDTO> vehiculesAssignes) {
        this.vehiculesAssignes = vehiculesAssignes;
    }

    public void ajouterVehiculeAsigne(AssignationVehiculeDTO assignation) {
        this.vehiculesAssignes.add(assignation);
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

    /**
     * Mettre à jà jour l'heure de départ si la nouvelle réservation est plus tard
     */
    public void maybeUpdateHeureDepart(Reservation reservation) {
        if (heureDepart == null || reservation.getHeure().after(heureDepart)) {
            heureDepart = reservation.getHeure();
        }
    }

    @Override
    public String toString() {
        return "RegroupementDetailDTO [numeroRegroupement=" + numeroRegroupement + ", heureDebut=" + heureDebut
                + ", heureDepart=" + heureDepart + ", nombreReservations=" + nombreReservations
                + ", nombreVehiculesAssignes=" + nombreVehiculesAssignes + "]";
    }
}
