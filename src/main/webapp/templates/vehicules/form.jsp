<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.backoffice.models.Vehicule" %>
<%@ page import="com.backoffice.models.Vehicule.TypeCarburant" %>
<%
    Boolean editMode = (Boolean) request.getAttribute("editMode");
    boolean isEdit = editMode != null && editMode;
    Vehicule vehicule = (Vehicule) request.getAttribute("vehicule");
    TypeCarburant[] typesCarburant = (TypeCarburant[]) request.getAttribute("typesCarburant");
%>
<html>
<head>
    <title><%= isEdit ? "Modifier" : "Nouveau" %> véhicule</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        h1 { color: #333; }
        .container { max-width: 500px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        form { margin-top: 20px; }
        label { display: block; margin-top: 15px; font-weight: bold; color: #333; }
        input, select { width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; font-size: 14px; }
        input:focus, select:focus { border-color: #2196F3; outline: none; }
        .btn { display: inline-block; padding: 12px 24px; background-color: #2196F3; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 20px; font-size: 14px; }
        .btn:hover { background-color: #1976D2; }
        .btn-back { background-color: #666; text-decoration: none; color: white; padding: 12px 24px; border-radius: 4px; margin-left: 10px; }
        .btn-back:hover { background-color: #555; }
        .buttons { margin-top: 25px; }
        .help-text { font-size: 12px; color: #666; margin-top: 5px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚗 <%= isEdit ? "Modifier le" : "Nouveau" %> véhicule</h1>

        <form action="${pageContext.request.contextPath}/<%= isEdit ? "vehicules/update" : "vehicules" %>" method="POST">
            <% if (isEdit && vehicule != null) { %>
                <input type="hidden" name="id" value="<%= vehicule.getId() %>"/>
            <% } %>
            
            <label for="reference">Référence</label>
            <input type="text" id="reference" name="reference" required placeholder="ex: VH-2026-001"
                   value="<%= isEdit && vehicule != null ? vehicule.getReference() : "" %>" maxlength="50"/>
            <p class="help-text">Identifiant unique du véhicule (50 caractères max)</p>

            <label for="place">Nombre de places</label>
            <input type="number" id="place" name="place" min="1" max="50" required placeholder="ex: 5"
                   value="<%= isEdit && vehicule != null ? vehicule.getPlace() : "" %>"/>

            <label for="typeCarburant">Type de carburant</label>
            <select id="typeCarburant" name="typeCarburant" required>
                <option value="">-- Sélectionner un type --</option>
                <% if (typesCarburant != null) {
                    for (TypeCarburant t : typesCarburant) {
                        boolean selected = isEdit && vehicule != null && vehicule.getTypeCarburant() == t;
                %>
                <option value="<%= t.name() %>" <%= selected ? "selected" : "" %>><%= t.getLibelle() %></option>
                <% }} %>
            </select>

            <div class="buttons">
                <button type="submit" class="btn"><%= isEdit ? "Mettre à jour" : "Créer le véhicule" %></button>
                <a href="${pageContext.request.contextPath}/vehicules" class="btn-back">Annuler</a>
            </div>
        </form>
    </div>
</body>
</html>
