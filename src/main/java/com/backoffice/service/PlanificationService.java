package com.backoffice.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.backoffice.dto.ReservationPlanningDTO;
import com.backoffice.dto.VehiculePlanningDTO;
import com.backoffice.models.Hotel;
import com.backoffice.models.Parametre;
import com.backoffice.models.Reservation;
import com.backoffice.models.Vehicule;
import com.backoffice.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class PlanificationService {

    private static final String CODE_AEROPORT = "TNR";

    // =========================================================================
    // Méthodes d'accès aux données
    // =========================================================================

    /**
     * Récupérer le délai d'attente depuis la table parametre
     */
    public int getDelaiAttente() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Parametre> query = em.createQuery(
                "SELECT p FROM Parametre p WHERE p.cle = :cle", Parametre.class);
            query.setParameter("cle", "delai_attente");
            List<Parametre> result = query.getResultList();
            return result.isEmpty() ? 30 : result.get(0).getValeurInt();
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

    private int getPrioriteCarburant(Vehicule v) {
        String code = v.getTypeCarburant() != null ? v.getTypeCarburant().getCode() : "";
        switch (code) {
            case "D":  return 1;
            case "H":  return 2;
            case "Es": return 3;
            case "El": return 4;
            default:   return 5;
        }
    }

    /**
     * Générer le planning complet pour une date donnée
     */
    public List<VehiculePlanningDTO> genererPlanning(Date date) {
        List<Reservation> reservations = getReservationsByDate(date);
        if (reservations.isEmpty()) return new ArrayList<>();

        List<Vehicule> tousVehicules = getAllVehicules();
        int delaiAttente = getDelaiAttente();
        Integer aeroportId = getAeroportId();

        // Trier par heure ASC, puis nombre de personnes DESC (gros groupes d'abord dans même créneau)
        reservations.sort((r1, r2) -> {
            int cmp = r1.getHeure().compareTo(r2.getHeure());
            if (cmp != 0) return cmp;
            return Integer.compare(r2.getNombre(), r1.getNombre());
        });

        List<VehiculePlanningDTO> planning = new ArrayList<>();
        boolean[] assignees = new boolean[reservations.size()];

        for (int i = 0; i < reservations.size(); i++) {
            if (assignees[i]) continue;

            Reservation resaPrincipale = reservations.get(i);

            // Fenêtre temporelle : heure_premiere_resa + delai_attente
            Time limiteHeure = ajouterMinutes(resaPrincipale.getHeure(), delaiAttente);

            // Collecter les autres réservations dans cette fenêtre (pas encore assignées)
            List<Integer> autresIndices = new ArrayList<>();
            for (int j = i + 1; j < reservations.size(); j++) {
                if (!assignees[j] && reservations.get(j).getHeure().compareTo(limiteHeure) <= 0) {
                    autresIndices.add(j);
                }
            }

            // Chercher le meilleur véhicule pour la résa principale
            List<Vehicule> vehiculesUtilises = getVehiculesUtilises(planning);
            List<Vehicule> disponibles = new ArrayList<>();
            for (Vehicule v : tousVehicules) {
                if (!isVehiculeUtilise(vehiculesUtilises, v)) {
                    disponibles.add(v);
                }
            }

            Vehicule meilleur = trouverMeilleurVehicule(resaPrincipale.getNombre(), disponibles);
            if (meilleur == null) {
                meilleur = trouverMeilleurVehicule(resaPrincipale.getNombre(), tousVehicules);
            }
            if (meilleur == null) continue;

            VehiculePlanningDTO vehiculePlanning = new VehiculePlanningDTO(meilleur);
            planning.add(vehiculePlanning);
            assignees[i] = true;
            ajouterReservationAuPlanning(vehiculePlanning, resaPrincipale, aeroportId, delaiAttente);

            // Si le véhicule a un SURPLUS de places, essayer d'y ajouter d'autres résas de la fenêtre
            int placesRestantes = meilleur.getPlace() - resaPrincipale.getNombre();
            if (placesRestantes > 0 && !autresIndices.isEmpty()) {
                // Trier par nombre de personnes DESC pour remplir au mieux
                autresIndices.sort((a, b) -> Integer.compare(reservations.get(b).getNombre(), reservations.get(a).getNombre()));

                for (int idx : autresIndices) {
                    if (assignees[idx]) continue;
                    Reservation resa = reservations.get(idx);
                    if (resa.getNombre() <= placesRestantes) {
                        assignees[idx] = true;
                        ajouterReservationAuPlanning(vehiculePlanning, resa, aeroportId, delaiAttente);
                        placesRestantes -= resa.getNombre();
                    }
                }
            }
        }

        // Calculer les itinéraires (ordre par proximité, distances, heures)
        for (VehiculePlanningDTO vp : planning) {
            calculerItineraire(vp, aeroportId, delaiAttente);
        }

        return planning;
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
     * Trouver un véhicule déjà planifié qui a assez de places restantes
     * dans la même fenêtre temporelle
     */
    private VehiculePlanningDTO trouverVehiculePlanningAvecPlace(
            List<VehiculePlanningDTO> planning, int nombrePersonnes,
            Time heurePremierResa, int delaiAttente) {

        Time limiteDepart = ajouterMinutes(heurePremierResa, delaiAttente);

        for (VehiculePlanningDTO vp : planning) {
            // Le véhicule doit être dans la même fenêtre temporelle
            if (vp.getReservations().isEmpty()) continue;
            Time heurePremiereDansVP = vp.getReservations().get(0).getReservation().getHeure();
            Time limiteDepartVP = ajouterMinutes(heurePremiereDansVP, delaiAttente);

            // Les fenêtres doivent se chevaucher
            if (heurePremierResa.after(limiteDepartVP)) continue;

            // Vérifier la capacité restante
            int placesOccupees = 0;
            for (ReservationPlanningDTO rp : vp.getReservations()) {
                placesOccupees += rp.getReservation().getNombre();
            }
            int placesRestantes = vp.getVehicule().getPlace() - placesOccupees;
            if (placesRestantes >= nombrePersonnes) {
                return vp;
            }
        }
        return null;
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
     */
    private void calculerItineraire(VehiculePlanningDTO vp, Integer aeroportId, int delaiAttente) {
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
                }
            }
            if (plusProche == null) break;

            ordreDepot.add(plusProche);
            posId = plusProche.getLieuHotelId();
            restants.remove(plusProche);
        }

        vp.setReservations(ordreDepot);

        // Heure RDV la plus tôt
        Time premierRdv = ordreDepot.get(0).getReservation().getHeure();
        for (ReservationPlanningDTO rp : ordreDepot) {
            if (rp.getReservation().getHeure().before(premierRdv)) {
                premierRdv = rp.getReservation().getHeure();
            }
        }

        // Heure de départ du véhicule = heure_premier_rdv - délai_attente
        Time heureDepart = ajouterMinutes(premierRdv, -delaiAttente);
        vp.setHeureDepart(heureDepart);

        // Parcourir l'itinéraire
        double distanceTotale = 0.0;
        posId = aeroportId;
        Time heureCourante = heureDepart;

        for (ReservationPlanningDTO rp : ordreDepot) {
            double distSegment = getDistance(posId, rp.getLieuHotelId());
            int tempsSegmentMin = calculerTempsTrajetMinutes(distSegment, vitesse);
            distanceTotale += distSegment;

            rp.setHeureDepart(heureCourante);
            Time heureArrivee = ajouterMinutes(heureCourante, tempsSegmentMin);
            rp.setHeureRetour(heureArrivee);

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
}
