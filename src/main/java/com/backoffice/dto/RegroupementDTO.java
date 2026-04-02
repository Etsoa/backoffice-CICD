package com.backoffice.dto;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.backoffice.models.Reservation;

/**
 * Sprint 5 - DTO représentant un regroupement de réservations.
 * 
 * Un regroupement contient toutes les réservations d'un même intervalle de
 * temps.
 * Tous les véhicules assignés à ce regroupement partiront à la même heure.
 */
public class RegroupementDTO {

    private int numeroGroupe;
    private Time heureDebut; // Heure de la première réservation (début de l'intervalle)
    private Time heureFin; // heureDebut + délai d'attente (fin de l'intervalle)
    private Time heureDepart; // Heure de départ commune = heure de la DERNIÈRE réservation
    private int delaiAttenteMinutes;
    private String typeDeclencheur; // VOL | RETOUR_VEHICULE | DEBUT_DISPONIBILITE
    private boolean contientVolDansIntervalle;

    private List<Reservation> reservations;
    private List<VehiculePlanningDTO> vehiculesAssignes;
    private List<Reservation> reservationsNonAssignees; // Réservations sans véhicule disponible

    public RegroupementDTO() {
        this.reservations = new ArrayList<>();
        this.vehiculesAssignes = new ArrayList<>();
        this.reservationsNonAssignees = new ArrayList<>();
    }

    public RegroupementDTO(int numeroGroupe, Time heureDebut, Time heureFin, int delaiAttenteMinutes) {
        this();
        this.numeroGroupe = numeroGroupe;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.delaiAttenteMinutes = delaiAttenteMinutes;
    }

    /**
     * Ajouter une réservation au regroupement et mettre à jour l'heure de départ
     */
    public void ajouterReservation(Reservation resa) {
        this.reservations.add(resa);

        // Mettre à jour l'heure de départ = heure de la dernière réservation
        if (heureDepart == null || resa.getHeure().after(heureDepart)) {
            heureDepart = resa.getHeure();
        }
    }

    /**
     * Nombre total de personnes dans ce regroupement
     */
    public int getNombrePersonnesTotal() {
        int total = 0;
        for (Reservation r : reservations) {
            total += r.getNombre();
        }
        return total;
    }

    /**
     * Nombre de personnes assignées à des véhicules
     */
    public int getNombrePersonnesAssignees() {
        int total = 0;
        for (VehiculePlanningDTO vp : vehiculesAssignes) {
            total += vp.calculerNombrePassagers();
        }
        return total;
    }

    /**
     * Nombre de personnes non assignées (sans véhicule)
     */
    public int getNombrePersonnesNonAssignees() {
        int total = 0;
        for (Reservation r : reservationsNonAssignees) {
            total += r.getNombre();
        }
        return total;
    }

    // Getters et Setters

    public int getNumeroGroupe() {
        return numeroGroupe;
    }

    public void setNumeroGroupe(int numeroGroupe) {
        this.numeroGroupe = numeroGroupe;
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

    public int getDelaiAttenteMinutes() {
        return delaiAttenteMinutes;
    }

    public void setDelaiAttenteMinutes(int delaiAttenteMinutes) {
        this.delaiAttenteMinutes = delaiAttenteMinutes;
    }

    public String getTypeDeclencheur() {
        return typeDeclencheur;
    }

    public void setTypeDeclencheur(String typeDeclencheur) {
        this.typeDeclencheur = typeDeclencheur;
    }

    public String getTypeDeclencheurLabel() {
        if (typeDeclencheur == null) {
            return "Non défini";
        }
        return switch (typeDeclencheur) {
            case "VOL" -> "Vol (réservation)";
            case "RETOUR_VEHICULE" -> "Retour véhicule";
            case "DEBUT_DISPONIBILITE" -> "Début disponibilité véhicule";
            default -> typeDeclencheur;
        };
    }

    public boolean isContientVolDansIntervalle() {
        return contientVolDansIntervalle;
    }

    public void setContientVolDansIntervalle(boolean contientVolDansIntervalle) {
        this.contientVolDansIntervalle = contientVolDansIntervalle;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public List<VehiculePlanningDTO> getVehiculesAssignes() {
        return vehiculesAssignes;
    }

    public void setVehiculesAssignes(List<VehiculePlanningDTO> vehiculesAssignes) {
        this.vehiculesAssignes = vehiculesAssignes;
    }

    public void ajouterVehicule(VehiculePlanningDTO vehicule) {
        this.vehiculesAssignes.add(vehicule);
    }

    public List<Reservation> getReservationsNonAssignees() {
        return reservationsNonAssignees;
    }

    public void setReservationsNonAssignees(List<Reservation> reservationsNonAssignees) {
        this.reservationsNonAssignees = reservationsNonAssignees;
    }

    public void ajouterReservationNonAssignee(Reservation resa) {
        // Ne PAS enlever de la liste principale. Une réservation peut être dans le
        // groupe ET non assignée (ou partiellement).
        // this.reservations.removeIf(r -> r.getId().equals(resa.getId()));

        // Ajouter à la liste des non assignées
        this.reservationsNonAssignees.add(resa);
    }

    public boolean hasReservationsNonAssignees() {
        return !this.reservationsNonAssignees.isEmpty();
    }

    /**
     * Description du groupe pour affichage
     */
    public String getDescription() {
        return String.format("Groupe %d : %s - %s (%d réservations, %d personnes)",
                numeroGroupe, heureDebut, heureFin, reservations.size(), getNombrePersonnesTotal());
    }
}
