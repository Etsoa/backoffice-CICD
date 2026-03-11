package com.backoffice.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import com.backoffice.models.Reservation;
import com.backoffice.models.Vehicule;

import com.backoffice.dto.RegroupementDTO;
import com.backoffice.dto.VehiculePlanningDTO;
import com.backoffice.service.PlanificationService;

import itu.framework.annotations.MyController;
import itu.framework.annotations.MyURL;
import itu.framework.model.ModelView;

@MyController(value = "Planification")
public class PlanificationController {

    private PlanificationService planificationService = new PlanificationService();

    @MyURL(value = "/planification", method = "GET")
    public ModelView afficherPlanning(HashMap<String, Object> params) {
        ModelView mv = new ModelView("planification/index.jsp");
        
        if (params != null && params.get("date") != null && !params.get("date").toString().isEmpty()) {
            String dateParam = params.get("date").toString();
            try {
                Date date = Date.valueOf(dateParam);
                List<RegroupementDTO> regroupements = planificationService.genererRegroupements(date);
                List<Reservation> nonAssignees = planificationService.getReservationsNonAssignees(date);
                
                // Compter le nombre total de véhicules utilisés
                int totalVehicules = 0;
                for (RegroupementDTO g : regroupements) {
                    totalVehicules += g.getVehiculesAssignes().size();
                }
                
                mv.addItem("regroupements", regroupements);
                mv.addItem("totalVehicules", totalVehicules);
                mv.addItem("dateSelectionnee", dateParam);
                mv.addItem("reservationsNonAssignees", nonAssignees);
                mv.addItem("delaiAttente", planificationService.getDelaiAttente());
            } catch (IllegalArgumentException e) {
                mv.addItem("error", "Format de date invalide. Utilisez le format YYYY-MM-DD");
            }
        }
        
        return mv;
    }

    @MyURL(value = "/planification/vehicules-non-assignes", method = "GET")
    public ModelView afficherVehiculesNonAssignes(HashMap<String, Object> params) {
        ModelView mv = new ModelView("planification/vehicules_non_assignes.jsp");

        if (params != null && params.get("date") != null && !params.get("date").toString().isEmpty()) {
            String dateParam = params.get("date").toString();
            try {
                Date date = Date.valueOf(dateParam);
                List<Vehicule> libres = planificationService.getVehiculesNonAssignes(date);

                mv.addItem("vehiculesLibres", libres);
                mv.addItem("dateSelectionnee", dateParam);
            } catch (IllegalArgumentException e) {
                mv.addItem("error", "Format de date invalide. Utilisez le format YYYY-MM-DD");
            }
        }

        return mv;
    }
    
    @MyURL(value = "/planification/reservations-non-assignees", method = "GET")
    public ModelView afficherReservationsNonAssignees(HashMap<String, Object> params) {
        ModelView mv = new ModelView("planification/reservations_non_assignees.jsp");

        if (params != null && params.get("date") != null && !params.get("date").toString().isEmpty()) {
            String dateParam = params.get("date").toString();
            try {
                Date date = Date.valueOf(dateParam);
                List<Reservation> nonAssignees = planificationService.getReservationsNonAssignees(date);
                List<RegroupementDTO> regroupements = planificationService.genererRegroupements(date);

                mv.addItem("reservationsNonAssignees", nonAssignees);
                mv.addItem("regroupements", regroupements);
                mv.addItem("dateSelectionnee", dateParam);
            } catch (IllegalArgumentException e) {
                mv.addItem("error", "Format de date invalide. Utilisez le format YYYY-MM-DD");
            }
        }

        return mv;
    }
}
