<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.backoffice.models.Vehicule" %>
<%@ page import="com.backoffice.models.Vehicule.TypeCarburant" %>
<html>
<head>
    <title>Liste des véhicules</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        h1 { color: #333; }
        .container { max-width: 1200px; margin: 0 auto; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
        th { background-color: #2196F3; color: white; }
        tr:nth-child(even) { background-color: #f9f9f9; }
        tr:hover { background-color: #f1f1f1; }
        .btn { display: inline-block; padding: 10px 20px; background-color: #2196F3; color: white; text-decoration: none; border-radius: 4px; margin-bottom: 15px; }
        .btn:hover { background-color: #1976D2; }
        .btn-edit { background-color: #ff9800; padding: 6px 12px; font-size: 12px; }
        .btn-edit:hover { background-color: #f57c00; }
        .btn-delete { background-color: #f44336; padding: 6px 12px; font-size: 12px; }
        .btn-delete:hover { background-color: #d32f2f; }
        .message { color: green; font-weight: bold; margin-bottom: 10px; padding: 10px; background: #e8f5e9; border-radius: 4px; }
        .filter-form { background: white; padding: 20px; border-radius: 4px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .filter-form h3 { margin-top: 0; color: #333; }
        .filter-row { display: flex; gap: 15px; align-items: center; flex-wrap: wrap; }
        .filter-group { display: flex; flex-direction: column; }
        .filter-group label { font-weight: bold; margin-bottom: 5px; color: #666; font-size: 12px; }
        .filter-form select { padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; min-width: 150px; }
        .filter-form button { padding: 10px 20px; background-color: #2196F3; color: white; border: none; border-radius: 4px; cursor: pointer; height: 40px; }
        .filter-form button:hover { background-color: #1976D2; }
        .filter-form .btn-reset { background-color: #9e9e9e; text-decoration: none; display: inline-flex; align-items: center; }
        .filter-form .btn-reset:hover { background-color: #757575; }
        .actions { display: flex; gap: 5px; }
        .stats { background: white; padding: 15px; border-radius: 4px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .stats span { font-weight: bold; color: #2196F3; }
        .badge { display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; color: white; }
        .badge-D { background-color: #795548; }
        .badge-Es { background-color: #ff9800; }
        .badge-H { background-color: #4CAF50; }
        .badge-El { background-color: #2196F3; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚗 Liste des véhicules</h1>

        <% if (request.getAttribute("message") != null) { %>
            <p class="message"><%= request.getAttribute("message") %></p>
        <% } %>

        <div class="filter-form">
            <h3>Filtrer les véhicules</h3>
            <form action="${pageContext.request.contextPath}/vehicules" method="GET">
                <div class="filter-row">
                    <div class="filter-group">
                        <label for="typeCarburant">Type de carburant</label>
                        <select id="typeCarburant" name="typeCarburant">
                            <option value="">-- Tous --</option>
                            <%
                                TypeCarburant[] types = (TypeCarburant[]) request.getAttribute("typesCarburant");
                                String filtreActuel = (String) request.getAttribute("typeCarburantFiltre");
                                if (types != null) {
                                    for (TypeCarburant t : types) {
                                        boolean selected = filtreActuel != null && filtreActuel.equals(t.name());
                            %>
                            <option value="<%= t.name() %>" <%= selected ? "selected" : "" %>><%= t.getLibelle() %></option>
                            <% }} %>
                        </select>
                    </div>
                    <button type="submit">Filtrer</button>
                    <a href="${pageContext.request.contextPath}/vehicules" class="btn-reset">Réinitialiser</a>
                </div>
            </form>
        </div>

        <%
            List<Vehicule> vehicules = (List<Vehicule>) request.getAttribute("vehicules");
        %>
        
        <div class="stats">
            Total: <span><%= vehicules != null ? vehicules.size() : 0 %></span> véhicule(s)
        </div>

        <a href="${pageContext.request.contextPath}/vehicules/new" class="btn">+ Nouveau véhicule</a>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Référence</th>
                    <th>Places</th>
                    <th>Type carburant</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <%
                    if (vehicules != null) {
                        for (Vehicule v : vehicules) {
                %>
                <tr>
                    <td><%= v.getId() %></td>
                    <td><%= v.getReference() %></td>
                    <td><%= v.getPlace() %></td>
                    <td>
                        <span class="badge badge-<%= v.getTypeCarburant().name() %>">
                            <%= v.getTypeCarburant().getLibelle() %>
                        </span>
                    </td>
                    <td class="actions">
                        <a href="${pageContext.request.contextPath}/vehicules/edit?id=<%= v.getId() %>" class="btn btn-edit">Modifier</a>
                        <a href="${pageContext.request.contextPath}/vehicules/delete?id=<%= v.getId() %>" class="btn btn-delete" onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce véhicule ?');">Supprimer</a>
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
