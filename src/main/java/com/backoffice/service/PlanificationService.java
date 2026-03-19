package com.backoffice.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.backoffice.dto.RegroupementDTO;
import com.backoffice.dto.ReservationPlanningDTO;
import com.backoffice.dto.VehiculePlanningDTO;
import com.backoffice.models.AssignationVehicule;
import com.backoffice.models.ConfigurationAttente;
import com.backoffice.models.Hotel;
import com.backoffice.models.ItineraireArret;
import com.backoffice.models.Lieu;
import com.backoffice.models.Parametre;
import com.backoffice.models.Planification;
import com.backoffice.models.Regroupement;
import com.backoffice.models.RegroupementReservation;
import com.backoffice.models.Reservation;
import com.backoffice.models.SuiviTrajetVehicule;
import com.backoffice.models.Vehicule;
import com.backoffice.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class PlanificationService {

    private static final String CODE_AEROPORT = "TNR";
    private static final int DEFAULT_DELAI_ATTENTE = 30; // Délai par défaut en minutes
    
    // Cache pour l'ID de l'aéroport (évite 3 requêtes BD par génération)
    private static Integer cacheAeroportId = null;

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
        try (EntityManager em = JPAUtil.getEntityManager()) {
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
        }
    }

    /**
     * Récupérer les réservations d'une date, triées par heure croissante
     */
    public List<Reservation> getReservationsByDate(Date date) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Reservation> query = em.createQuery(
                "SELECT r FROM Reservation r WHERE r.date = :date ORDER BY r.heure ASC, r.reference ASC",
                Reservation.class);
            query.setParameter("date", date);
            return query.getResultList();
        }
    }

    /**
     * Récupérer tous les véhicules
     */
    public List<Vehicule> getAllVehicules() {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT v FROM Vehicule v ORDER BY v.place ASC", Vehicule.class)
                     .getResultList();
        }
    }

    /**
     * Récupérer l'ID du lieu aéroport (TNR).
     * Utilise un cache static pour éviter 3 requêtes BD par génération.
     */
    public Integer getAeroportId() {
        // Retourner le cache s'il existe
        if (cacheAeroportId != null) {
            return cacheAeroportId;
        }
        
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<Integer> query = em.createQuery(
                "SELECT l.id FROM Lieu l WHERE l.code = :code", Integer.class);
            query.setParameter("code", CODE_AEROPORT);
            List<Integer> result = query.getResultList();
            if (result.isEmpty() || result.get(0) == null) {
                cacheAeroportId = 1; // valeur par défaut
            } else {
                cacheAeroportId = result.get(0);
            }
            return cacheAeroportId;
        }
    }

    /**
     * Récupérer l'ID du lieu correspondant à un hôtel (par libellé)
     */
    public Integer getLieuIdByHotelId(Integer hotelId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Hotel hotel = em.find(Hotel.class, hotelId);
            if (hotel == null) return null;

            TypedQuery<Integer> query = em.createQuery(
                "SELECT l.id FROM Lieu l WHERE LOWER(l.libelle) = LOWER(:libelle)", Integer.class);
            query.setParameter("libelle", hotel.getLibelle());
            List<Integer> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        }
    }

    /**
     * Récupérer la distance entre deux lieux
     */
    public Double getDistance(Integer lieuDepartId, Integer lieuArriveeId) {
        if (lieuDepartId == null || lieuArriveeId == null) return 0.0;
        if (lieuDepartId.equals(lieuArriveeId)) return 0.0;
        try (EntityManager em = JPAUtil.getEntityManager()) {
            // Chercher dans les deux sens (la distance est symétrique)
            TypedQuery<Double> query = em.createQuery(
                "SELECT d.km FROM Distance d WHERE " +
                "(d.lieuDepart.id = :a AND d.lieuArrivee.id = :b) OR " +
                "(d.lieuDepart.id = :b AND d.lieuArrivee.id = :a)",
                Double.class);
            query.setParameter("a", lieuDepartId);
            query.setParameter("b", lieuArriveeId);
            List<Double> result = query.getResultList();
            if (result.isEmpty() || result.get(0) == null) {
                return 0.0;
            }
            return result.get(0);
        }
    }

    /**
     * Récupérer le libellé d'un hôtel
     */
    public String getHotelLibelle(Integer hotelId) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Hotel hotel = em.find(Hotel.class, hotelId);
            return hotel != null ? hotel.getLibelle() : "Inconnu";
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
     * Règle : places >= nombre, le plus proche. 
     * Critères de tri (par ordre de priorité) :
     * 1. Capacité (places le plus proche)
     * 2. Nombre de trajets (le moins de trajets)
     * 3. Carburant (Diesel prioritaire)
     * 4. Random si tout est identique
     */
    public Vehicule trouverMeilleurVehicule(int nombrePersonnes, List<Vehicule> vehiculesDisponibles, 
                                            Map<Integer, Integer> nombreTrajetsParVehicule) {
        List<Vehicule> candidats = new ArrayList<>();
        for (Vehicule v : vehiculesDisponibles) {
            if (v.getPlace() >= nombrePersonnes) {
                candidats.add(v);
            }
        }
        if (candidats.isEmpty()) return null;

        // Trier par : places (plus proche), puis nombre de trajets (moins), puis carburant (Diesel)
        candidats.sort((v1, v2) -> {
            // 1. Trier par nombre de places ASC (plus proche)
            int cmpPlaces = Integer.compare(v1.getPlace(), v2.getPlace());
            if (cmpPlaces != 0) return cmpPlaces;
            
            // 2. Trier par nombre de trajets ASC (moins de trajets en priorité)
            int trajets1 = nombreTrajetsParVehicule.getOrDefault(v1.getId(), 0);
            int trajets2 = nombreTrajetsParVehicule.getOrDefault(v2.getId(), 0);
            int cmpTrajets = Integer.compare(trajets1, trajets2);
            if (cmpTrajets != 0) return cmpTrajets;
            
            // 3. Trier par carburant (Diesel prioritaire)
            return Integer.compare(getPrioriteCarburant(v1), getPrioriteCarburant(v2));
        });

        // Si plusieurs avec mêmes attributs -> random
        int bestPlaces = candidats.get(0).getPlace();
        int bestTrajets = nombreTrajetsParVehicule.getOrDefault(candidats.get(0).getId(), 0);
        int bestPrio = getPrioriteCarburant(candidats.get(0));
        List<Vehicule> meilleurs = new ArrayList<>();
        for (Vehicule v : candidats) {
            int trajets = nombreTrajetsParVehicule.getOrDefault(v.getId(), 0);
            if (v.getPlace() == bestPlaces && trajets == bestTrajets && getPrioriteCarburant(v) == bestPrio) {
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
        return switch (code) {
            case "D" -> 1;   // Diesel - priorité maximale
            case "Es" -> 2;  // Essence - deuxième priorité
            default -> 3;    // Autres (Hybride, Electrique, etc.) - même priorité, random
        };
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
     * 
     * Les réservations non assignées à un groupe sont reportées au groupe suivant et réessayées.
     */
    public List<RegroupementDTO> genererRegroupements(Date date) {
        List<Reservation> reservations = getReservationsByDate(date);
        if (reservations.isEmpty()) return new ArrayList<>();

        List<Vehicule> tousVehicules = getAllVehicules();
        int delaiAttente = getDelaiAttente();
        Integer aeroportId = getAeroportId();

        // Trier par heure ASC puis référence ASC pour un ordre stable si même heure
        reservations.sort((r1, r2) -> {
            int cmpHeure = r1.getHeure().compareTo(r2.getHeure());
            if (cmpHeure != 0) {
                return cmpHeure;
            }
            return Integer.compare(r1.getReference(), r2.getReference());
        });

        // 1. Initialiser tous les maps de tracking
        TrackingData tracking = initializerTrackingMaps(tousVehicules, reservations.size());

        List<RegroupementDTO> regroupements = new ArrayList<>();
        int numeroGroupe = 1;

        // 2. Boucle principale pour créer les regroupements
        for (int i = 0; i < reservations.size(); i++) {
            if (tracking.assignees[i]) continue;

            // 3. Créer un nouveau groupe pour cette réservation
            Reservation premiereDuGroupe = reservations.get(i);
            RegroupementDTO groupe = creerIntervalleGroupe(numeroGroupe++, premiereDuGroupe, tracking.indicesReportees, 
                                                            reservations, delaiAttente);

            // 4. Déterminer les véhicules disponibles pour ce groupe
            Time heureDepartGroupe = groupe.getHeureDepart();
            List<Integer> indicesGroupe = collecterIndicesGroupe(groupe, reservations, tracking.assignees);
            List<Vehicule> vehiculesDisponibles = determinerVehiculesDisponibles(tousVehicules, 
                                                                                   heureDepartGroupe, 
                                                                                   tracking.heureRetourParVehicule);

            // 5. Assigner les véhicules et réservations
            Time debutIntervalle = groupe.getHeureDepart();  // première réservation
            Time finIntervalle = ajouterMinutes(debutIntervalle, delaiAttente);
            
            List<VehiculePlanningDTO> vehiculesGroupe = assignerVehiculesAuGroupe(
                groupe, indicesGroupe, reservations, tracking, aeroportId, 
                delaiAttente, vehiculesDisponibles, debutIntervalle, finIntervalle
            );

            // 6. Finaliser le groupe (itinéraires, tracking, reportées)
            finaliserGroupe(groupe, vehiculesGroupe, tracking, regroupements);
            
            // 7. Mettre à jour les indices reportés
            mettreAJourIndicesReportees(groupe, reservations, tracking);
        }

        // 8. Nettoyage des réservations non-assignées (retirer les fausses ou doublons)
        Set<Integer> resasVuesDansNonAssignees = new HashSet<>();
        // Parcourir à l'envers pour conserver uniquement la dernière tentative d'une réservation non-assignée
        for (int i = regroupements.size() - 1; i >= 0; i--) {
            RegroupementDTO groupe = regroupements.get(i);
            groupe.getReservationsNonAssignees().removeIf(r -> {
                // Trouver l'indice de la réservation
                int idx = -1;
                for (int j = 0; j < reservations.size(); j++) {
                    if (reservations.get(j).getId().equals(r.getId())) {
                        idx = j;
                        break;
                    }
                }
                // Si elle a été assignée finalement ailleurs, on l'enlève de la liste des non-assignées
                if (idx != -1 && tracking.assignees[idx]) {
                    return true;
                }
                
                // Si elle est vraiment non-assignée, on s'assure de la ne garder qu'une seule fois
                if (resasVuesDansNonAssignees.contains(r.getId())) {
                    return true;
                } else {
                    resasVuesDansNonAssignees.add(r.getId());
                    return false;
                }
            });
        }

        // 9. Retirer les groupes devenus complètement vides (fantômes) et renuméroter
        regroupements.removeIf(g -> g.getReservations().isEmpty() && g.getReservationsNonAssignees().isEmpty());
        int numReel = 1;
        for (RegroupementDTO g : regroupements) {
            g.setNumeroGroupe(numReel++);
        }

        return regroupements;
    }

    /**
     * Structure pour regrouper tous les tracking maps ensemble.
     * Évite de passer 10 paramètres partout.
     */
    private static class TrackingData {
        boolean[] assignees;
        Map<Integer, Time> heureRetourParVehicule;
        Map<Integer, Integer> nombreTrajetsParVehicule;
        Set<Integer> indicesReportees;

        TrackingData(int nbReservations, List<Vehicule> tousVehicules) {
            this.assignees = new boolean[nbReservations];
            this.heureRetourParVehicule = new HashMap<>();
            this.nombreTrajetsParVehicule = new HashMap<>();
            this.indicesReportees = new HashSet<>();
            
            for (Vehicule v : tousVehicules) {
                heureRetourParVehicule.put(v.getId(), null);
                nombreTrajetsParVehicule.put(v.getId(), 0);
            }
        }
    }

    /**
     * Initialiser tous les maps de tracking pour les véhicules.
     */
    private TrackingData initializerTrackingMaps(List<Vehicule> tousVehicules, int nbReservations) {
        return new TrackingData(nbReservations, tousVehicules);
    }

    /**
     * Créer l'intervalle d'un nouveau groupe.
     * Ajouter TOUTES les réservations qui tombent dans l'intervalle [debut, debut+delaiAttente].
     */
    private RegroupementDTO creerIntervalleGroupe(int numeroGroupe, Reservation premiereDuGroupe, 
                                                   Set<Integer> indicesReportees, List<Reservation> reservations,
                                                   int delaiAttente) {
        Time debutIntervalle = premiereDuGroupe.getHeure();
        Time finIntervalle = ajouterMinutes(debutIntervalle, delaiAttente);
        
        RegroupementDTO groupe = new RegroupementDTO(numeroGroupe, debutIntervalle, finIntervalle, delaiAttente);
        
        // Tracker les indices déjà ajoutés pour éviter les doublons DANS CE GROUPE
        Set<Integer> indicesAjoutes = new HashSet<>();
        
        // Trouver l'indice de la première réservation
        int indexPremiere = reservations.indexOf(premiereDuGroupe);
        
        // Ajouter la première réservation
        groupe.ajouterReservation(premiereDuGroupe);
        indicesAjoutes.add(indexPremiere);
        
        // Ajouter les réservations reportées du groupe précédent (sauf si déjà ajoutées)
        for (int indiceReporte : indicesReportees) {
            if (!indicesAjoutes.contains(indiceReporte)) {
                groupe.ajouterReservation(reservations.get(indiceReporte));
                indicesAjoutes.add(indiceReporte);
            }
        }
        
        // Ajouter les réservations suivantes qui tombent dans l'intervalle (sauf celles déjà ajoutées)
        for (int i = indexPremiere + 1; i < reservations.size(); i++) {
            // Sauter si cette réservation est déjà ajoutée à ce groupe
            if (indicesAjoutes.contains(i)) {
                continue;
            }
            
            Reservation resa = reservations.get(i);
            // Si l'heure de la réservation <= finIntervalle, elle rentre dans le groupe
            if (resa.getHeure().compareTo(finIntervalle) <= 0) {
                groupe.ajouterReservation(resa);
                indicesAjoutes.add(i);
            } else {
                // Dès qu'on sort de l'intervalle, on arrête (car réservations triées par heure)
                break;
            }
        }
        
        return groupe;
    }

    /**
     * Collecter tous les indices des réservations du groupe.
     * Optimisé en O(n) avec une Map au lieu de O(n²) avec double boucle.
     */
    private List<Integer> collecterIndicesGroupe(RegroupementDTO groupe, List<Reservation> reservations, 
                                                  boolean[] assignees) {
        // Créer une map: Reservation → indice pour recherche O(1)
        Map<Reservation, Integer> resaToIndex = new HashMap<>();
        for (int i = 0; i < reservations.size(); i++) {
            resaToIndex.put(reservations.get(i), i);
        }
        
        // Collecter les indices en O(n)
        List<Integer> indices = new ArrayList<>();
        for (Reservation resa : groupe.getReservations()) {
            Integer index = resaToIndex.get(resa);
            if (index != null && !assignees[index]) {
                indices.add(index);
            }
        }
        
        return indices;
    }

    /**
     * Déterminer quels véhicules sont disponibles pour ce groupe.
     * Un véhicule est disponible s'il n'a jamais été utilisé (null) ou 
     * s'il revient avant le départ du groupe.
     */
    private List<Vehicule> determinerVehiculesDisponibles(List<Vehicule> tousVehicules, 
                                                           Time heureDepartGroupe,
                                                           Map<Integer, Time> heureRetourParVehicule) {
        List<Vehicule> disponibles = new ArrayList<>();
        
        for (Vehicule v : tousVehicules) {
            Time heureRetourVehicule = heureRetourParVehicule.get(v.getId());
            // Disponible si: jamais utilisé (null) OU revenant avant le départ du groupe
            if (heureRetourVehicule == null || heureRetourVehicule.compareTo(heureDepartGroupe) <= 0) {
                disponibles.add(v);
            }
        }
        
        return disponibles;
    }

    /**
     * Assigner les véhicules aux réservations du groupe.
     * Stratégie 1: Essayer 1 seul véhicule
     * Stratégie 2: Bin-packing si nécessaire
     */
    private List<VehiculePlanningDTO> assignerVehiculesAuGroupe(
        RegroupementDTO groupe, List<Integer> indicesGroupe, List<Reservation> reservations,
        TrackingData tracking, Integer aeroportId, int delaiAttente, List<Vehicule> vehiculesDisponibles,
        Time debutIntervalle, Time finIntervalle
    ) {
        int totalPersonnesGroupe = groupe.getNombrePersonnesTotal();
        
        // STRATÉGIE 1 : Essayer de trouver UN véhicule pour tout le groupe
        Vehicule vehiculePourTout = trouverMeilleurVehicule(totalPersonnesGroupe, vehiculesDisponibles, 
                                                            tracking.nombreTrajetsParVehicule);
        
        if (vehiculePourTout != null) {
            assignerVehiculeStrategie1(vehiculePourTout, groupe, indicesGroupe, reservations, tracking, 
                                      aeroportId, delaiAttente, debutIntervalle, finIntervalle, vehiculesDisponibles);
        } else {
            // STRATÉGIE 2 : Bin-packing
            assignerVehiculeStrategie2(groupe, indicesGroupe, reservations, tracking, aeroportId, 
                                      delaiAttente, debutIntervalle, finIntervalle, vehiculesDisponibles);
        }
        
        return groupe.getVehiculesAssignes();
    }

    /**
     * STRATÉGIE 1: Un seul véhicule peut accueillir tout le groupe.
     */
    private void assignerVehiculeStrategie1(Vehicule vehiculePourTout, RegroupementDTO groupe, 
                                           List<Integer> indicesGroupe, List<Reservation> reservations,
                                           TrackingData tracking, Integer aeroportId, int delaiAttente,
                                           Time debutIntervalle, Time finIntervalle, List<Vehicule> vehiculesDisponibles) {
        VehiculePlanningDTO vp = new VehiculePlanningDTO(vehiculePourTout);
        vp.setHeureDebutIntervalle(debutIntervalle);
        vp.setHeureFinIntervalle(finIntervalle);
        
        // Ajouter TOUTES les réservations à ce véhicule
        for (int idx : indicesGroupe) {
            Reservation resa = reservations.get(idx);
            tracking.assignees[idx] = true;
            ajouterReservationAuPlanning(vp, resa, aeroportId, delaiAttente);
        }
        
        groupe.ajouterVehicule(vp);
        vehiculesDisponibles.remove(vehiculePourTout);
        
        // Incrémenter le nombre de trajets
        tracking.nombreTrajetsParVehicule.put(vehiculePourTout.getId(), 
            tracking.nombreTrajetsParVehicule.get(vehiculePourTout.getId()) + 1);
    }

    /**
     * STRATÉGIE 2: Bin-packing - remplir les véhicules au maximum.
     */
    private void assignerVehiculeStrategie2(RegroupementDTO groupe, List<Integer> indicesGroupe,
                                           List<Reservation> reservations, TrackingData tracking,
                                           Integer aeroportId, int delaiAttente,
                                           Time debutIntervalle, Time finIntervalle, List<Vehicule> vehiculesDisponibles) {
        // Trier réservations par nombre de personnes DESC (gros groupes d'abord)
        indicesGroupe.sort((a, b) -> Integer.compare(
            reservations.get(b).getNombre(), 
            reservations.get(a).getNombre()
        ));

        for (int idx : indicesGroupe) {
            if (tracking.assignees[idx]) continue;
            
            Reservation resa = reservations.get(idx);
            
            // 1. Essayer de trouver un véhicule du groupe qui a encore de la place
            VehiculePlanningDTO vehiculeAvecPlace = trouverVehiculeAvecPlace(groupe.getVehiculesAssignes(), resa);

            if (vehiculeAvecPlace != null) {
                // Ajouter à un véhicule existant du groupe
                tracking.assignees[idx] = true;
                ajouterReservationAuPlanning(vehiculeAvecPlace, resa, aeroportId, delaiAttente);
            } else {
                // 2. Besoin d'un nouveau véhicule
                Vehicule meilleur = trouverMeilleurVehicule(resa.getNombre(), vehiculesDisponibles, 
                                                           tracking.nombreTrajetsParVehicule);
                
                if (meilleur == null) {
                    // Aucun véhicule disponible - réservation non assignée
                    groupe.ajouterReservationNonAssignee(resa);
                    // NE PAS METTRE à TRUE car elle doit être reportée
                    tracking.assignees[idx] = false;
                    continue;
                }

                // Créer et assigner le nouveau véhicule
                VehiculePlanningDTO nouveauVehicule = new VehiculePlanningDTO(meilleur);
                nouveauVehicule.setHeureDebutIntervalle(debutIntervalle);
                nouveauVehicule.setHeureFinIntervalle(finIntervalle);
                
                groupe.ajouterVehicule(nouveauVehicule);
                vehiculesDisponibles.remove(meilleur);
                
                // Incrémenter le nombre de trajets
                tracking.nombreTrajetsParVehicule.put(meilleur.getId(), 
                    tracking.nombreTrajetsParVehicule.get(meilleur.getId()) + 1);
                
                tracking.assignees[idx] = true;
                ajouterReservationAuPlanning(nouveauVehicule, resa, aeroportId, delaiAttente);
            }
        }
    }

    /**
     * Chercher un véhicule déjà assigné au groupe qui a encore de la place.
     */
    private VehiculePlanningDTO trouverVehiculeAvecPlace(List<VehiculePlanningDTO> vehiculesGroupe, Reservation resa) {
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
        
        return vehiculeAvecPlace;
    }

    /**
     * Finaliser un groupe: calculer les itinéraires et mettre à jour le tracking.
     */
    private void finaliserGroupe(RegroupementDTO groupe, List<VehiculePlanningDTO> vehiculesGroupe,
                                 TrackingData tracking, List<RegroupementDTO> regroupements) {
        // Heure de départ = heure de la DERNIÈRE réservation du groupe
        List<Reservation> reservationsGroupe = groupe.getReservations();
        Time heureDepartGroupe = reservationsGroupe.isEmpty() ? groupe.getHeureDepart() : 
                                 reservationsGroupe.get(reservationsGroupe.size() - 1).getHeure();
        
        // Mettre à jour l'heure de départ du groupe pour la vue/UI
        groupe.setHeureDepart(heureDepartGroupe);

        Integer aeroportId = getAeroportId();

        // Calculer les itinéraires avec l'heure de départ COMMUNE du groupe
        for (VehiculePlanningDTO vp : vehiculesGroupe) {
            vp.setNombrePassagers(vp.calculerNombrePassagers());
            calculerItineraire(vp, aeroportId, heureDepartGroupe);
            
            // Mettre à jour l'heure de retour du véhicule pour les groupes suivants
            tracking.heureRetourParVehicule.put(vp.getVehicule().getId(), vp.getHeureRetourAeroport());
        }
        
        regroupements.add(groupe);
    }

    /**
     * Mettre à jour les indices des réservations reportées pour le groupe suivant.
     */
    private void mettreAJourIndicesReportees(RegroupementDTO groupe, List<Reservation> reservations, 
                                            TrackingData tracking) {
        tracking.indicesReportees.clear();
        
        for (Reservation r : groupe.getReservationsNonAssignees()) {
            // Trouver l'indice de cette réservation dans le tableau principal
            for (int j = 0; j < reservations.size(); j++) {
                if (reservations.get(j).getId().equals(r.getId()) && !tracking.assignees[j]) {
                    tracking.indicesReportees.add(j);
                    break;
                }
            }
        }
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

    // =========================================================================
    // Persistence et sauvegarde des planifications (Sprint 5+)
    // =========================================================================

    /**
     * Charger ou générer une planification pour une date donnée.
     * 
     * Logique:
     * 1. Chercher une planification ACTIVE pour la date
     * 2. Si trouvée: charger depuis la BD
     * 3. Si NOT trouvée: générer, sauvegarder, puis retourner
     */
    public List<RegroupementDTO> chargerOuGenerer(Date date) {
        List<RegroupementDTO> planificationExistante = chargerPlanification(date);
        if (planificationExistante != null && !planificationExistante.isEmpty()) {
            // Les données chargées depuis BD peuvent manquer le détail des réservations par véhicule.
            // Dans ce cas, on régénère en mémoire pour l'affichage de la répartition.
            if (hasMissingReservationBreakdown(planificationExistante)) {
                return genererRegroupements(date);
            }
            return planificationExistante;
        }

        List<RegroupementDTO> regroupements = genererRegroupements(date);
        try {
            sauvegarderPlanification(date, regroupements);
            return regroupements;
        } catch (RuntimeException e) {
            // Cas de concurrence: une autre requête vient de sauvegarder la même date.
            if (isDuplicateDatePlanification(e)) {
                List<RegroupementDTO> planificationConcurrente = chargerPlanification(date);
                if (planificationConcurrente != null && !planificationConcurrente.isEmpty()) {
                    return planificationConcurrente;
                }
            }
            throw e;
        }
    }

    /**
     * Vérifie si au moins un véhicule n'a pas le détail des réservations.
     */
    private boolean hasMissingReservationBreakdown(List<RegroupementDTO> regroupements) {
        for (RegroupementDTO groupe : regroupements) {
            for (VehiculePlanningDTO vp : groupe.getVehiculesAssignes()) {
                if (vp.getReservations() == null || vp.getReservations().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Charger une planification existante depuis la BD pour une date donnée.
     * Retourne NULL si aucune planification ACTIVE n'existe.
     */
    public List<RegroupementDTO> chargerPlanification(Date date) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Planification planificationModel = trouverPlanificationActive(em, date);
            if (planificationModel == null) {
                return null;
            }

            return chargerRegroupementsFromBD(em, planificationModel);
        }
    }

    /**
     * Chercher la planification ACTIVE pour une date donnée.
     */
    private Planification trouverPlanificationActive(EntityManager em, Date date) {
        TypedQuery<Planification> query = em.createQuery(
            "SELECT p FROM Planification p WHERE p.datePlanification = :date AND p.statut = 'ACTIVE'",
            Planification.class);
        query.setParameter("date", date);
        List<Planification> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Charger tous les regroupements pour une planification donnée depuis la BD.
     */
    private List<RegroupementDTO> chargerRegroupementsFromBD(EntityManager em, Planification planification) {
        TypedQuery<Regroupement> query = em.createQuery(
            "SELECT r FROM Regroupement r WHERE r.planification.id = :planifId ORDER BY r.numeroRegroupement ASC",
            Regroupement.class);
        query.setParameter("planifId", planification.getId());
        List<Regroupement> regroupementsDB = query.getResultList();

        List<RegroupementDTO> result = new ArrayList<>();
        for (Regroupement regroup : regroupementsDB) {
            RegroupementDTO dto = chargerRegroupementDetail(em, regroup, planification.getDelaiAttenteUtilise());
            result.add(dto);
        }
        return result;
    }

    /**
     * Charger les détails complets d'un regroupement: réservations + véhicules + itinéraires.
     */
    private RegroupementDTO chargerRegroupementDetail(EntityManager em, Regroupement regroup, Integer delai) {
        RegroupementDTO dto = new RegroupementDTO(
            regroup.getNumeroRegroupement(),
            null,
            null,
            delai
        );

        // Charger les réservations
        List<Reservation> reservations = chargerReservationsRegroupement(em, regroup.getId());
        for (Reservation r : reservations) {
            dto.ajouterReservation(r);
        }

        // Charger les véhicules assignés
        List<AssignationVehicule> assignations = chargerAssignationsVehicules(em, regroup.getId());
        for (AssignationVehicule assignation : assignations) {
            VehiculePlanningDTO vp = convertirAssignationToVehiculeDTO(assignation);
            dto.ajouterVehicule(vp);
        }

        return dto;
    }

    /**
     * Charger les réservations d'un regroupement.
     */
    private List<Reservation> chargerReservationsRegroupement(EntityManager em, Integer regroupementId) {
        TypedQuery<Reservation> query = em.createQuery(
            "SELECT rr.reservation FROM RegroupementReservation rr WHERE rr.regroupement.id = :regroupId",
            Reservation.class);
        query.setParameter("regroupId", regroupementId);
        return query.getResultList();
    }

    /**
     * Charger les assignations de véhicules pour un regroupement.
     */
    private List<AssignationVehicule> chargerAssignationsVehicules(EntityManager em, Integer regroupementId) {
        TypedQuery<AssignationVehicule> query = em.createQuery(
            "SELECT a FROM AssignationVehicule a WHERE a.regroupement.id = :regroupId ORDER BY a.numeroOrdreGroupe ASC",
            AssignationVehicule.class);
        query.setParameter("regroupId", regroupementId);
        return query.getResultList();
    }

    /**
     * Convertir une assignation BD en VehiculePlanningDTO avec itinéraires.
     */
    private VehiculePlanningDTO convertirAssignationToVehiculeDTO(AssignationVehicule assignation) {
        VehiculePlanningDTO vp = new VehiculePlanningDTO(assignation.getVehicule());
        vp.setHeureDepart(assignation.getHeureDepartAeroport());
        vp.setHeureRetourAeroport(assignation.getHeureRetourAeroport());
        vp.setDistanceTotale(assignation.getDistanceTotaleKm());
        vp.setNombrePassagers(assignation.getNombrePassagersTransportes());

        // Les itinéraires sont disponibles via assignation.getItineraireArrets()
        // Mais les ReservationPlanningDTO ne sont pas reconstruits (optimisation pour affichage simple)
        // En cas de besoin futur, implémenter le chargement des ItineraireArret

        return vp;
    }

    /**
     * Sauvegarder une planification complète en BD avec tous ses regroupements et véhicules.
     */
    public void sauvegarderPlanification(Date date, List<RegroupementDTO> regroupements) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Créer ou réutiliser la planification principale (idempotent par date)
            Planification planification = trouverPlanificationParDate(em, date);
            Planification stats = creerPlanification(regroupements);

            if (planification == null) {
                planification = stats;
                planification.setDatePlanification(date);
                em.persist(planification);
            } else {
                // Recalcul d'une date déjà existante: nettoyer les détails avant de réécrire
                nettoyerDetailsPlanification(em, planification.getId());
                planification.setStatut("ACTIVE");
                planification.setDelaiAttenteUtilise(stats.getDelaiAttenteUtilise());
                planification.setNombreRegroupements(stats.getNombreRegroupements());
                planification.setNombreReservationsTotal(stats.getNombreReservationsTotal());
                planification.setNombreReservationsAssignees(stats.getNombreReservationsAssignees());
                planification.setNombreVehiculesUtilises(stats.getNombreVehiculesUtilises());
                em.merge(planification);
            }

            // 2. Sauvegarder chaque regroupement avec ses assignations
            for (RegroupementDTO groupeDTO : regroupements) {
                sauvegarderRegroupement(em, planification, groupeDTO);
            }

            // 3. Créer les enregistrements de suivi par véhicule
            sauvegarderSuiviVehicules(em, planification, regroupements);

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde de la planification: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

        /**
         * Chercher une planification existante pour une date donnée, peu importe son statut.
         */
        private Planification trouverPlanificationParDate(EntityManager em, Date date) {
                TypedQuery<Planification> query = em.createQuery(
                        "SELECT p FROM Planification p WHERE p.datePlanification = :date ORDER BY p.id DESC",
                        Planification.class);
                query.setParameter("date", date);
                query.setMaxResults(1);
                List<Planification> result = query.getResultList();
                return result.isEmpty() ? null : result.get(0);
        }

        /**
         * Supprimer les détails liés à une planification existante avant recalcul.
         */
        private void nettoyerDetailsPlanification(EntityManager em, Integer planificationId) {
                em.createQuery("DELETE FROM ItineraireArret ia WHERE ia.assignationVehicule.id IN (" +
                                            "SELECT a.id FROM AssignationVehicule a WHERE a.regroupement.id IN (" +
                                            "SELECT r.id FROM Regroupement r WHERE r.planification.id = :planifId))")
                    .setParameter("planifId", planificationId)
                    .executeUpdate();

                em.createQuery("DELETE FROM AssignationVehicule a WHERE a.regroupement.id IN (" +
                                            "SELECT r.id FROM Regroupement r WHERE r.planification.id = :planifId)")
                    .setParameter("planifId", planificationId)
                    .executeUpdate();

                em.createQuery("DELETE FROM RegroupementReservation rr WHERE rr.regroupement.id IN (" +
                                            "SELECT r.id FROM Regroupement r WHERE r.planification.id = :planifId)")
                    .setParameter("planifId", planificationId)
                    .executeUpdate();

                em.createQuery("DELETE FROM Regroupement r WHERE r.planification.id = :planifId")
                    .setParameter("planifId", planificationId)
                    .executeUpdate();

                em.createQuery("DELETE FROM SuiviTrajetVehicule s WHERE s.planification.id = :planifId")
                    .setParameter("planifId", planificationId)
                    .executeUpdate();
        }

    /**
     * Détecter une violation d'unicité sur date_planification (collision de concurrence).
     */
    private boolean isDuplicateDatePlanification(Throwable error) {
        Throwable current = error;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                String lower = message.toLowerCase();
                boolean hasDatePlanif = lower.contains("date_planification");
                boolean hasUnique = lower.contains("uk_") || lower.contains("duplicate") || lower.contains("doubl") || lower.contains("unique");
                if (hasDatePlanif && hasUnique) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    /**
     * Créer l'entité Planification avec les statistiques calculées.
     */
    private Planification creerPlanification(List<RegroupementDTO> regroupements) {
        int totalReservations = 0;
        int totalAssignees = 0;
        Set<Integer> vehiculesUniques = new HashSet<>();

        for (RegroupementDTO groupe : regroupements) {
            totalReservations += groupe.getReservations().size() + groupe.getReservationsNonAssignees().size();
            totalAssignees += groupe.getReservations().size();
            for (VehiculePlanningDTO vp : groupe.getVehiculesAssignes()) {
                vehiculesUniques.add(vp.getVehicule().getId());
            }
        }

        Planification planification = new Planification();
        planification.setStatut("ACTIVE");
        planification.setDelaiAttenteUtilise(getDelaiAttente());
        planification.setNombreRegroupements(regroupements.size());
        planification.setNombreReservationsTotal(totalReservations);
        planification.setNombreReservationsAssignees(totalAssignees);
        planification.setNombreVehiculesUtilises(vehiculesUniques.size());

        return planification;
    }

    /**
     * Sauvegarder les associations regroupement-réservations.
     * Helper pour éviter la duplication de code.
     */
    private void sauvegarderMappingsRegroupementReservations(EntityManager em, Regroupement regroupement, 
                                                             RegroupementDTO groupeDTO) {
        // Tracker les IDs déjà ajoutés pour éviter les duplicatas dans la base (ConstraintViolationException)
        Set<Integer> idsAjoutes = new HashSet<>();
        
        // Sauvegarder TOUTES les réservations (assignees + non-assignees) en une boucle
        List<Reservation> toutesReservations = new ArrayList<>();
        toutesReservations.addAll(groupeDTO.getReservations());
        toutesReservations.addAll(groupeDTO.getReservationsNonAssignees());
        
        for (Reservation resa : toutesReservations) {
            if (resa != null && resa.getId() != null && !idsAjoutes.contains(resa.getId())) {
                em.persist(new RegroupementReservation(regroupement, resa));
                idsAjoutes.add(resa.getId());
            }
        }
    }

    /**
     * Sauvegarder un regroupement complet avec ses réservations, véhicules et itinéraires.
     */
    private void sauvegarderRegroupement(EntityManager em, Planification planification, RegroupementDTO groupeDTO) {
        Regroupement regroupement = creerRegroupement(planification, groupeDTO);
        em.persist(regroupement);

        // Sauvegarder les associations regroupement-réservations (optimisé)
        sauvegarderMappingsRegroupementReservations(em, regroupement, groupeDTO);

        // Sauvegarder les assignations de véhicules
        for (int i = 0; i < groupeDTO.getVehiculesAssignes().size(); i++) {
            sauvegarderAssignationVehicule(em, regroupement, groupeDTO.getVehiculesAssignes().get(i), i + 1);
        }
    }
    private Regroupement creerRegroupement(Planification planification, RegroupementDTO groupeDTO) {
        Regroupement regroupement = new Regroupement();
        regroupement.setPlanification(planification);
        regroupement.setNumeroRegroupement(groupeDTO.getNumeroGroupe());
        regroupement.setHeureDepartGroupe(groupeDTO.getHeureDepart());
        regroupement.setNombreReservations(groupeDTO.getReservations().size());
        regroupement.setNombrePassagersTotal(groupeDTO.getNombrePersonnesTotal());
        regroupement.setNombreVehiculesAssignes(groupeDTO.getVehiculesAssignes().size());
        return regroupement;
    }

    /**
     * Sauvegarder une assignation de véhicule avec son itinéraire complet.
     */
    private void sauvegarderAssignationVehicule(EntityManager em, Regroupement regroupement, VehiculePlanningDTO vpDTO, int numeroOrdre) {
        AssignationVehicule assignation = creerAssignationVehicule(regroupement, vpDTO, numeroOrdre);
        em.persist(assignation);

        // Sauvegarder les itinéraires (arrêts)
        sauvegarderItineraires(em, assignation, vpDTO);
    }

    /**
     * Créer l'entité AssignationVehicule.
     */
    private AssignationVehicule creerAssignationVehicule(Regroupement regroupement, VehiculePlanningDTO vpDTO, int numeroOrdre) {
        AssignationVehicule assignation = new AssignationVehicule();
        assignation.setRegroupement(regroupement);
        assignation.setVehicule(vpDTO.getVehicule());
        assignation.setNumeroOrdreGroupe(numeroOrdre);
        assignation.setHeureDepartAeroport(vpDTO.getHeureDepart());
        assignation.setHeureRetourAeroport(vpDTO.getHeureRetourAeroport());
        assignation.setDistanceTotaleKm(vpDTO.getDistanceTotale());
        
        // Calculer le temps total en minutes
        int timeMinutes = (int) (vpDTO.getDistanceTotale() / vpDTO.getVehicule().getVitesseMoyenne() * 60);
        assignation.setTempsTotalMinutes(timeMinutes);
        assignation.setNombrePassagersTransportes(vpDTO.getNombrePassagers());
        
        return assignation;
    }

    /**
     * Sauvegarder les itinéraires (arrêts) pour une assignation de véhicule.
     */
    private void sauvegarderItineraires(EntityManager em, AssignationVehicule assignation, VehiculePlanningDTO vpDTO) {
        Integer aeroportId = getAeroportId();
        Lieu aeroport = em.find(Lieu.class, aeroportId);

        // Premier arrêt = départ aéroport
        em.persist(creerItineraireDepart(assignation, aeroport, vpDTO));

        // Arrêts intermédiaires
        int ordreArret = 2;
        for (ReservationPlanningDTO rpDTO : vpDTO.getReservations()) {
            em.persist(creerItineraireArret(em, assignation, ordreArret++, rpDTO));
        }

        // Dernier arrêt = retour aéroport
        em.persist(creerItineraireRetour(assignation, aeroport, vpDTO, ordreArret));
    }

    /**
     * Créer l'itinéraire de départ (aéroport).
     */
    private ItineraireArret creerItineraireDepart(AssignationVehicule assignation, Lieu aeroport, VehiculePlanningDTO vpDTO) {
        ItineraireArret arret = new ItineraireArret();
        arret.setAssignationVehicule(assignation);
        arret.setOrdreArret(1);
        arret.setLieu(aeroport);
        arret.setHeureArrivee(vpDTO.getHeureDepart());
        arret.setHeureDepart(vpDTO.getHeureDepart());
        arret.setNombrePassagersEmbarques(0);
        return arret;
    }

    /**
     * Créer un itinéraire intermédiaire (hôtel).
     */
    private ItineraireArret creerItineraireArret(EntityManager em, AssignationVehicule assignation, int ordreArret, ReservationPlanningDTO rpDTO) {
        ItineraireArret arret = new ItineraireArret();
        arret.setAssignationVehicule(assignation);
        arret.setOrdreArret(ordreArret);
        arret.setLieu(em.find(Lieu.class, rpDTO.getLieuHotelId()));
        arret.setHotel(em.find(Hotel.class, rpDTO.getReservation().getHotel()));
        arret.setHeureArrivee(rpDTO.getHeureRetour());
        arret.setHeureDepart(rpDTO.getHeureRetour());
        // Important pour les splits: utiliser le nombre réellement transporté sur ce segment.
        Integer passagersSegment = rpDTO.getNombrePassagers() != null
                ? rpDTO.getNombrePassagers()
                : (rpDTO.getReservation() != null ? rpDTO.getReservation().getNombre() : 0);
        arret.setNombrePassagersEmbarques(passagersSegment);
        return arret;
    }

    /**
     * Créer l'itinéraire de retour (aéroport).
     */
    private ItineraireArret creerItineraireRetour(AssignationVehicule assignation, Lieu aeroport, VehiculePlanningDTO vpDTO, int ordreArret) {
        ItineraireArret arret = new ItineraireArret();
        arret.setAssignationVehicule(assignation);
        arret.setOrdreArret(ordreArret);
        arret.setLieu(aeroport);
        arret.setHeureArrivee(vpDTO.getHeureRetourAeroport());
        arret.setHeureDepart(vpDTO.getHeureRetourAeroport());
        arret.setNombrePassagersEmbarques(0);
        return arret;
    }

    /**
     * Sauvegarder le suivi des trajets pour chaque véhicule utilisé.
     */
    private void sauvegarderSuiviVehicules(EntityManager em, Planification planification, List<RegroupementDTO> regroupements) {
        Map<Integer, VehiculeStats> statsParVehicule = calculerStatsVehicules(regroupements);

        for (Map.Entry<Integer, VehiculeStats> entry : statsParVehicule.entrySet()) {
            Vehicule vehicule = em.find(Vehicule.class, entry.getKey());
            VehiculeStats stats = entry.getValue();

            SuiviTrajetVehicule suivi = new SuiviTrajetVehicule();
            suivi.setPlanification(planification);
            suivi.setVehicule(vehicule);
            suivi.setDatePlanification(planification.getDatePlanification());
            suivi.setNombreRegroupementsAssignes(stats.nombreGroupes);
            suivi.setNombrePassagersTotal(stats.totalPassagers);
            suivi.setDistanceTotaleKm(stats.totalDistance);
            suivi.setTempsTotalHeures(stats.totalDistance / vehicule.getVitesseMoyenne());
            suivi.setHeurePremiereUtilisation(stats.heureDepart);
            suivi.setHeureDerniereRetour(stats.heureRetour);

            em.persist(suivi);
        }
    }

    /**
     * Calculer les statistiques pour chaque véhicule.
     * Structure auxiliaire pour optimiser les calculs.
     */
    private Map<Integer, VehiculeStats> calculerStatsVehicules(List<RegroupementDTO> regroupements) {
        Map<Integer, VehiculeStats> stats = new HashMap<>();

        for (RegroupementDTO groupe : regroupements) {
            for (VehiculePlanningDTO vp : groupe.getVehiculesAssignes()) {
                int vehiculeId = vp.getVehicule().getId();
                VehiculeStats stat = stats.computeIfAbsent(vehiculeId, k -> new VehiculeStats());

                stat.nombreGroupes++;
                stat.totalPassagers += vp.getNombrePassagers();
                stat.totalDistance += vp.getDistanceTotale();

                if (stat.heureDepart == null || vp.getHeureDepart().before(stat.heureDepart)) {
                    stat.heureDepart = vp.getHeureDepart();
                }
                if (stat.heureRetour == null || vp.getHeureRetourAeroport().after(stat.heureRetour)) {
                    stat.heureRetour = vp.getHeureRetourAeroport();
                }
            }
        }

        return stats;
    }

    /**
     * Structure auxiliaire pour stocker les statistiques des véhicules.
     * Utilisée uniquement par sauvegarderSuiviVehicules().
     */
    private static class VehiculeStats {
        int nombreGroupes = 0;
        int totalPassagers = 0;
        double totalDistance = 0.0;
        Time heureDepart = null;
        Time heureRetour = null;
    }

    /**
     * Archiver la planification actuelle pour une date (la rendre inactive).
     * Avant de générer une nouvelle planification, archiver l'ancienne.
     */
    public void archiverPlanification(Date date) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.getTransaction().begin();

            TypedQuery<Planification> query = em.createQuery(
                "SELECT p FROM Planification p WHERE p.datePlanification = :date AND p.statut = 'ACTIVE'",
                Planification.class);
            query.setParameter("date", date);
            List<Planification> result = query.getResultList();

            for (Planification planif : result) {
                planif.setStatut("ARCHIVED");
                em.merge(planif);
            }

            em.getTransaction().commit();
        }
    }
}
