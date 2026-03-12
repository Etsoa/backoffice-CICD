package com.backoffice.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.backoffice.dto.RegroupementDTO;
import com.backoffice.dto.ReservationPlanningDTO;
import com.backoffice.dto.VehiculePlanningDTO;
import com.backoffice.models.ConfigurationAttente;
import com.backoffice.models.Hotel;
import com.backoffice.models.Parametre;
import com.backoffice.models.Reservation;
import com.backoffice.models.Vehicule;
import com.backoffice.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class PlanificationService {

    private static final String CODE_AEROPORT = "TNR";
    private static final int DEFAULT_DELAI_ATTENTE = 30; // Délai par défaut en minutes

    // =========================================================================
    // Méthodes d'accès aux données
    // =========================================================================

    /**
     * Récupérer le délai d'attente depuis la table configuration_attente (Sprint 5)
     * ou depuis la table parametre en fallback.
     * 
     * Priorité: 
     * 1. Table configuration_attente (entrée active)
     * 2. Table parametre (clé 'delai_attente')
     * 3. Valeur par défaut (30 minutes)
     */
    public int getDelaiAttente() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // D'abord essayer la nouvelle table configuration_attente (Sprint 5)
            try {
                TypedQuery<ConfigurationAttente> configQuery = em.createQuery(
                    "SELECT c FROM ConfigurationAttente c WHERE c.actif = true ORDER BY c.id DESC",
                    ConfigurationAttente.class);
                configQuery.setMaxResults(1);
                List<ConfigurationAttente> configResult = configQuery.getResultList();
                if (!configResult.isEmpty()) {
                    return configResult.get(0).getTempsAttenteMinutes();
                }
            } catch (Exception e) {
                // Table configuration_attente n'existe peut-être pas encore, continuer avec parametre
            }

            // Fallback sur la table parametre
            TypedQuery<Parametre> query = em.createQuery(
                "SELECT p FROM Parametre p WHERE p.cle = :cle", Parametre.class);
            query.setParameter("cle", "delai_attente");
            List<Parametre> result = query.getResultList();
            return result.isEmpty() ? DEFAULT_DELAI_ATTENTE : result.get(0).getValeurInt();
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer les réservations d'une date, triées par heure croissante
     */
    public List<Reservation> getReservationsByDate(Date date) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Reservation> query = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.date = :date ORDER BY r.heure ASC",
                Reservation.class);
            query.setParameter("date", date);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer tous les véhicules
     */
    public List<Vehicule> getAllVehicules() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT v FROM Vehicule v ORDER BY v.place ASC", Vehicule.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer l'ID du lieu aéroport (TNR)
     */
    public Integer getAeroportId() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Integer> query = em.createQuery(
                "SELECT l.id FROM Lieu l WHERE l.code = :code", Integer.class);
            query.setParameter("code", CODE_AEROPORT);
            List<Integer> result = query.getResultList();
            return result.isEmpty() ? 1 : result.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer l'ID du lieu correspondant à un hôtel (par libellé)
     */
    public Integer getLieuIdByHotelId(Integer hotelId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Hotel hotel = em.find(Hotel.class, hotelId);
            if (hotel == null) return null;

            TypedQuery<Integer> query = em.createQuery(
                "SELECT l.id FROM Lieu l WHERE LOWER(l.libelle) = LOWER(:libelle)", Integer.class);
            query.setParameter("libelle", hotel.getLibelle());
            List<Integer> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer la distance entre deux lieux
     */
    public Double getDistance(Integer lieuDepartId, Integer lieuArriveeId) {
        if (lieuDepartId == null || lieuArriveeId == null) return 0.0;
        if (lieuDepartId.equals(lieuArriveeId)) return 0.0;
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Chercher dans les deux sens (la distance est symétrique)
            TypedQuery<Double> query = em.createQuery(
                "SELECT d.km FROM Distance d WHERE " +
                "(d.lieuDepart.id = :a AND d.lieuArrivee.id = :b) OR " +
                "(d.lieuDepart.id = :b AND d.lieuArrivee.id = :a)",
                Double.class);
            query.setParameter("a", lieuDepartId);
            query.setParameter("b", lieuArriveeId);
            List<Double> result = query.getResultList();
            return result.isEmpty() ? 0.0 : result.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Récupérer le libellé d'un hôtel
     */
    public String getHotelLibelle(Integer hotelId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Hotel hotel = em.find(Hotel.class, hotelId);
            return hotel != null ? hotel.getLibelle() : "Inconnu";
        } finally {
            em.close();
        }
    }

    // =========================================================================
    // Utilitaires de calcul
    // =========================================================================

    public int calculerTempsTrajetMinutes(double distanceKm, double vitesseKmh) {
        if (vitesseKmh <= 0) return 0;
        return (int) Math.ceil((distanceKm / vitesseKmh) * 60);
    }

    public Time ajouterMinutes(Time heure, int minutes) {
        if (heure == null) return null;
        return new Time(heure.getTime() + minutes * 60 * 1000L);
    }

    // =========================================================================
    // Logique métier principale
    // =========================================================================

    /**
     * Trouver le meilleur véhicule pour un nombre de personnes donné.
     * Règle : places >= nombre, le plus proche. Si égalité, Diesel prioritaire. Si encore égalité, random.
     */
    public Vehicule trouverMeilleurVehicule(int nombrePersonnes, List<Vehicule> vehiculesDisponibles) {
        List<Vehicule> candidats = new ArrayList<>();
        for (Vehicule v : vehiculesDisponibles) {
            if (v.getPlace() >= nombrePersonnes) {
                candidats.add(v);
            }
        }
        if (candidats.isEmpty()) return null;

        // Trier par places (plus proche), puis carburant (Diesel prioritaire)
        candidats.sort((v1, v2) -> {
            int cmp = Integer.compare(v1.getPlace(), v2.getPlace());
            if (cmp != 0) return cmp;
            return Integer.compare(getPrioriteCarburant(v1), getPrioriteCarburant(v2));
        });

        // Si plusieurs avec même places et même priorité carburant -> random
        int bestPlaces = candidats.get(0).getPlace();
        int bestPrio = getPrioriteCarburant(candidats.get(0));
        List<Vehicule> meilleurs = new ArrayList<>();
        for (Vehicule v : candidats) {
            if (v.getPlace() == bestPlaces && getPrioriteCarburant(v) == bestPrio) {
                meilleurs.add(v);
            }
        }
        if (meilleurs.size() > 1) {
            return meilleurs.get(new Random().nextInt(meilleurs.size()));
        }
        return meilleurs.get(0);
    }

    /**
     * Priorité des carburants selon Sprint 5:
     * 1. Diesel (D) - priorité 1
     * 2. Essence (Es) - priorité 2
     * 3. Autres (H, El, etc.) - priorité 3 (random parmi eux)
     */
    private int getPrioriteCarburant(Vehicule v) {
        String code = v.getTypeCarburant() != null ? v.getTypeCarburant().getCode() : "";
        switch (code) {
            case "D":  return 1;  // Diesel - priorité maximale
            case "Es": return 2;  // Essence - deuxième priorité
            default:   return 3;  // Autres (Hybride, Electrique, etc.) - même priorité, random
        }
    }

    /**
     * Générer le planning complet pour une date donnée.
     * 
     * Sprint 5 - Logique de regroupement avec RegroupementDTO :
     * 1. Créer les regroupements (groupes de réservations par intervalle)
     * 2. Pour chaque groupe, assigner les véhicules
     * 3. TOUS les véhicules d'un même groupe partent à la même heure = heure de la DERNIÈRE réservation
     */
    public List<VehiculePlanningDTO> genererPlanning(Date date) {
        // Créer les regroupements et assigner les véhicules
        List<RegroupementDTO> regroupements = genererRegroupements(date);
        
        // Collecter tous les véhicules assignés
        List<VehiculePlanningDTO> planning = new ArrayList<>();
        for (RegroupementDTO groupe : regroupements) {
            planning.addAll(groupe.getVehiculesAssignes());
        }
        
        return planning;
    }
    
    /**
     * Générer les regroupements pour une date donnée.
     * Cette méthode retourne les groupes avec leurs véhicules assignés ET les réservations non assignées.
     */
    public List<RegroupementDTO> genererRegroupements(Date date) {
        List<Reservation> reservations = getReservationsByDate(date);
        if (reservations.isEmpty()) return new ArrayList<>();

        List<Vehicule> tousVehicules = getAllVehicules();
        int delaiAttente = getDelaiAttente();
        Integer aeroportId = getAeroportId();

        // Trier par heure ASC
        reservations.sort((r1, r2) -> r1.getHeure().compareTo(r2.getHeure()));

        List<RegroupementDTO> regroupements = new ArrayList<>();
        boolean[] assignees = new boolean[reservations.size()];
        // Map pour tracker l'heure de retour de chaque véhicule (vehiculeId -> heureRetour)
        Map<Integer, Time> vehiculesHeureRetour = new HashMap<>();
        int numeroGroupe = 1;

        for (int i = 0; i < reservations.size(); i++) {
            if (assignees[i]) continue;

            // Cette réservation définit le début d'un nouveau groupe/intervalle
            Reservation premiereDuGroupe = reservations.get(i);
            Time debutIntervalle = premiereDuGroupe.getHeure();
            Time finIntervalle = ajouterMinutes(debutIntervalle, delaiAttente);

            // Créer le regroupement
            RegroupementDTO groupe = new RegroupementDTO(numeroGroupe++, debutIntervalle, finIntervalle, delaiAttente);
            
            // Collecter TOUTES les réservations dans cet intervalle
            List<Integer> indicesGroupe = new ArrayList<>();
            indicesGroupe.add(i);
            groupe.ajouterReservation(premiereDuGroupe);
            
            for (int j = i + 1; j < reservations.size(); j++) {
                if (!assignees[j] && reservations.get(j).getHeure().compareTo(finIntervalle) <= 0) {
                    indicesGroupe.add(j);
                    groupe.ajouterReservation(reservations.get(j));
                }
            }
            
            // L'heure de départ COMMUNE pour tous les véhicules de ce groupe
            // = heure de la DERNIÈRE réservation du groupe
            Time heureDepartGroupe = groupe.getHeureDepart();

            // Calculer le total de personnes du groupe
            int totalPersonnesGroupe = groupe.getNombrePersonnesTotal();

            // Trier les réservations du groupe par heure (pour l'itinéraire optimal)
            indicesGroupe.sort((a, b) -> reservations.get(a).getHeure().compareTo(reservations.get(b).getHeure()));

            // Liste des véhicules disponibles pour ce groupe
            // Un véhicule est disponible s'il n'a jamais été utilisé OU s'il est revenu avant l'heure de départ
            List<Vehicule> disponibles = new ArrayList<>();
            for (Vehicule v : tousVehicules) {
                Time heureRetour = vehiculesHeureRetour.get(v.getId());
                if (heureRetour == null || heureRetour.compareTo(heureDepartGroupe) <= 0) {
                    // Véhicule disponible : jamais utilisé OU revenu avant le départ de ce groupe
                    disponibles.add(v);
                }
            }

            // Assigner ces réservations à autant de véhicules que nécessaire
            List<VehiculePlanningDTO> vehiculesGroupe = new ArrayList<>();

            // STRATÉGIE 1 : Essayer de trouver UN véhicule pour tout le groupe
            Vehicule vehiculePourTout = trouverMeilleurVehicule(totalPersonnesGroupe, disponibles);
            
            if (vehiculePourTout != null) {
                // Un seul véhicule peut accueillir tout le groupe !
                VehiculePlanningDTO vp = new VehiculePlanningDTO(vehiculePourTout);
                vp.setHeureDebutIntervalle(debutIntervalle);
                vp.setHeureFinIntervalle(finIntervalle);
                
                // Ajouter TOUTES les réservations à ce véhicule
                for (int idx : indicesGroupe) {
                    Reservation resa = reservations.get(idx);
                    assignees[idx] = true;
                    ajouterReservationAuPlanning(vp, resa, aeroportId, delaiAttente);
                }
                
                vehiculesGroupe.add(vp);
                disponibles.remove(vehiculePourTout);
            } else {
                // STRATÉGIE 2 : Bin-packing - remplir les véhicules au maximum
                // Trier réservations par nombre de personnes DESC (gros groupes d'abord)
                indicesGroupe.sort((a, b) -> Integer.compare(
                    reservations.get(b).getNombre(), 
                    reservations.get(a).getNombre()
                ));

                for (int idx : indicesGroupe) {
                    if (assignees[idx]) continue;
                    
                    Reservation resa = reservations.get(idx);
                    
                    // Essayer de trouver un véhicule du groupe qui a encore de la place
                    VehiculePlanningDTO vehiculeAvecPlace = null;
                    int maxPlacesRestantes = 0;
                    
                    for (VehiculePlanningDTO vp : vehiculesGroupe) {
                        int placesOccupees = vp.calculerNombrePassagers();
                        int placesRestantes = vp.getVehicule().getPlace() - placesOccupees;
                        if (placesRestantes >= resa.getNombre() && placesRestantes > maxPlacesRestantes) {
                            vehiculeAvecPlace = vp;
                            maxPlacesRestantes = placesRestantes;
                        }
                    }

                    if (vehiculeAvecPlace != null) {
                        // Ajouter à un véhicule existant du groupe
                        assignees[idx] = true;
                        ajouterReservationAuPlanning(vehiculeAvecPlace, resa, aeroportId, delaiAttente);
                    } else {
                        // Besoin d'un nouveau véhicule
                        Vehicule meilleur = trouverMeilleurVehicule(resa.getNombre(), disponibles);
                        
                        if (meilleur == null) {
                            // Aucun véhicule disponible avec assez de places
                            groupe.ajouterReservationNonAssignee(resa);
                            assignees[idx] = true;
                            continue;
                        }

                        VehiculePlanningDTO nouveauVehicule = new VehiculePlanningDTO(meilleur);
                        nouveauVehicule.setHeureDebutIntervalle(debutIntervalle);
                        nouveauVehicule.setHeureFinIntervalle(finIntervalle);
                        
                        vehiculesGroupe.add(nouveauVehicule);
                        disponibles.remove(meilleur);
                        assignees[idx] = true;
                        ajouterReservationAuPlanning(nouveauVehicule, resa, aeroportId, delaiAttente);
                    }
                }
            }

            // Calculer les itinéraires avec l'heure de départ COMMUNE du groupe
            // et mettre à jour l'heure de retour de chaque véhicule
            for (VehiculePlanningDTO vp : vehiculesGroupe) {
                vp.setNombrePassagers(vp.calculerNombrePassagers());
                calculerItineraire(vp, aeroportId, heureDepartGroupe);
                groupe.ajouterVehicule(vp);
                
                // Enregistrer l'heure de retour pour ce véhicule
                // Permet de le réutiliser dans les groupes suivants si le départ est après ce retour
                if (vp.getHeureRetourAeroport() != null) {
                    vehiculesHeureRetour.put(vp.getVehicule().getId(), vp.getHeureRetourAeroport());
                }
            }
            
            regroupements.add(groupe);
        }

        return regroupements;
    }
    
    /**
     * Récupérer toutes les réservations non assignées pour une date donnée.
     * Une réservation est non assignée quand aucun véhicule disponible ne peut la prendre
     * (capacité insuffisante ou tous les véhicules sont utilisés).
     */
    public List<Reservation> getReservationsNonAssignees(Date date) {
        List<RegroupementDTO> regroupements = genererRegroupements(date);
        List<Reservation> nonAssignees = new ArrayList<>();
        
        for (RegroupementDTO groupe : regroupements) {
            nonAssignees.addAll(groupe.getReservationsNonAssignees());
        }
        
        return nonAssignees;
    }

    /**
     * Ajouter une réservation au planning d'un véhicule
     */
    private void ajouterReservationAuPlanning(VehiculePlanningDTO vehiculePlanning,
                                               Reservation resa, Integer aeroportId, int delaiAttente) {
        Integer lieuHotelId = getLieuIdByHotelId(resa.getHotel());
        String hotelLibelle = getHotelLibelle(resa.getHotel());

        ReservationPlanningDTO resaDTO = new ReservationPlanningDTO();
        resaDTO.setReservation(resa);
        resaDTO.setHotelLibelle(hotelLibelle);
        resaDTO.setLieuHotelId(lieuHotelId);
        resaDTO.setDistanceKm(getDistance(aeroportId, lieuHotelId));
        resaDTO.setTempsAttenteMin(delaiAttente);

        vehiculePlanning.addReservation(resaDTO);
    }

    private List<Vehicule> getVehiculesUtilises(List<VehiculePlanningDTO> planning) {
        List<Vehicule> result = new ArrayList<>();
        for (VehiculePlanningDTO vp : planning) {
            result.add(vp.getVehicule());
        }
        return result;
    }

    private boolean isVehiculeUtilise(List<Vehicule> utilises, Vehicule v) {
        for (Vehicule u : utilises) {
            if (u.getId().equals(v.getId())) return true;
        }
        return false;
    }

    /**
     * Calculer l'itinéraire optimal pour un véhicule.
     * Ordre des dépôts par proximité (nearest neighbor).
     * Distance totale : TNR -> hotel1 -> hotel2 -> ... -> TNR
     * 
     * Sprint 5: L'heure de départ est passée en paramètre (commune à tout le groupe)
     */
    private void calculerItineraire(VehiculePlanningDTO vp, Integer aeroportId, Time heureDepartGroupe) {
        List<ReservationPlanningDTO> reservations = vp.getReservations();
        if (reservations.isEmpty()) return;

        Vehicule vehicule = vp.getVehicule();
        double vitesse = vehicule.getVitesseMoyenne();

        // Trier les dépôts par proximité (nearest neighbor depuis TNR)
        List<ReservationPlanningDTO> ordreDepot = new ArrayList<>();
        List<ReservationPlanningDTO> restants = new ArrayList<>(reservations);
        Integer posId = aeroportId;

        while (!restants.isEmpty()) {
            ReservationPlanningDTO plusProche = null;
            double distMin = Double.MAX_VALUE;

            for (ReservationPlanningDTO rp : restants) {
                double dist = getDistance(posId, rp.getLieuHotelId());
                if (dist < distMin) {
                    distMin = dist;
                    plusProche = rp;
                } else if (dist == distMin && plusProche != null) {
                    // À distance égale, choisir l'hôtel dont le nom vient en premier alphabétiquement
                    String libelleActuel = plusProche.getHotelLibelle() != null ? plusProche.getHotelLibelle() : "";
                    String libelleCandidat = rp.getHotelLibelle() != null ? rp.getHotelLibelle() : "";
                    if (libelleCandidat.compareToIgnoreCase(libelleActuel) < 0) {
                        plusProche = rp;
                    }
                }
            }
            if (plusProche == null) break;

            ordreDepot.add(plusProche);
            posId = plusProche.getLieuHotelId();
            restants.remove(plusProche);
        }

        vp.setReservations(ordreDepot);

        // Sprint 5: L'heure de départ est fixée par le groupe (passée en paramètre)
        // Tous les véhicules du même groupe partent à la même heure
        vp.setHeureDepart(heureDepartGroupe);

        // Parcourir l'itinéraire : aéroport -> hôtel1 -> hôtel2 -> ... -> aéroport
        double distanceTotale = 0.0;
        posId = aeroportId;
        Time heureCourante = heureDepartGroupe;

        for (ReservationPlanningDTO rp : ordreDepot) {
            double distSegment = getDistance(posId, rp.getLieuHotelId());
            int tempsSegmentMin = calculerTempsTrajetMinutes(distSegment, vitesse);
            distanceTotale += distSegment;

            // Départ du segment (depuis position courante vers cet hôtel)
            rp.setHeureDepart(heureCourante);

            // Arrivée à l'hôtel = départ + temps de trajet
            Time heureArrivee = ajouterMinutes(heureCourante, tempsSegmentMin);
            rp.setHeureRetour(heureArrivee);

            // Le prochain segment part de cet hôtel (après dépôt des clients)
            heureCourante = heureArrivee;
            posId = rp.getLieuHotelId();
        }

        // Retour TNR
        double distRetour = getDistance(posId, aeroportId);
        distanceTotale += distRetour;
        int tempsRetourMin = calculerTempsTrajetMinutes(distRetour, vitesse);
        Time heureRetourAeroport = ajouterMinutes(heureCourante, tempsRetourMin);

        vp.setHeureRetourAeroport(heureRetourAeroport);
        vp.setDistanceTotale(distanceTotale);
    }

    // =========================================================================
    // Disponibilité des véhicules
    // =========================================================================

    /**
     * Récupérer les véhicules disponibles à une date et heure données.
     * Un véhicule est indisponible s'il est en course à cette heure
     * (heureDepart <= heure < heureRetourAeroport).
     */
    public List<Vehicule> getVehiculesDisponibles(Date date, Time heure) {
        List<Vehicule> tousVehicules = getAllVehicules();
        List<VehiculePlanningDTO> planning = genererPlanning(date);

        if (planning.isEmpty()) return tousVehicules;

        // Trouver les véhicules occupés à cette heure
        List<Integer> idsOccupes = new ArrayList<>();
        for (VehiculePlanningDTO vp : planning) {
            Time depart = vp.getHeureDepart();
            Time retour = vp.getHeureRetourAeroport();
            if (depart != null && retour != null
                && !heure.before(depart) && heure.before(retour)) {
                idsOccupes.add(vp.getVehicule().getId());
            }
        }

        List<Vehicule> disponibles = new ArrayList<>();
        for (Vehicule v : tousVehicules) {
            if (!idsOccupes.contains(v.getId())) {
                disponibles.add(v);
            }
        }
        return disponibles;
    }

    /**
     * Récupérer les véhicules non assignés pour une date donnée.
     * Un véhicule non assigné n'apparait pas dans le planning généré pour la date.
     */
    public List<Vehicule> getVehiculesNonAssignes(Date date) {
        List<Vehicule> tousVehicules = getAllVehicules();
        List<VehiculePlanningDTO> planning = genererPlanning(date);

        if (planning.isEmpty()) return tousVehicules;

        List<Integer> idsUtilises = new ArrayList<>();
        for (VehiculePlanningDTO vp : planning) {
            if (vp.getVehicule() != null && vp.getVehicule().getId() != null) {
                idsUtilises.add(vp.getVehicule().getId());
            }
        }

        List<Vehicule> libres = new ArrayList<>();
        for (Vehicule v : tousVehicules) {
            if (!idsUtilises.contains(v.getId())) {
                libres.add(v);
            }
        }
        return libres;
    }
}
