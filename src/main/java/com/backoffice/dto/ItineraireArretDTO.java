package com.backoffice.dto;

import java.sql.Time;

/**
 * DTO pour représenter un arrêt dans l'itinéraire d'un véhicule.
 * Exemple: Aéroport → Hôtel Colbert → Hôtel Novotel → Aéroport
 */
public class ItineraireArretDTO {
    
    private Integer ordre;          // Numéro de l'arrêt (1, 2, 3, etc.)
    private String lieuCode;        // Code du lieu (TNR, COL, NOV, etc.)
    private String lieuLibelle;     // Libellé du lieu
    private String hotelLibelle;    // Libellé de l'hôtel (NULL si c'est l'aéroport)
    private Time heureArrivee;      // Heure prévue d'arrivée
    private Time heureDepart;       // Heure prévue de départ
    private Integer nombrePassagers;// Nombre de passagers cumulés
    private Double distanceKm;      // Distance depuis l'arrêt précédent
    
    public ItineraireArretDTO() {
    }
    
    public ItineraireArretDTO(Integer ordre, String lieuCode, String lieuLibelle, Time heureArrivee, Time heureDepart) {
        this.ordre = ordre;
        this.lieuCode = lieuCode;
        this.lieuLibelle = lieuLibelle;
        this.heureArrivee = heureArrivee;
        this.heureDepart = heureDepart;
    }

    // Getters et Setters
    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public String getLieuCode() {
        return lieuCode;
    }

    public void setLieuCode(String lieuCode) {
        this.lieuCode = lieuCode;
    }

    public String getLieuLibelle() {
        return lieuLibelle;
    }

    public void setLieuLibelle(String lieuLibelle) {
        this.lieuLibelle = lieuLibelle;
    }

    public String getHotelLibelle() {
        return hotelLibelle;
    }

    public void setHotelLibelle(String hotelLibelle) {
        this.hotelLibelle = hotelLibelle;
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

    public Integer getNombrePassagers() {
        return nombrePassagers;
    }

    public void setNombrePassagers(Integer nombrePassagers) {
        this.nombrePassagers = nombrePassagers;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    @Override
    public String toString() {
        return "ItineraireArretDTO [ordre=" + ordre + ", lieuCode=" + lieuCode + ", heureArrivee=" + heureArrivee
                + ", heureDepart=" + heureDepart + ", nombrePassagers=" + nombrePassagers + "]";
    }
}
