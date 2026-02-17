package com.backoffice.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.backoffice.dto.ReservationPlanningDTO;
import com.backoffice.dto.VehiculePlanningDTO;
import com.backoffice.models.Distance;
import com.backoffice.models.Hotel;
import com.backoffice.models.Reservation;
import com.backoffice.models.Vehicule;
import com.backoffice.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class PlanificationService {

    private static final Integer TEMPS_ATTENTE_MIN = 30; // 30 minutes par défaut
    private static final String CODE_AEROPORT = "TNR"; // Code de l'aéroport Ivato

    /**
     * Obtenir toutes les réservations pour une date donnée
     */
    public List<Reservation> getReservationsByDate(Date date) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Reservation> query = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.date = :date ORDER BY r.heure", 
                Reservation.class
            );
            query.setParameter("date", date);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Obtenir tous les véhicules disponibles, triés par priorité (Diesel en premier)
     */
    public List<Vehicule> getVehiculesDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Vehicule> query = em.createQuery(
                "SELECT v FROM Vehicule v ORDER BY " +
                "CASE v.typeCarburant " +
                "  WHEN 'D' THEN 1 " +
                "  WHEN 'H' THEN 2 " +
                "  WHEN 'Es' THEN 3 " +
                "  WHEN 'El' THEN 4 " +
                "END, v.place", 
                Vehicule.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Obtenir la distance entre deux lieux
     */
    public Double getDistance(Integer lieuDepartId, Integer lieuArriveeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Distance> query = em.createQuery(
                "SELECT d FROM Distance d WHERE d.lieuDepart.id = :depart AND d.lieuArrivee.id = :arrivee", 
                Distance.class
            );
            query.setParameter("depart", lieuDepartId);
            query.setParameter("arrivee", lieuArriveeId);
            List<Distance> result = query.getResultList();
            return result.isEmpty() ? 0.0 : result.get(0).getKm();
        } finally {
            em.close();
        }
    }

    /**
     * Obtenir l'ID de l'aéroport Ivato
     */
    public Integer getAeroportId() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(
                "SELECT l.id FROM Lieu l WHERE l.code = :code", 
                Integer.class
            );
            query.setParameter("code", CODE_AEROPORT);
            List<Integer> result = query.getResultList();
            return result.isEmpty() ? 1 : result.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Calculer le temps de trajet en minutes
     */
    public Integer calculerTempsTrajet(Double distanceKm, Double vitesseMoyenne) {
        if (distanceKm == null || vitesseMoyenne == null || vitesseMoyenne == 0) {
            return 0;
        }
        double heures = distanceKm / vitesseMoyenne;
        return (int) Math.ceil(heures * 60);
    }

    /**
     * Ajouter des minutes à une heure
     */
    public Time ajouterMinutes(Time heure, Integer minutes) {
        if (heure == null || minutes == null) {
            return heure;
        }
        long millis = heure.getTime() + (minutes * 60 * 1000L);
        return new Time(millis);
    }

    /**
     * Soustraire des minutes d'une heure
     */
    public Time soustraireMinutes(Time heure, Integer minutes) {
        if (heure == null || minutes == null) {
            return heure;
        }
        long millis = heure.getTime() - (minutes * 60 * 1000L);
        return new Time(millis);
    }

    /**
     * Trouver le meilleur véhicule pour une réservation
     * Règle: choisir le véhicule avec le nombre de places le plus proche (supérieur ou égal)
     * Priorité: Diesel > Hybride > Essence > Electrique
     */
    public Vehicule trouverMeilleurVehicule(Integer nombrePersonnes, List<Vehicule> vehiculesDisponibles) {
        if (vehiculesDisponibles == null || vehiculesDisponibles.isEmpty()) {
            return null;
        }

        // Filtrer les véhicules ayant assez de places
        List<Vehicule> vehiculesCapables = new ArrayList<>();
        for (Vehicule v : vehiculesDisponibles) {
            if (v.getPlace() >= nombrePersonnes) {
                vehiculesCapables.add(v);
            }
        }

        if (vehiculesCapables.isEmpty()) {
            return null;
        }

        // Trier par: 1) type de carburant (Diesel prioritaire), 2) nombre de places (le plus proche)
        vehiculesCapables.sort(new Comparator<Vehicule>() {
            @Override
            public int compare(Vehicule v1, Vehicule v2) {
                // Priorité du carburant
                int priorite1 = getPrioriteCarburant(v1.getTypeCarburant());
                int priorite2 = getPrioriteCarburant(v2.getTypeCarburant());
                
                if (priorite1 != priorite2) {
                    return Integer.compare(priorite1, priorite2);
                }
                
                // Si même priorité, choisir le plus petit nombre de places
                return Integer.compare(v1.getPlace(), v2.getPlace());
            }
        });

        return vehiculesCapables.get(0);
    }

    private int getPrioriteCarburant(Vehicule.TypeCarburant type) {
        switch (type) {
            case D:   return 1; // Diesel - priorité la plus haute
            case H:   return 2; // Hybride
            case Es:  return 3; // Essence
            case El:  return 4; // Electrique
            default:  return 5;
        }
    }

    /**
     * Générer le planning pour une date donnée
     */
    public List<VehiculePlanningDTO> genererPlanning(Date date) {
        List<Reservation> reservations = getReservationsByDate(date);
        List<Vehicule> vehiculesDisponibles = getVehiculesDisponibles();
        List<VehiculePlanningDTO> planning = new ArrayList<>();
        
        Integer aeroportId = getAeroportId();
        
        // Grouper les réservations par véhicule optimal
        for (Reservation reservation : reservations) {
            Vehicule vehicule = trouverMeilleurVehicule(reservation.getNombre(), vehiculesDisponibles);
            
            if (vehicule == null) {
                continue; // Pas de véhicule disponible pour cette réservation
            }
            
            // Trouver ou créer le VehiculePlanningDTO
            VehiculePlanningDTO vehiculePlanning = null;
            for (VehiculePlanningDTO vp : planning) {
                if (vp.getVehicule().getId().equals(vehicule.getId())) {
                    vehiculePlanning = vp;
                    break;
                }
            }
            
            if (vehiculePlanning == null) {
                vehiculePlanning = new VehiculePlanningDTO(vehicule);
                planning.add(vehiculePlanning);
            }
            
            // Calculer les détails de la réservation
            ReservationPlanningDTO resPlanning = creerReservationPlanning(
                reservation, aeroportId, vehicule
            );
            
            vehiculePlanning.addReservation(resPlanning);
        }
        
        // Calculer l'heure de retour à l'aéroport pour chaque véhicule
        for (VehiculePlanningDTO vp : planning) {
            calculerHeureRetourAeroport(vp, aeroportId);
        }
        
        return planning;
    }

    /**
     * Créer un ReservationPlanningDTO avec tous les calculs
     */
    private ReservationPlanningDTO creerReservationPlanning(Reservation reservation, 
                                                            Integer aeroportId, 
                                                            Vehicule vehicule) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Hotel hotel = em.find(Hotel.class, reservation.getHotel());
            String hotelLibelle = hotel != null ? hotel.getLibelle() : "Inconnu";
            
            // Distance aéroport -> hôtel
            Double distanceKm = getDistance(aeroportId, reservation.getHotel());
            
            // Temps de trajet en minutes
            Integer tempsTrajetMin = calculerTempsTrajet(distanceKm, vehicule.getVitesseMoyenne());
            
            // Heure de départ = heure réservation - temps trajet - temps attente
            Time heureDepart = soustraireMinutes(reservation.getHeure(), 
                                                 tempsTrajetMin + TEMPS_ATTENTE_MIN);
            
            // Heure de retour = heure réservation + temps attente + temps trajet retour
            Time heureRetour = ajouterMinutes(reservation.getHeure(), 
                                              TEMPS_ATTENTE_MIN + tempsTrajetMin);
            
            return new ReservationPlanningDTO(
                reservation, 
                hotelLibelle, 
                heureDepart, 
                heureRetour, 
                distanceKm, 
                TEMPS_ATTENTE_MIN
            );
        } finally {
            em.close();
        }
    }

    /**
     * Calculer l'heure de retour du véhicule à l'aéroport
     */
    private void calculerHeureRetourAeroport(VehiculePlanningDTO vehiculePlanning, Integer aeroportId) {
        if (vehiculePlanning.getReservations().isEmpty()) {
            return;
        }
        
        // La dernière réservation détermine l'heure de retour
        List<ReservationPlanningDTO> reservations = vehiculePlanning.getReservations();
        ReservationPlanningDTO derniereReservation = reservations.get(reservations.size() - 1);
        
        Time heureRetour = derniereReservation.getHeureRetour();
        vehiculePlanning.setHeureRetourAeroport(heureRetour);
    }
}
