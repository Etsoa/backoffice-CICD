package com.backoffice.controller;

import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.backoffice.dto.ReservationDTO;
import com.backoffice.models.Client;
import com.backoffice.models.Hotel;
import com.backoffice.models.Reservation;
import com.backoffice.util.JPAUtil;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.model.JsonResponse;
import itu.framework.model.ModelView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@MyController(value = "Reservation")
public class ReservationController {

    @MyURL(value = "/reservations", method = "GET")
    public ModelView listReservations(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("reservations/list.jsp");

        try {
            List<Reservation> reservations;
            String dateFiltre = null;
            String dateDebut = null;
            String dateFin = null;

            // Filtre par plage de dates
            if (params != null && params.get("dateDebut") != null && !params.get("dateDebut").toString().isEmpty()
                    && params.get("dateFin") != null && !params.get("dateFin").toString().isEmpty()) {
                dateDebut = params.get("dateDebut").toString();
                dateFin = params.get("dateFin").toString();
                TypedQuery<Reservation> query = em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.date BETWEEN :dateDebut AND :dateFin ORDER BY r.date ASC, r.heure ASC",
                        Reservation.class);
                query.setParameter("dateDebut", java.sql.Date.valueOf(dateDebut));
                query.setParameter("dateFin", java.sql.Date.valueOf(dateFin));
                reservations = query.getResultList();
                mv.addItem("dateDebut", dateDebut);
                mv.addItem("dateFin", dateFin);
            }
            // Filtre par date unique
            else if (params != null && params.get("dateFiltre") != null
                    && !params.get("dateFiltre").toString().isEmpty()) {
                dateFiltre = params.get("dateFiltre").toString();
                TypedQuery<Reservation> query = em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.date = :dateFiltre ORDER BY r.heure ASC",
                        Reservation.class);
                query.setParameter("dateFiltre", java.sql.Date.valueOf(dateFiltre));
                reservations = query.getResultList();
                mv.addItem("dateFiltre", dateFiltre);
            } else {
                reservations = em
                        .createQuery("SELECT r FROM Reservation r ORDER BY r.date DESC, r.heure ASC", Reservation.class)
                        .getResultList();
            }

            // Charger les hôtels pour afficher les noms
            List<Hotel> hotels = em.createQuery("SELECT h FROM Hotel h", Hotel.class).getResultList();
            Map<Integer, String> hotelMap = hotels.stream()
                    .collect(Collectors.toMap(Hotel::getId, Hotel::getLibelle));

