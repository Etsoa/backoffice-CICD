package com.backoffice.controller;

import java.util.HashMap;
import java.util.List;

import com.backoffice.models.Hotel;
import com.backoffice.util.JPAUtil;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.model.ModelView;
import jakarta.persistence.EntityManager;

@MyController(value = "Hotel")
public class HotelController {

    @MyURL(value = "/hotels", method = "GET")
    public ModelView listHotels() {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("hotels/list.jsp");
        try {
            List<Hotel> hotels = em.createQuery("SELECT h FROM Hotel h ORDER BY h.libelle", Hotel.class).getResultList();
            mv.addItem("hotels", hotels);
        } finally {
            em.close();
        }
        return mv;
    }

    @MyURL(value = "/hotels/new", method = "GET")
    public ModelView showCreateForm() {
        return new ModelView("hotels/form.jsp");
    }

    @MyURL(value = "/hotels/edit", method = "GET")
    public ModelView showEditForm(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("hotels/form.jsp");
        try {
            Integer id = Integer.parseInt(params.get("id").toString());
            Hotel hotel = em.find(Hotel.class, id);
            mv.addItem("hotel", hotel);
            mv.addItem("editMode", true);
        } finally {
            em.close();
        }
        return mv;
    }

    @MyURL(value = "/hotels", method = "POST")
    public ModelView createHotel(Hotel hotel) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("hotels/list.jsp");
        try {
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
            mv.addItem("message", "Hotel cree avec succes");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }
        return reloadList(mv);
    }

    @MyURL(value = "/hotels/update", method = "POST")
    public ModelView updateHotel(Hotel hotel) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("hotels/list.jsp");
        try {
            em.getTransaction().begin();
            em.merge(hotel);
            em.getTransaction().commit();
            mv.addItem("message", "Hotel mis a jour avec succes");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }
        return reloadList(mv);
    }

    @MyURL(value = "/hotels/delete", method = "GET")
    public ModelView deleteHotel(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("hotels/list.jsp");
        try {
            Integer id = Integer.parseInt(params.get("id").toString());
            em.getTransaction().begin();
            Hotel hotel = em.find(Hotel.class, id);
            if (hotel != null) {
                em.remove(hotel);
                mv.addItem("message", "Hotel supprime avec succes");
            } else {
                mv.addItem("message", "Hotel non trouve");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }
        return reloadList(mv);
    }

    private ModelView reloadList(ModelView mv) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Hotel> hotels = em.createQuery("SELECT h FROM Hotel h ORDER BY h.libelle", Hotel.class).getResultList();
            mv.addItem("hotels", hotels);
        } finally {
            em.close();
        }
        return mv;
    }
}
