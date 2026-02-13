package com.backoffice.dto;

import com.backoffice.models.Reservation;

public class ReservationDTO {
    private Integer id;
    private Integer reference;
    private Integer nombre;
    private String date;  // Format: yyyy-MM-dd
    private String heure; // Format: HH:mm:ss
    private Integer hotel;

    public ReservationDTO() {
    }

    public ReservationDTO(Reservation reservation) {
        this.id = reservation.getId();
        this.reference = reservation.getReference();
        this.nombre = reservation.getNombre();
        this.date = reservation.getDate() != null ? reservation.getDate().toString() : null;
        this.heure = reservation.getHeure() != null ? reservation.getHeure().toString() : null;
        this.hotel = reservation.getHotel();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReference() {
        return reference;
    }

    public void setReference(Integer reference) {
        this.reference = reference;
    }

    public Integer getNombre() {
        return nombre;
    }

    public void setNombre(Integer nombre) {
        this.nombre = nombre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public Integer getHotel() {
        return hotel;
    }

    public void setHotel(Integer hotel) {
        this.hotel = hotel;
    }
}
