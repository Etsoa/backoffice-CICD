<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.backoffice.dto.VehiculePlanningDTO" %>
<%@ page import="com.backoffice.dto.ReservationPlanningDTO" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Planification des Véhicules</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            overflow: hidden;
        }
        
        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        
        .header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        
        .date-selector {
            background: #f8f9fa;
            padding: 20px 30px;
            border-bottom: 2px solid #e9ecef;
        }
        
        .date-selector form {
            display: flex;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
        }
        
        .date-selector label {
            font-weight: 600;
            color: #495057;
        }
        
        .date-selector input[type="date"] {
            padding: 10px 15px;
            border: 2px solid #dee2e6;
            border-radius: 8px;
            font-size: 1em;
            transition: all 0.3s;
        }
        
        .date-selector input[type="date"]:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .date-selector button {
            padding: 10px 25px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 1em;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        
        .date-selector button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .content {
            padding: 30px;
        }
        
        .error {
            background: #f8d7da;
            color: #721c24;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #f5c6cb;
        }
        
        .info {
            background: #d1ecf1;
            color: #0c5460;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #bee5eb;
        }
        
        .vehicule-card {
            background: #fff;
            border: 2px solid #e9ecef;
            border-radius: 12px;
            margin-bottom: 25px;
            overflow: hidden;
            transition: all 0.3s;
        }
        
        .vehicule-card:hover {
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            transform: translateY(-3px);
        }
        
        .vehicule-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .vehicule-info {
            display: flex;
            gap: 30px;
            align-items: center;
        }
        
        .vehicule-ref {
            font-size: 1.5em;
            font-weight: bold;
        }
        
        .vehicule-badge {
            display: inline-block;
            padding: 5px 12px;
            background: rgba(255,255,255,0.2);
            border-radius: 20px;
            font-size: 0.9em;
        }
        
        .reservations-list {
            padding: 0;
        }
        
        .reservation-item {
            padding: 20px;
            border-bottom: 1px solid #e9ecef;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
            transition: background 0.2s;
        }
        
        .reservation-item:last-child {
            border-bottom: none;
        }
        
        .reservation-item:hover {
            background: #f8f9fa;
        }
        
        .reservation-field {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }
        
        .reservation-label {
            font-size: 0.85em;
            color: #6c757d;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .reservation-value {
            font-size: 1.1em;
            color: #212529;
            font-weight: 500;
        }
        
        .time-badge {
            display: inline-block;
            padding: 5px 10px;
            background: #e7f3ff;
            color: #004085;
            border-radius: 5px;
            font-weight: 600;
        }
        
        .retour-info {
            background: #d4edda;
            padding: 15px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .retour-label {
            font-weight: 600;
            color: #155724;
        }
        
        .retour-time {
            font-size: 1.2em;
            font-weight: bold;
            color: #155724;
        }
        
        .no-planning {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .no-planning svg {
            width: 100px;
            height: 100px;
            margin-bottom: 20px;
            opacity: 0.5;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🚗 Planification des Véhicules</h1>
            <p>Gestion optimale des transferts aéroport</p>
        </div>
        
        <div class="date-selector">
            <form method="GET" action="${pageContext.request.contextPath}/planification">
                <label for="date">Sélectionner une date :</label>
                <input type="date" id="date" name="date" 
                       value="${dateSelectionnee != null ? dateSelectionnee : ''}" 
                       required>
                <button type="submit">📅 Afficher le Planning</button>
            </form>
        </div>
        
        <div class="content">
            <% if (request.getAttribute("error") != null) { %>
                <div class="error">
                    ⚠️ <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <% 
            @SuppressWarnings("unchecked")
            List<VehiculePlanningDTO> planning = (List<VehiculePlanningDTO>) request.getAttribute("planning");
            
            if (planning != null && !planning.isEmpty()) { 
            %>
                <div class="info">
                    📊 Planning pour le <strong>${dateSelectionnee}</strong> - 
                    <%= planning.size() %> véhicule(s) utilisé(s)
                </div>
                
                <% for (VehiculePlanningDTO vehiculePlanning : planning) { %>
                    <div class="vehicule-card">
                        <div class="vehicule-header">
                            <div class="vehicule-info">
                                <span class="vehicule-ref">
                                    <%= vehiculePlanning.getVehicule().getReference() %>
                                </span>
                                <span class="vehicule-badge">
                                    <%= vehiculePlanning.getVehicule().getPlace() %> places
                                </span>
                                <span class="vehicule-badge">
                                    <%= vehiculePlanning.getVehicule().getTypeCarburant().getLibelle() %>
                                </span>
                                <span class="vehicule-badge">
                                    <%= String.format("%.0f", vehiculePlanning.getVehicule().getVitesseMoyenne()) %> km/h
                                </span>
                            </div>
                            <div>
                                <%= vehiculePlanning.getReservations().size() %> réservation(s)
                            </div>
                        </div>
                        
                        <div class="reservations-list">
                            <% for (ReservationPlanningDTO resPlanning : vehiculePlanning.getReservations()) { %>
                                <div class="reservation-item">
                                    <div class="reservation-field">
                                        <span class="reservation-label">Référence</span>
                                        <span class="reservation-value">
                                            #<%= resPlanning.getReservation().getReference() %>
                                        </span>
                                    </div>
                                    
                                    <div class="reservation-field">
                                        <span class="reservation-label">Hôtel</span>
                                        <span class="reservation-value">
                                            <%= resPlanning.getHotelLibelle() %>
                                        </span>
                                    </div>
                                    
                                    <div class="reservation-field">
                                        <span class="reservation-label">Personnes</span>
                                        <span class="reservation-value">
                                            <%= resPlanning.getReservation().getNombre() %> 
                                            <%= resPlanning.getReservation().getNombre() > 1 ? "personnes" : "personne" %>
                                        </span>
                                    </div>
                                    
                                    <div class="reservation-field">
                                        <span class="reservation-label">Heure RDV</span>
                                        <span class="reservation-value">
                                            <span class="time-badge">
                                                <%= resPlanning.getReservation().getHeure() %>
                                            </span>
                                        </span>
                                    </div>
                                    
                                    <div class="reservation-field">
                                        <span class="reservation-label">Départ Aéroport</span>
                                        <span class="reservation-value">
                                            <span class="time-badge">
                                                <%= resPlanning.getHeureDepart() %>
                                            </span>
                                        </span>
                                    </div>
                                    
                                    <div class="reservation-field">
                                        <span class="reservation-label">Retour prévu</span>
                                        <span class="reservation-value">
                                            <span class="time-badge">
                                                <%= resPlanning.getHeureRetour() %>
                                            </span>
                                        </span>
                                    </div>
                                    
                                    <div class="reservation-field">
                                        <span class="reservation-label">Distance</span>
                                        <span class="reservation-value">
                                            <%= String.format("%.1f", resPlanning.getDistanceKm()) %> km
                                        </span>
                                    </div>
                                    
                                    <div class="reservation-field">
                                        <span class="reservation-label">Temps d'attente</span>
                                        <span class="reservation-value">
                                            <%= resPlanning.getTempsAttenteMin() %> min
                                        </span>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                        
                        <div class="retour-info">
                            <span class="retour-label">
                                ✅ Retour définitif à l'aéroport :
                            </span>
                            <span class="retour-time">
                                <%= vehiculePlanning.getHeureRetourAeroport() %>
                            </span>
                        </div>
                    </div>
                <% } %>
                
            <% } else if (request.getAttribute("dateSelectionnee") != null) { %>
                <div class="no-planning">
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                              d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
                    </svg>
                    <h2>Aucune réservation</h2>
                    <p>Aucune réservation trouvée pour la date sélectionnée.</p>
                </div>
            <% } else { %>
                <div class="no-planning">
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                              d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                    </svg>
                    <h2>Sélectionnez une date</h2>
                    <p>Veuillez sélectionner une date pour afficher le planning des véhicules.</p>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
