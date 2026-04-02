package com.backoffice.models;

import java.sql.Time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicule")
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "reference", nullable = false, unique = true, length = 50)
    private String reference;

    @Column(name = "place", nullable = false)
    private Integer place;

    @ManyToOne
    @JoinColumn(name = "type_carburant", nullable = false)
    private TypeCarburant typeCarburant;

    @Column(name = "vitesse_moyenne", nullable = false, columnDefinition = "DECIMAL(5,2)")
    private Double vitesseMoyenne; // en km/h

    @Column(name = "heure_disponibilite", nullable = false)
    private Time heureDisponibilite;

    public Vehicule() {
        this.heureDisponibilite = Time.valueOf("00:00:00");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

    public TypeCarburant getTypeCarburant() {
        return typeCarburant;
    }

    public void setTypeCarburant(TypeCarburant typeCarburant) {
        this.typeCarburant = typeCarburant;
    }

    public Double getVitesseMoyenne() {
        return vitesseMoyenne;
    }

    public void setVitesseMoyenne(Double vitesseMoyenne) {
        this.vitesseMoyenne = vitesseMoyenne;
    }

    public Time getHeureDisponibilite() {
        return heureDisponibilite;
    }

    public void setHeureDisponibilite(Time heureDisponibilite) {
        this.heureDisponibilite = heureDisponibilite;
    }
}
