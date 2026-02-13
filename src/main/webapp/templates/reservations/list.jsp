<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.backoffice.models.Reservation" %>
<html>
<head>
    <title>Liste des réservations</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        h1 { color: #333; }
        .container { max-width: 1200px; margin: 0 auto; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        tr:hover { background-color: #f1f1f1; }
        .btn { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; margin-bottom: 15px; }
        .btn:hover { background-color: #45a049; }
        .btn-edit { background-color: #2196F3; padding: 6px 12px; font-size: 12px; }
        .btn-edit:hover { background-color: #1976D2; }
        .btn-delete { background-color: #f44336; padding: 6px 12px; font-size: 12px; }
        .btn-delete:hover { background-color: #d32f2f; }
        .message { color: green; font-weight: bold; margin-bottom: 10px; padding: 10px; background: #e8f5e9; border-radius: 4px; }
        .filter-form { background: white; padding: 20px; border-radius: 4px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .filter-form h3 { margin-top: 0; color: #333; }
        .filter-row { display: flex; gap: 15px; align-items: center; flex-wrap: wrap; }
        .filter-group { display: flex; flex-direction: column; }
        .filter-group label { font-weight: bold; margin-bottom: 5px; color: #666; font-size: 12px; }
        .filter-form input[type="date"] { padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
        .filter-form button { padding: 10px 20px; background-color: #2196F3; color: white; border: none; border-radius: 4px; cursor: pointer; height: 40px; }
        .filter-form button:hover { background-color: #1976D2; }
        .filter-form .btn-reset { background-color: #9e9e9e; text-decoration: none; display: inline-flex; align-items: center; }
        .filter-form .btn-reset:hover { background-color: #757575; }
        .actions { display: flex; gap: 5px; }
        .stats { background: white; padding: 15px; border-radius: 4px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .stats span { font-weight: bold; color: #4CAF50; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Liste des réservations</h1>

        <% if (request.getAttribute("message") != null) { %>
            <p class="message"><%= request.getAttribute("message") %></p>
        <% } %>

        <div class="filter-form">
            <h3>Filtrer les réservations</h3>
            <form action="${pageContext.request.contextPath}/reservations" method="GET">
                <div class="filter-row">
                    <div class="filter-group">
                        <label for="dateFiltre">Date exacte</label>
                        <input type="date" id="dateFiltre" name="dateFiltre" 
                               value="<%= request.getAttribute("dateFiltre") != null ? request.getAttribute("dateFiltre") : "" %>"/>
                    </div>
                    <div class="filter-group">
                        <label>ou</label>
                    </div>
                    <div class="filter-group">
                        <label for="dateDebut">Date début</label>
                        <input type="date" id="dateDebut" name="dateDebut" 
                               value="<%= request.getAttribute("dateDebut") != null ? request.getAttribute("dateDebut") : "" %>"/>
                    </div>
                    <div class="filter-group">
                        <label for="dateFin">Date fin</label>
                        <input type="date" id="dateFin" name="dateFin" 
                               value="<%= request.getAttribute("dateFin") != null ? request.getAttribute("dateFin") : "" %>"/>
                    </div>
                    <button type="submit">Filtrer</button>
                    <a href="${pageContext.request.contextPath}/reservations" class="btn-reset">Réinitialiser</a>
                </div>
            </form>
        </div>

        <%
            List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
            Map<Integer, String> hotelMap = (Map<Integer, String>) request.getAttribute("hotelMap");
        %>
        
        <div class="stats">
            Total: <span><%= reservations != null ? reservations.size() : 0 %></span> réservation(s)
        </div>

        <a href="${pageContext.request.contextPath}/reservations/new" class="btn">+ Nouvelle réservation</a>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Référence</th>
                    <th>Nombre</th>
                    <th>Date</th>
                    <th>Heure</th>
                    <th>Hôtel</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (reservations != null) {
                        for (Reservation r : reservations) {
                            String hotelName = hotelMap != null && hotelMap.get(r.getHotel()) != null 
                                ? hotelMap.get(r.getHotel()) 
                                : "Hôtel #" + r.getHotel();
                %>
                <tr>
                    <td><%= r.getId() %></td>
                    <td><%= r.getReference() %></td>
                    <td><%= r.getNombre() %></td>
                    <td><%= r.getDate() %></td>
                    <td><%= r.getHeure() %></td>
                    <td><%= hotelName %></td>
                    <td class="actions">
                        <a href="${pageContext.request.contextPath}/reservations/edit?id=<%= r.getId() %>" class="btn btn-edit">Modifier</a>
                        <a href="${pageContext.request.contextPath}/reservations/delete?id=<%= r.getId() %>" class="btn btn-delete" onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette réservation ?');">Supprimer</a>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
            </tbody>
        </table>

        <br>
        <a href="${pageContext.request.contextPath}/">Retour à l'accueil</a>
    </div>
</body>
</html>
