package com.backoffice.models;

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
@Table(name = "regroupement_reservation")
public class RegroupementReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "regroupement_id", nullable = false)
    private Regroupement regroupement;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "date_creation", nullable = false)
    private Timestamp dateCreation;

    // Constructeurs
    public RegroupementReservation() {
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public RegroupementReservation(Regroupement regroupement, Reservation reservation) {
        this();
        this.regroupement = regroupement;
        this.reservation = reservation;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Regroupement getRegroupement() {
        return regroupement;
    }

    public void setRegroupement(Regroupement regroupement) {
        this.regroupement = regroupement;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "RegroupementReservation [id=" + id + ", regroupement=" + regroupement.getId() + ", reservation="
                + reservation.getId() + "]";
    }
}
