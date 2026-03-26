package com.backoffice.models;

import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "reference", nullable = false)
    private Integer reference;

    @Column(name = "nombre", nullable = false)
    private Integer nombre;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "heure", nullable = false)
    private Time heure;

    @Column(name = "hotel", nullable = false)
    private Integer hotel;

    @Column(name = "client")
    private String client;

    public Reservation() {
    }

    public Reservation(Integer id, Integer reference, Integer nombre, Date date, Time heure, Integer hotel) {
        this.id = id;
        this.reference = reference;
        this.nombre = nombre;
        this.date = date;
        this.heure = heure;
        this.hotel = hotel;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getHeure() {
        return heure;
    }

    public void setHeure(Time heure) {
        this.heure = heure;
    }

    public Integer getHotel() {
        return hotel;
    }

    public void setHotel(Integer hotel) {
        this.hotel = hotel;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
