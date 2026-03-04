package com.backoffice.dto;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.backoffice.models.Vehicule;

public class VehiculePlanningDTO {
    private Vehicule vehicule;
    private List<ReservationPlanningDTO> reservations;
    private Time heureRetourAeroport; // Heure de retour à l'aéroport
    private Time heureDepart; // Heure de départ du véhicule de l'aéroport
    private Double distanceTotale; // Distance totale du trajet en km

    public VehiculePlanningDTO() {
        this.reservations = new ArrayList<>();
    }

    public VehiculePlanningDTO(Vehicule vehicule) {
        this.vehicule = vehicule;
        this.reservations = new ArrayList<>();
    }

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public List<ReservationPlanningDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationPlanningDTO> reservations) {
        this.reservations = reservations;
    }

    public void addReservation(ReservationPlanningDTO reservation) {
        this.reservations.add(reservation);
    }

    public Time getHeureRetourAeroport() {
        return heureRetourAeroport;
    }

    public void setHeureRetourAeroport(Time heureRetourAeroport) {
        this.heureRetourAeroport = heureRetourAeroport;
    }

    public Time getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(Time heureDepart) {
        this.heureDepart = heureDepart;
    }

    public Double getDistanceTotale() {
        return distanceTotale;
    }

    public void setDistanceTotale(Double distanceTotale) {
        this.distanceTotale = distanceTotale;
    }
}
