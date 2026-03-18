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
@Table(name = "assignation_vehicule")
public class AssignationVehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "regroupement_id", nullable = false)
    private Regroupement regroupement;

    @ManyToOne
    @JoinColumn(name = "vehicule_id", nullable = false)
    private Vehicule vehicule;

    @Column(name = "numero_ordre_groupe", nullable = false)
    private Integer numeroOrdreGroupe;

    @Column(name = "nombre_trajet_effectues", nullable = false)
    private Integer nombreTrajetEffectues;

    @Column(name = "heure_depart_aeroport", nullable = false)
    private Time heureDepartAeroport;

    @Column(name = "heure_retour_aeroport", nullable = false)
    private Time heureRetourAeroport;

    @Column(name = "distance_totale_km", nullable = false, columnDefinition = "DECIMAL(8,2)")
    private Double distanceTotaleKm;

    @Column(name = "temps_total_minutes", nullable = false)
    private Integer tempsTotalMinutes;

    @Column(name = "nombre_passagers_transportes", nullable = false)
    private Integer nombrePassagersTransportes;

    @Column(name = "date_creation", nullable = false)
    private Timestamp dateCreation;

    @Column(name = "date_modification", nullable = false)
    private Timestamp dateModification;

    // Constructeurs
    public AssignationVehicule() {
        this.nombreTrajetEffectues = 1;
        this.dateCreation = new Timestamp(System.currentTimeMillis());
        this.dateModification = new Timestamp(System.currentTimeMillis());
    }

    public AssignationVehicule(Regroupement regroupement, Vehicule vehicule, Integer numeroOrdreGroupe) {
        this();
        this.regroupement = regroupement;
        this.vehicule = vehicule;
        this.numeroOrdreGroupe = numeroOrdreGroupe;
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

    public Vehicule getVehicule() {
        return vehicule;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
    }

    public Integer getNumeroOrdreGroupe() {
        return numeroOrdreGroupe;
    }

    public void setNumeroOrdreGroupe(Integer numeroOrdreGroupe) {
        this.numeroOrdreGroupe = numeroOrdreGroupe;
    }

    public Integer getNombreTrajetEffectues() {
        return nombreTrajetEffectues;
    }

    public void setNombreTrajetEffectues(Integer nombreTrajetEffectues) {
        this.nombreTrajetEffectues = nombreTrajetEffectues;
    }

    public Time getHeureDepartAeroport() {
        return heureDepartAeroport;
    }

    public void setHeureDepartAeroport(Time heureDepartAeroport) {
        this.heureDepartAeroport = heureDepartAeroport;
    }

    public Time getHeureRetourAeroport() {
        return heureRetourAeroport;
    }

    public void setHeureRetourAeroport(Time heureRetourAeroport) {
        this.heureRetourAeroport = heureRetourAeroport;
    }

    public Double getDistanceTotaleKm() {
        return distanceTotaleKm;
    }

    public void setDistanceTotaleKm(Double distanceTotaleKm) {
        this.distanceTotaleKm = distanceTotaleKm;
    }

    public Integer getTempsTotalMinutes() {
        return tempsTotalMinutes;
    }

    public void setTempsTotalMinutes(Integer tempsTotalMinutes) {
        this.tempsTotalMinutes = tempsTotalMinutes;
    }

    public Integer getNombrePassagersTransportes() {
        return nombrePassagersTransportes;
    }

    public void setNombrePassagersTransportes(Integer nombrePassagersTransportes) {
        this.nombrePassagersTransportes = nombrePassagersTransportes;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Timestamp getDateModification() {
        return dateModification;
    }

    public void setDateModification(Timestamp dateModification) {
        this.dateModification = dateModification;
    }

    @Override
    public String toString() {
        return "AssignationVehicule [id=" + id + ", vehicule=" + vehicule.getReference() + ", numeroOrdreGroupe="
                + numeroOrdreGroupe + ", nombreTrajetEffectues=" + nombreTrajetEffectues + ", distanceTotaleKm="
                + distanceTotaleKm + ", tempsTotalMinutes=" + tempsTotalMinutes + "]";
    }
}
