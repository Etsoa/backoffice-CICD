package com.backoffice.dto;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.backoffice.models.Vehicule;

/**
 * DTO enrichi pour représenter l'assignation d'un véhicule à un regroupement
 * avec tous les détails de sa mission (itinéraire, timings, distances, statistiques).
 */
public class AssignationVehiculeDTO {
    
    private Integer id;                          // ID de l'assignation en BD
    private Vehicule vehicule;                   // Le véhicule assigné
    private Integer numeroOrdre;                 // 1er, 2e véhicule du regroupement
    private Time heureDepartAeroport;            // Heure de départ du véhicule
    private Time heureRetourAeroport;            // Heure de retour à l'aéroport
    private Double distanceTotaleKm;             // Distance totale du trajet
    private Integer tempsTotalMinutes;           // Temps total du trajet en minutes
    private Integer nombrePassagersTransportes;  // Nombre total de passagers
    private Integer nombreTrajetEffectues;       // Nombre de trajets ce jour
    private List<ItineraireArretDTO> itineraire; // Détail des arrêts
    private List<ReservationPlanningDTO> reservations; // Les réservations assignées

    public AssignationVehiculeDTO() {
        this.itineraire = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.nombreTrajetEffectues = 1;
    }

    public AssignationVehiculeDTO(Vehicule vehicule, Integer numeroOrdre) {
        this();
        this.vehicule = vehicule;
        this.numeroOrdre = numeroOrdre;
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

    public Integer getNumeroOrdre() {
        return numeroOrdre;
    }

    public void setNumeroOrdre(Integer numeroOrdre) {
        this.numeroOrdre = numeroOrdre;
    }

    public Time getHeureDepartAeroport() {
        return heureDepartAeroport;
    }

    public void setHeureDepartAeroport(Time heureDepartAeroport) {
        this.heureDepartAeroport = heureDepartAeroport;
    }

    public Time getHeureRetourAeroport() {
        return heureRetourAeroport;
    }

    public void setHeureRetourAeroport(Time heureRetourAeroport) {
        this.heureRetourAeroport = heureRetourAeroport;
    }

    public Double getDistanceTotaleKm() {
        return distanceTotaleKm;
    }

    public void setDistanceTotaleKm(Double distanceTotaleKm) {
        this.distanceTotaleKm = distanceTotaleKm;
    }

    public Integer getTempsTotalMinutes() {
        return tempsTotalMinutes;
    }

    public void setTempsTotalMinutes(Integer tempsTotalMinutes) {
        this.tempsTotalMinutes = tempsTotalMinutes;
    }

    public Integer getNombrePassagersTransportes() {
        return nombrePassagersTransportes;
    }

    public void setNombrePassagersTransportes(Integer nombrePassagersTransportes) {
        this.nombrePassagersTransportes = nombrePassagersTransportes;
    }

    public Integer getNombreTrajetEffectues() {
        return nombreTrajetEffectues;
    }

    public void setNombreTrajetEffectues(Integer nombreTrajetEffectues) {
        this.nombreTrajetEffectues = nombreTrajetEffectues;
    }

    public List<ItineraireArretDTO> getItineraire() {
        return itineraire;
    }

    public void setItineraire(List<ItineraireArretDTO> itineraire) {
        this.itineraire = itineraire;
    }

    public void ajouterArret(ItineraireArretDTO arret) {
        this.itineraire.add(arret);
    }

    public List<ReservationPlanningDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationPlanningDTO> reservations) {
        this.reservations = reservations;
    }

    public void ajouterReservation(ReservationPlanningDTO reservation) {
        this.reservations.add(reservation);
    }

    public Integer calculerDureeHeures() {
        if (tempsTotalMinutes == null) return 0;
        return tempsTotalMinutes / 60;
    }

    @Override
    public String toString() {
        return "AssignationVehiculeDTO [vehicule=" + vehicule.getReference() + ", numeroOrdre=" + numeroOrdre
                + ", heureDeparAeroport=" + heureDepartAeroport + ", heureRetourAeroport=" + heureRetourAeroport
                + ", distanceTotaleKm=" + distanceTotaleKm + ", nombrePassagersTransportes="
                + nombrePassagersTransportes + "]";
    }
}
