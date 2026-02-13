<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.backoffice.models.Hotel" %>
<%@ page import="com.backoffice.models.Reservation" %>
<%
    Boolean editMode = (Boolean) request.getAttribute("editMode");
    boolean isEdit = editMode != null && editMode;
    Reservation reservation = (Reservation) request.getAttribute("reservation");
    List<Hotel> hotels = (List<Hotel>) request.getAttribute("hotels");
%>
<html>
<head>
    <title><%= isEdit ? "Modifier" : "Nouvelle" %> réservation</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        h1 { color: #333; }
        .container { max-width: 500px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        form { margin-top: 20px; }
        label { display: block; margin-top: 15px; font-weight: bold; color: #333; }
        input, select { width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; font-size: 14px; }
        input:focus, select:focus { border-color: #4CAF50; outline: none; }
        .btn { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 20px; font-size: 14px; }
        .btn:hover { background-color: #45a049; }
        .btn-back { background-color: #666; text-decoration: none; color: white; padding: 12px 24px; border-radius: 4px; margin-left: 10px; }
        .btn-back:hover { background-color: #555; }
        .buttons { margin-top: 25px; }
    </style>
</head>
<body>
    <div class="container">
        <h1><%= isEdit ? "Modifier la" : "Nouvelle" %> réservation</h1>

        <form action="${pageContext.request.contextPath}/<%= isEdit ? "reservations/update" : "reservations" %>" method="POST">
            <% if (isEdit && reservation != null) { %>
                <input type="hidden" name="id" value="<%= reservation.getId() %>"/>
            <% } %>
            
            <label for="reference">Référence</label>
            <input type="number" id="reference" name="reference" required placeholder="ex: 4631"
                   value="<%= isEdit && reservation != null ? reservation.getReference() : "" %>"/>

            <label for="nombre">Nombre de personnes</label>
            <input type="number" id="nombre" name="nombre" min="1" required placeholder="ex: 2"
                   value="<%= isEdit && reservation != null ? reservation.getNombre() : "" %>"/>

            <label for="date">Date</label>
            <input type="date" id="date" name="date" required
                   value="<%= isEdit && reservation != null ? reservation.getDate() : "" %>"/>

            <label for="heure">Heure</label>
            <input type="time" id="heure" name="heure" required
                   value="<%= isEdit && reservation != null ? reservation.getHeure().toString().substring(0, 5) : "" %>"/>

            <label for="hotel">Hôtel</label>
            <select id="hotel" name="hotel" required>
                <option value="">-- Sélectionner un hôtel --</option>
                <% if (hotels != null) {
                    for (Hotel h : hotels) {
                        boolean selected = isEdit && reservation != null && reservation.getHotel() == h.getId();
                %>
                <option value="<%= h.getId() %>" <%= selected ? "selected" : "" %>><%= h.getLibelle() %></option>
                <% }} %>
            </select>

            <div class="buttons">
                <button type="submit" class="btn"><%= isEdit ? "Mettre à jour" : "Créer la réservation" %></button>
                <a href="${pageContext.request.contextPath}/reservations" class="btn-back">Annuler</a>
            </div>
        </form>
    </div>
</body>
</html>
