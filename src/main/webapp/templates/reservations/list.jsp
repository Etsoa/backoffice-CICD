<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.backoffice.models.Reservation" %>
<html>
<head>
    <title>Liste des réservations</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .btn { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; margin-bottom: 15px; }
        .btn:hover { background-color: #45a049; }
        .message { color: green; font-weight: bold; margin-bottom: 10px; }
        .filter-form { background: #f9f9f9; padding: 15px; border-radius: 4px; margin-bottom: 20px; border: 1px solid #ddd; }
        .filter-form label { font-weight: bold; margin-right: 10px; }
        .filter-form input[type="date"] { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
        .filter-form button { padding: 8px 16px; background-color: #2196F3; color: white; border: none; border-radius: 4px; cursor: pointer; margin-left: 10px; }
        .filter-form button:hover { background-color: #1976D2; }
        .filter-form a { margin-left: 10px; color: #666; text-decoration: none; }
    </style>
</head>
<body>
    <h1>Liste des réservations</h1>

    <% if (request.getAttribute("message") != null) { %>
        <p class="message"><%= request.getAttribute("message") %></p>
    <% } %>

    <div class="filter-form">
        <form action="${pageContext.request.contextPath}/reservations" method="GET">
            <label for="dateFiltre">Filtrer par date :</label>
            <input type="date" id="dateFiltre" name="dateFiltre" 
                   value="<%= request.getAttribute("dateFiltre") != null ? request.getAttribute("dateFiltre") : "" %>"/>
            <button type="submit">Filtrer</button>
            <a href="${pageContext.request.contextPath}/reservations">Réinitialiser</a>
        </form>
    </div>

    <a href="${pageContext.request.contextPath}/reservations/new" class="btn">Nouvelle réservation</a>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Référence</th>
                <th>Nombre</th>
                <th>Date</th>
                <th>Heure</th>
                <th>Hôtel</th>
            </tr>
        </thead>
        <tbody>
            <%
                List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
                if (reservations != null) {
                    for (Reservation r : reservations) {
            %>
            <tr>
                <td><%= r.getId() %></td>
                <td><%= r.getReference() %></td>
                <td><%= r.getNombre() %></td>
                <td><%= r.getDate() %></td>
                <td><%= r.getHeure() %></td>
                <td><%= r.getHotel() %></td>
            </tr>
            <%
                    }
                }
            %>
        </tbody>
    </table>

    <br>
    <a href="${pageContext.request.contextPath}/">Retour à l'accueil</a>
</body>
</html>
