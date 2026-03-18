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
@Table(name = "itineraire_arret")
public class ItineraireArret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "assignation_vehicule_id", nullable = false)
    private AssignationVehicule assignationVehicule;

    @Column(name = "ordre_arret", nullable = false)
    private Integer ordreArret;

    @ManyToOne
    @JoinColumn(name = "lieu_id", nullable = false)
    private Lieu lieu;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;  // NULL si c'est l'aéroport

    @Column(name = "heure_arrivee", nullable = false)
    private Time heureArrivee;

    @Column(name = "heure_depart", nullable = false)
    private Time heureDepart;

    @Column(name = "nombre_passagers_embarques")
    private Integer nombrePassagersEmbarques;

    @Column(name = "distance_depuis_prev_km", columnDefinition = "DECIMAL(8,2)")
    private Double distanceDepuisPrevKm;

    @Column(name = "date_creation", nullable = false)
    private Timestamp dateCreation;

    // Constructeurs
    public ItineraireArret() {
        this.dateCreation = new Timestamp(System.currentTimeMillis());
    }

    public ItineraireArret(AssignationVehicule assignationVehicule, Integer ordreArret, Lieu lieu) {
        this();
        this.assignationVehicule = assignationVehicule;
        this.ordreArret = ordreArret;
        this.lieu = lieu;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AssignationVehicule getAssignationVehicule() {
        return assignationVehicule;
    }

    public void setAssignationVehicule(AssignationVehicule assignationVehicule) {
        this.assignationVehicule = assignationVehicule;
    }

    public Integer getOrdreArret() {
        return ordreArret;
    }

    public void setOrdreArret(Integer ordreArret) {
        this.ordreArret = ordreArret;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Time getHeureArrivee() {
        return heureArrivee;
    }

    public void setHeureArrivee(Time heureArrivee) {
        this.heureArrivee = heureArrivee;
    }

    public Time getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(Time heureDepart) {
        this.heureDepart = heureDepart;
    }

    public Integer getNombrePassagersEmbarques() {
        return nombrePassagersEmbarques;
    }

    public void setNombrePassagersEmbarques(Integer nombrePassagersEmbarques) {
        this.nombrePassagersEmbarques = nombrePassagersEmbarques;
    }

    public Double getDistanceDepuisPrevKm() {
        return distanceDepuisPrevKm;
    }

    public void setDistanceDepuisPrevKm(Double distanceDepuisPrevKm) {
        this.distanceDepuisPrevKm = distanceDepuisPrevKm;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "ItineraireArret [id=" + id + ", ordreArret=" + ordreArret + ", lieu=" + lieu.getCode()
                + ", heureArrivee=" + heureArrivee + ", heureDepart=" + heureDepart + ", nombrePassagersEmbarques="
                + nombrePassagersEmbarques + "]";
    }
}
