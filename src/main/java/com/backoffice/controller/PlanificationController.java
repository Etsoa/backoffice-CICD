package com.backoffice.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import com.backoffice.dto.VehiculePlanningDTO;
import com.backoffice.service.PlanificationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/planification")
public class PlanificationController extends HttpServlet {

    private PlanificationService planificationService;

    @Override
    public void init() throws ServletException {
        super.init();
        planificationService = new PlanificationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String dateParam = request.getParameter("date");
        
        if (dateParam != null && !dateParam.isEmpty()) {
            try {
                Date date = Date.valueOf(dateParam);
                List<VehiculePlanningDTO> planning = planificationService.genererPlanning(date);
                
                request.setAttribute("planning", planning);
                request.setAttribute("dateSelectionnee", dateParam);
            } catch (IllegalArgumentException e) {
                request.setAttribute("error", "Format de date invalide. Utilisez le format YYYY-MM-DD");
            }
        }
        
        request.getRequestDispatcher("/templates/planification/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
