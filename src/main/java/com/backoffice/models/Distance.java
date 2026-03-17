package com.backoffice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "distance")
public class Distance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lieu_depart", nullable = false)
    private Lieu lieuDepart;

    @ManyToOne
    @JoinColumn(name = "lieu_arrivee", nullable = false)
    private Lieu lieuArrivee;

    @Column(name = "km", nullable = false, columnDefinition = "DECIMAL(6,2)")
    private Double km;

    public Distance() {
    }

    public Distance(Integer id, Lieu lieuDepart, Lieu lieuArrivee, Double km) {
        this.id = id;
        this.lieuDepart = lieuDepart;
        this.lieuArrivee = lieuArrivee;
        this.km = km;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Lieu getLieuDepart() {
        return lieuDepart;
    }

    public void setLieuDepart(Lieu lieuDepart) {
        this.lieuDepart = lieuDepart;
    }

    public Lieu getLieuArrivee() {
        return lieuArrivee;
    }

    public void setLieuArrivee(Lieu lieuArrivee) {
        this.lieuArrivee = lieuArrivee;
    }

    public Double getKm() {
        return km;
    }

    public void setKm(Double km) {
        this.km = km;
    }
}
