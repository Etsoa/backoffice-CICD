<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.backoffice.models.Vehicule" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Véhicules non assignés</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body { font-family: Arial, Helvetica, sans-serif; background:#f8fafc; padding:24px; }
        .card { background:#fff; border-radius:8px; padding:20px; border:1px solid #e2e8f0; }
        .btn { display:inline-block; padding:8px 14px; border-radius:6px; text-decoration:none; color:#fff; background:#1a73e8; }
        table { width:100%; border-collapse:collapse; margin-top:16px; }
        th,td { padding:10px; border-bottom:1px solid #eef2f7; text-align:left; }
        th { background:#f1f5f9; color:#475569; font-weight:700; }
    </style>
</head>
<body>
    <div class="card">
        <h2>Véhicules non assignés</h2>
        <p>Liste des véhicules non utilisés pour la date : <strong>${dateSelectionnee}</strong></p>

        <a href="${pageContext.request.contextPath}/planification?date=${dateSelectionnee}" class="btn"><i class="fas fa-arrow-left"></i> Retour au planning</a>

        <%
            @SuppressWarnings("unchecked")
            List<Vehicule> vehicules = (List<Vehicule>) request.getAttribute("vehiculesLibres");
        %>

        <% if (vehicules != null && !vehicules.isEmpty()) { %>
            <table>
                <thead>
                    <tr>
                        <th>Référence</th>
                        <th>Places</th>
                        <th>Carburant</th>
                        <th>Vitesse (km/h)</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Vehicule v : vehicules) { %>
                    <tr>
                        <td><%= v.getReference() %></td>
                        <td><%= v.getPlace() %></td>
                        <td><%= v.getTypeCarburant() != null ? v.getTypeCarburant().getLibelle() : "-" %></td>
                        <td><%= String.format("%.0f", v.getVitesseMoyenne()) %></td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        <% } else { %>
            <p>Aucun véhicule non assigné trouvé pour cette date.</p>
        <% } %>
    </div>
</body>
</html>
