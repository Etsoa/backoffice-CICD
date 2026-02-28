package com.backoffice.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

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
                List<VehiculePlanningDTO> planning = planificationService.genererPlanning(date);
                
                mv.addItem("planning", planning);
                mv.addItem("dateSelectionnee", dateParam);
            } catch (IllegalArgumentException e) {
                mv.addItem("error", "Format de date invalide. Utilisez le format YYYY-MM-DD");
            }
        }
        
        return mv;
    }
}
