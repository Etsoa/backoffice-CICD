package com.backoffice.dto;

import java.sql.Time;
import com.backoffice.models.Reservation;

public class ReservationPlanningDTO {
    private Reservation reservation;
    private String hotelLibelle;
    private Time heureDepart;
    private Time heureRetour;
    private Double distanceKm;
    private Integer tempsAttenteMin; // TA en minutes

    public ReservationPlanningDTO() {
    }

    public ReservationPlanningDTO(Reservation reservation, String hotelLibelle, Time heureDepart, 
                                   Time heureRetour, Double distanceKm, Integer tempsAttenteMin) {
        this.reservation = reservation;
        this.hotelLibelle = hotelLibelle;
        this.heureDepart = heureDepart;
        this.heureRetour = heureRetour;
        this.distanceKm = distanceKm;
        this.tempsAttenteMin = tempsAttenteMin;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public String getHotelLibelle() {
        return hotelLibelle;
    }

    public void setHotelLibelle(String hotelLibelle) {
        this.hotelLibelle = hotelLibelle;
    }

    public Time getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(Time heureDepart) {
        this.heureDepart = heureDepart;
    }

    public Time getHeureRetour() {
        return heureRetour;
    }

    public void setHeureRetour(Time heureRetour) {
        this.heureRetour = heureRetour;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getTempsAttenteMin() {
        return tempsAttenteMin;
    }

    public void setTempsAttenteMin(Integer tempsAttenteMin) {
        this.tempsAttenteMin = tempsAttenteMin;
    }
}
