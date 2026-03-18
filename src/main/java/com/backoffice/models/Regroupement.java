package com.backoffice.models;

import java.sql.Time;
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
@Table(name = "regroupement")
public class Regroupement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "planification_id", nullable = false)
    private Planification planification;

    @Column(name = "numero_regroupement", nullable = false)
    private Integer numeroRegroupement;

    @Column(name = "heure_depart_groupe", nullable = false)
    private Time heureDepartGroupe;

    @Column(name = "nombre_reservations", nullable = false)
    private Integer nombreReservations;

    @Column(name = "nombre_passagers_total", nullable = false)
    private Integer nombrePassagersTotal;

    @Column(name = "nombre_vehicules_assignes", nullable = false)
    private Integer nombreVehiculesAssignes;

    @Column(name = "date_creation", nullable = false)
    private Timestamp dateCreation;

    // Constructeurs
    public Regroupement() {
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public Regroupement(Planification planification, Integer numeroRegroupement, Time heureDepartGroupe) {
        this();
        this.planification = planification;
        this.numeroRegroupement = numeroRegroupement;
        this.heureDepartGroupe = heureDepartGroupe;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Planification getPlanification() {
        return planification;
    }

    public void setPlanification(Planification planification) {
        this.planification = planification;
    }

    public Integer getNumeroRegroupement() {
        return numeroRegroupement;
    }

    public void setNumeroRegroupement(Integer numeroRegroupement) {
        this.numeroRegroupement = numeroRegroupement;
    }

    public Time getHeureDepartGroupe() {
        return heureDepartGroupe;
    }

    public void setHeureDepartGroupe(Time heureDepartGroupe) {
        this.heureDepartGroupe = heureDepartGroupe;
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

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Regroupement [id=" + id + ", numeroRegroupement=" + numeroRegroupement + ", heureDepartGroupe="
                + heureDepartGroupe + ", nombreReservations=" + nombreReservations + ", nombreVehiculesAssignes="
                + nombreVehiculesAssignes + "]";
    }
}
