package java.com.backoffice.controller;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.model.ModelView;
import itu.framework.model.JsonResponse;
import java.com.backoffice.models.Reservation;
import java.util.HashMap;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

@MyController(value = "Reservation")
public class ReservationController {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("backoffice-pu");

    @MyURL(value = "/reservations", method = "GET")
    public ModelView listReservations(HashMap<String, Object> params) {
        EntityManager em = emf.createEntityManager();
        ModelView mv = new ModelView("reservations/list.jsp");

        try {
            List<Reservation> reservations;
            if (params != null && params.get("dateFiltre") != null && !params.get("dateFiltre").toString().isEmpty()) {
                String dateFiltre = params.get("dateFiltre").toString();
                TypedQuery<Reservation> query = em.createQuery(
                        "SELECT r FROM Reservation r WHERE CAST(r.dateHeureArrivee AS date) = CAST(:dateFiltre AS date)",
                        Reservation.class);
                query.setParameter("dateFiltre", java.sql.Date.valueOf(dateFiltre));
                reservations = query.getResultList();
                mv.addItem("dateFiltre", dateFiltre);
            } else {
                reservations = em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
            }
            mv.addItem("reservations", reservations);
        } finally {
            em.close();
        }

        return mv;
    }

    @MyURL(value = "/reservations/new", method = "GET")
    public ModelView showCreateForm() {
        return new ModelView("reservations/form.jsp");
    }

    @MyURL(value = "/reservations", method = "POST")
    public ModelView createReservation(Reservation reservation) {
        EntityManager em = emf.createEntityManager();
        ModelView mv = new ModelView("reservations/list.jsp");

        try {
            em.getTransaction().begin();
            em.persist(reservation);
            em.getTransaction().commit();
            mv.addItem("message", "Réservation créée avec succès");

            // Recharger la liste
            List<Reservation> reservations = em.createQuery("SELECT r FROM Reservation r", Reservation.class)
                    .getResultList();
            mv.addItem("reservations", reservations);
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }

        return mv;
    }

    @MyURL(value = "/api/reservations", method = "GET")
    public JsonResponse apiListReservations(HashMap<String, Object> params) {
        EntityManager em = emf.createEntityManager();

        try {
            List<Reservation> reservations;
            if (params != null && params.get("dateFiltre") != null && !params.get("dateFiltre").toString().isEmpty()) {
                String dateFiltre = params.get("dateFiltre").toString();
                TypedQuery<Reservation> query = em.createQuery(
                        "SELECT r FROM Reservation r WHERE CAST(r.dateHeureArrivee AS date) = CAST(:dateFiltre AS date)",
                        Reservation.class);
                query.setParameter("dateFiltre", java.sql.Date.valueOf(dateFiltre));
                reservations = query.getResultList();
            } else {
                reservations = em.createQuery("SELECT r FROM Reservation r", Reservation.class).getResultList();
            }
            return JsonResponse.success(reservations, "Liste des réservations");
        } catch (Exception e) {
            return JsonResponse.error(500, e.getMessage());
        } finally {
            em.close();
        }
    }
}