            mv.addItem("reservations", reservations);
            mv.addItem("hotelMap", hotelMap);
        } finally {
            em.close();
        }

        return mv;
    }

    @MyURL(value = "/reservations/new", method = "GET")
    public ModelView showCreateForm() {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("reservations/form.jsp");
        try {
            List<Hotel> hotels = em.createQuery("SELECT h FROM Hotel h ORDER BY h.libelle", Hotel.class)
                    .getResultList();
            List<Client> clients = em.createQuery("SELECT c FROM Client c ORDER BY c.nom, c.prenom", Client.class)
                    .getResultList();
            mv.addItem("hotels", hotels);
            mv.addItem("clients", clients);
        } finally {
            em.close();
        }
        return mv;
    }

    @MyURL(value = "/reservations/edit", method = "GET")
    public ModelView showEditForm(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("reservations/form.jsp");
        try {
            Integer id = Integer.parseInt(params.get("id").toString());
            Reservation reservation = em.find(Reservation.class, id);
            List<Hotel> hotels = em.createQuery("SELECT h FROM Hotel h ORDER BY h.libelle", Hotel.class)
                    .getResultList();
            List<Client> clients = em.createQuery("SELECT c FROM Client c ORDER BY c.nom, c.prenom", Client.class)
                    .getResultList();
            mv.addItem("reservation", reservation);
            mv.addItem("hotels", hotels);
            mv.addItem("clients", clients);
            mv.addItem("editMode", true);
        } finally {
            em.close();
        }
        return mv;
    }

    @MyURL(value = "/reservations", method = "POST")
    public ModelView createReservation(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("reservations/list.jsp");

        try {
            Reservation reservation = new Reservation();
            reservation.setReference(Integer.parseInt(params.get("reference").toString()));
            reservation.setNombre(Integer.parseInt(params.get("nombre").toString()));
            reservation.setDate(Date.valueOf(params.get("date").toString()));
            reservation.setHeure(Time.valueOf(params.get("heure").toString() + ":00"));
            reservation.setHotel(Integer.parseInt(params.get("hotel").toString()));
            if (params.get("client") != null && !params.get("client").toString().isEmpty()) {
                reservation.setClient(params.get("client").toString());
            }

            em.getTransaction().begin();
            em.persist(reservation);
            em.getTransaction().commit();
            mv.addItem("message", "Réservation créée avec succès");
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }

        // Recharger la liste avec les hôtels
        return reloadListWithHotels(mv);
    }

    @MyURL(value = "/reservations/update", method = "POST")
    public ModelView updateReservation(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("reservations/list.jsp");

        try {
            Integer id = Integer.parseInt(params.get("id").toString());
            Reservation reservation = em.find(Reservation.class, id);
            if (reservation != null) {
                reservation.setReference(Integer.parseInt(params.get("reference").toString()));
                reservation.setNombre(Integer.parseInt(params.get("nombre").toString()));
                reservation.setDate(Date.valueOf(params.get("date").toString()));
                reservation.setHeure(Time.valueOf(params.get("heure").toString() + ":00"));
                reservation.setHotel(Integer.parseInt(params.get("hotel").toString()));
                if (params.get("client") != null && !params.get("client").toString().isEmpty()) {
                    reservation.setClient(params.get("client").toString());
                }

                em.getTransaction().begin();
                em.merge(reservation);
                em.getTransaction().commit();
                mv.addItem("message", "Réservation mise à jour avec succès");
            } else {
                mv.addItem("message", "Réservation non trouvée");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }

        return reloadListWithHotels(mv);
    }

    @MyURL(value = "/reservations/delete", method = "GET")
    public ModelView deleteReservation(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("reservations/list.jsp");

        try {
            Integer id = Integer.parseInt(params.get("id").toString());
            em.getTransaction().begin();
            Reservation reservation = em.find(Reservation.class, id);
            if (reservation != null) {
                em.remove(reservation);
                mv.addItem("message", "Réservation supprimée avec succès");
            } else {
                mv.addItem("message", "Réservation non trouvée");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }

        return reloadListWithHotels(mv);
    }

    private ModelView reloadListWithHotels(ModelView mv) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Reservation> reservations = em
                    .createQuery("SELECT r FROM Reservation r ORDER BY r.date DESC, r.heure ASC", Reservation.class)
                    .getResultList();
            List<Hotel> hotels = em.createQuery("SELECT h FROM Hotel h", Hotel.class).getResultList();
            List<Client> clients = em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
            Map<Integer, String> hotelMap = hotels.stream()
                    .collect(Collectors.toMap(Hotel::getId, Hotel::getLibelle));
            Map<String, String> clientMap = clients.stream()
                    .collect(Collectors.toMap(Client::getIdClient, c -> c.getPrenom() + " " + c.getNom()));
            mv.addItem("reservations", reservations);
            mv.addItem("hotelMap", hotelMap);
            mv.addItem("clientMap", clientMap);
        } finally {
            em.close();
        }
        return mv;
    }

    @MyURL(value = "/api/reservations", method = "GET")
    public JsonResponse apiListReservations(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            List<Reservation> reservations;
            if (params != null && params.get("dateFiltre") != null && !params.get("dateFiltre").toString().isEmpty()) {
                String dateFiltre = params.get("dateFiltre").toString();
                TypedQuery<Reservation> query = em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.date = :dateFiltre",
                        Reservation.class);
                query.setParameter("dateFiltre", java.sql.Date.valueOf(dateFiltre));
                reservations = query.getResultList();
            } else {
                reservations = em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
            }

            // Convertir les Reservation en ReservationDTO pour une meilleure sérialisation
            // JSON
            List<ReservationDTO> reservationDTOs = reservations.stream()
                    .map(ReservationDTO::new)
                    .collect(Collectors.toList());

            return JsonResponse.success(reservationDTOs, "Liste des réservations");
        } catch (Exception e) {
            return JsonResponse.error(500, e.getMessage());
        } finally {
            em.close();
        }
    }
}
