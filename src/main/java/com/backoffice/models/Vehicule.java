package com.backoffice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicule")
public class Vehicule {

    public enum TypeCarburant {
        D("Diesel"),
        Es("Essence"),
        H("Hybride"),
        El("Electrique");

        private final String libelle;

        TypeCarburant(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "reference", nullable = false, unique = true, length = 50)
    private String reference;

    @Column(name = "place", nullable = false)
    private Integer place;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_carburant", nullable = false, length = 10)
    private TypeCarburant typeCarburant;

    @Column(name = "vitesse_moyenne", nullable = false)
    private Double vitesseMoyenne; // en km/h

    public Vehicule() {
    }

    public Vehicule(Integer id, String reference, Integer place, TypeCarburant typeCarburant) {
        this.id = id;
        this.reference = reference;
        this.place = place;
        this.typeCarburant = typeCarburant;
        this.vitesseMoyenne = 60.0; // Valeur par défaut
    }

    public Vehicule(Integer id, String reference, Integer place, TypeCarburant typeCarburant, Double vitesseMoyenne) {
        this.id = id;
        this.reference = reference;
        this.place = place;
        this.typeCarburant = typeCarburant;
        this.vitesseMoyenne = vitesseMoyenne;
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
}
