package com.backoffice.controller;

import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;

import com.backoffice.models.TypeCarburant;
import com.backoffice.models.Vehicule;
import com.backoffice.service.PlanificationService;
import com.backoffice.util.JPAUtil;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.model.JsonResponse;
import itu.framework.model.ModelView;
import jakarta.persistence.EntityManager;

@MyController(value = "Vehicule")
public class VehiculeController {

    private PlanificationService planificationService = new PlanificationService();

    @MyURL(value = "/vehicules", method = "GET")
    public ModelView listVehicules(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/list.jsp");

        try {
            List<Vehicule> vehicules;            
            // Filtre par disponibilité (date + heure)
            boolean filtreDispoActif = false;
            if (params != null && params.get("dispoDate") != null && !params.get("dispoDate").toString().isEmpty()
                && params.get("dispoHeure") != null && !params.get("dispoHeure").toString().isEmpty()) {
                try {
                    Date date = Date.valueOf(params.get("dispoDate").toString());
                    String heureStr = params.get("dispoHeure").toString();
                    if (!heureStr.contains(":")) heureStr += ":00";
                    if (heureStr.split(":").length == 2) heureStr += ":00";
                    Time heure = Time.valueOf(heureStr);
                    
                    vehicules = planificationService.getVehiculesDisponibles(date, heure);
                    mv.addItem("dispoDate", params.get("dispoDate").toString());
                    mv.addItem("dispoHeure", params.get("dispoHeure").toString());
                    filtreDispoActif = true;
                } catch (Exception e) {
                    vehicules = em.createQuery("SELECT v FROM Vehicule v ORDER BY v.reference", Vehicule.class).getResultList();
                    mv.addItem("message", "Erreur: Format date/heure invalide");
                }
            }
            // Filtre par type de carburant
            else if (params != null && params.get("typeCarburant") != null && !params.get("typeCarburant").toString().isEmpty()) {
                Integer typeId = Integer.parseInt(params.get("typeCarburant").toString());
                vehicules = em.createQuery(
                        "SELECT v FROM Vehicule v WHERE v.typeCarburant.id = :typeId ORDER BY v.reference",
                        Vehicule.class)
                        .setParameter("typeId", typeId)
                        .getResultList();
                mv.addItem("typeCarburantFiltre", params.get("typeCarburant").toString());
            } else {
                vehicules = em.createQuery("SELECT v FROM Vehicule v ORDER BY v.reference", Vehicule.class)
                        .getResultList();
            }

            mv.addItem("vehicules", vehicules);
            mv.addItem("typesCarburant", getTypesCarburant(em));
            mv.addItem("filtreDispoActif", filtreDispoActif);
        } finally {
            em.close();
        }

        return mv;
    }

    @MyURL(value = "/vehicules/new", method = "GET")
    public ModelView showCreateForm() {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/form.jsp");
        try {
            mv.addItem("typesCarburant", getTypesCarburant(em));
        } finally {
            em.close();
        }
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
            mv.addItem("typesCarburant", getTypesCarburant(em));
            mv.addItem("editMode", true);
        } finally {
            em.close();
        }
        return mv;
    }

    @MyURL(value = "/vehicules", method = "POST")
    public ModelView createVehicule(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/list.jsp");

        try {
            Vehicule vehicule = new Vehicule();
            vehicule.setReference(params.get("reference").toString());
            vehicule.setPlace(Integer.parseInt(params.get("place").toString()));
            vehicule.setVitesseMoyenne(Double.parseDouble(params.get("vitesseMoyenne").toString()));

            Integer typeId = Integer.parseInt(params.get("typeCarburant").toString());
            TypeCarburant type = em.find(TypeCarburant.class, typeId);
            vehicule.setTypeCarburant(type);

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
    public ModelView updateVehicule(HashMap<String, Object> params) {
        EntityManager em = JPAUtil.getEntityManager();
        ModelView mv = new ModelView("vehicules/list.jsp");

        try {
            Integer id = Integer.parseInt(params.get("id").toString());
            Vehicule vehicule = em.find(Vehicule.class, id);

            vehicule.setReference(params.get("reference").toString());
            vehicule.setPlace(Integer.parseInt(params.get("place").toString()));
            vehicule.setVitesseMoyenne(Double.parseDouble(params.get("vitesseMoyenne").toString()));

            Integer typeId = Integer.parseInt(params.get("typeCarburant").toString());
            TypeCarburant type = em.find(TypeCarburant.class, typeId);
            vehicule.setTypeCarburant(type);

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
            mv.addItem("typesCarburant", getTypesCarburant(em));
        } finally {
            em.close();
        }
        return mv;
    }

    private List<TypeCarburant> getTypesCarburant(EntityManager em) {
        return em.createQuery("SELECT t FROM TypeCarburant t ORDER BY t.id", TypeCarburant.class).getResultList();
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
