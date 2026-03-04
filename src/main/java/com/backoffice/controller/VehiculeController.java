package com.backoffice.controller;

import java.util.HashMap;
import java.util.List;

import com.backoffice.models.Vehicule;
import com.backoffice.models.Vehicule.TypeCarburant;
import com.backoffice.util.JPAUtil;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.model.JsonResponse;
import itu.framework.model.ModelView;
import jakarta.persistence.EntityManager;

@MyController(value = "Vehicule")
public class VehiculeController {

    @MyURL(value = "/vehicules", method = "GET")
    public ModelView listVehicules(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/list.jsp");

        try {
            List<Vehicule> vehicules;

            // Filtre par type de carburant
            if (params != null && params.get("typeCarburant") != null
                    && !params.get("typeCarburant").toString().isEmpty()) {
                String type = params.get("typeCarburant").toString();
                vehicules = em.createQuery(
                        "SELECT v FROM Vehicule v WHERE v.typeCarburant = :type ORDER BY v.reference",
                        Vehicule.class)
                        .setParameter("type", TypeCarburant.valueOf(type))
                        .getResultList();
                mv.addItem("typeCarburantFiltre", type);
            } else {
                vehicules = em.createQuery("SELECT v FROM Vehicule v ORDER BY v.reference", Vehicule.class)
                        .getResultList();
            }

            mv.addItem("vehicules", vehicules);
            mv.addItem("typesCarburant", TypeCarburant.values());
        } finally {
            em.close();
        }

        return mv;
    }

    @MyURL(value = "/vehicules/new", method = "GET")
    public ModelView showCreateForm() {
        ModelView mv = new ModelView("vehicules/form.jsp");
        mv.addItem("typesCarburant", TypeCarburant.values());
        return mv;
    }

    @MyURL(value = "/vehicules/edit", method = "GET")
    public ModelView showEditForm(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/form.jsp");
        try {
            Integer id = Integer.parseInt(params.get("id").toString());
            Vehicule vehicule = em.find(Vehicule.class, id);
            mv.addItem("vehicule", vehicule);
            mv.addItem("typesCarburant", TypeCarburant.values());
            mv.addItem("editMode", true);
        } finally {
            em.close();
        }
        return mv;
    }

    @MyURL(value = "/vehicules", method = "POST")
    public ModelView createVehicule(Vehicule vehicule) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/list.jsp");

        try {
            em.getTransaction().begin();
            em.persist(vehicule);
            em.getTransaction().commit();
            mv.addItem("message", "Véhicule créé avec succès");
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }

        return reloadList(mv);
    }

    @MyURL(value = "/vehicules/update", method = "POST")
    public ModelView updateVehicule(Vehicule vehicule) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/list.jsp");

        try {
            em.getTransaction().begin();
            em.merge(vehicule);
            em.getTransaction().commit();
            mv.addItem("message", "Véhicule mis à jour avec succès");
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }

        return reloadList(mv);
    }

    @MyURL(value = "/vehicules/delete", method = "GET")
    public ModelView deleteVehicule(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/list.jsp");

        try {
            Integer id = Integer.parseInt(params.get("id").toString());
            em.getTransaction().begin();
            Vehicule vehicule = em.find(Vehicule.class, id);
            if (vehicule != null) {
                em.remove(vehicule);
                mv.addItem("message", "Véhicule supprimé avec succès");
            } else {
                mv.addItem("message", "Véhicule non trouvé");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            mv.addItem("message", "Erreur: " + e.getMessage());
        } finally {
            em.close();
        }

        return reloadList(mv);
    }

    private ModelView reloadList(ModelView mv) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Vehicule> vehicules = em.createQuery("SELECT v FROM Vehicule v ORDER BY v.reference", Vehicule.class)
                    .getResultList();
            mv.addItem("vehicules", vehicules);
            mv.addItem("typesCarburant", TypeCarburant.values());
        } finally {
            em.close();
        }
        return mv;
    }

    // API JSON
    @MyURL(value = "/api/vehicules", method = "GET")
    public JsonResponse apiListVehicules() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<Vehicule> vehicules = em.createQuery("SELECT v FROM Vehicule v ORDER BY v.reference", Vehicule.class)
                    .getResultList();
            return JsonResponse.success(vehicules, "Liste des véhicules");
        } catch (Exception e) {
            return JsonResponse.error(500, e.getMessage());
        } finally {
            em.close();
        }
    }
}
