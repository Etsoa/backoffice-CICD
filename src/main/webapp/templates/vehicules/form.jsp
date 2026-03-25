<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.backoffice.models.Vehicule" %>
<%@ page import="com.backoffice.models.TypeCarburant" %>
<%@ page import="java.util.List" %>
<%
    Boolean editMode = (Boolean) request.getAttribute("editMode");
    boolean isEdit = editMode != null && editMode;
    Vehicule vehicule = (Vehicule) request.getAttribute("vehicule");
    @SuppressWarnings("unchecked")
    List<TypeCarburant> typesCarburant = (List<TypeCarburant>) request.getAttribute("typesCarburant");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isEdit ? "Modifier" : "Nouveau" %> vehicule</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', 'Segoe UI', sans-serif; background-color: #f0f4f8; min-height: 100vh; }

        .sidebar { position: fixed; left: 0; top: 0; bottom: 0; width: 260px; background: #ffffff; border-right: 1px solid #e2e8f0; display: flex; flex-direction: column; z-index: 100; }
        .sidebar-brand { padding: 24px 20px; border-bottom: 1px solid #e2e8f0; display: flex; align-items: center; gap: 12px; }
        .sidebar-brand i { font-size: 24px; color: #1a73e8; }
        .sidebar-brand span { font-size: 18px; font-weight: 700; color: #1e293b; }
        .sidebar-nav { padding: 16px 12px; flex: 1; }
        .sidebar-nav a { display: flex; align-items: center; gap: 12px; padding: 12px 16px; color: #64748b; text-decoration: none; border-radius: 8px; font-size: 14px; font-weight: 500; transition: all 0.2s; margin-bottom: 4px; }
        .sidebar-nav a:hover, .sidebar-nav a.active { background: #e8f0fe; color: #1a73e8; }
        .sidebar-nav a i { width: 20px; text-align: center; font-size: 16px; }

        .main { margin-left: 260px; padding: 32px 40px; }
        .page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 28px; }
        .page-header h1 { font-size: 26px; font-weight: 700; color: #1e293b; }

        .form-card { background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; max-width: 560px; }
        .form-card-header { padding: 20px 28px; border-bottom: 1px solid #e2e8f0; }
        .form-card-header h2 { font-size: 16px; font-weight: 600; color: #1e293b; display: flex; align-items: center; gap: 8px; }
        .form-card-header h2 i { color: #1a73e8; }
        .form-card-body { padding: 28px; }

        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; font-size: 13px; font-weight: 600; color: #475569; margin-bottom: 6px; }
        .form-control { width: 100%; padding: 10px 14px; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 14px; font-family: inherit; transition: border-color 0.2s; }
        .form-control:focus { outline: none; border-color: #1a73e8; box-shadow: 0 0 0 3px rgba(26,115,232,0.1); }
        .help-text { font-size: 12px; color: #94a3b8; margin-top: 4px; }

        .form-actions { display: flex; gap: 12px; margin-top: 28px; padding-top: 20px; border-top: 1px solid #f1f5f9; }
        .btn { display: inline-flex; align-items: center; gap: 8px; padding: 10px 24px; border-radius: 8px; font-size: 14px; font-weight: 600; text-decoration: none; transition: all 0.2s; border: none; cursor: pointer; font-family: inherit; }
        .btn-primary { background: #1a73e8; color: #fff; }
        .btn-primary:hover { background: #1557b0; }
        .btn-secondary { background: #e2e8f0; color: #64748b; }
        .btn-secondary:hover { background: #cbd5e1; color: #475569; }

        @media (max-width: 768px) {
            .sidebar { display: none; }
            .main { margin-left: 0; padding: 20px; }
        }
    </style>
</head>
<body>
    <aside class="sidebar">
        <div class="sidebar-brand">
            <i class="fas fa-building"></i>
            <span>BackOffice</span>
        </div>
        <nav class="sidebar-nav">
            <a href="${pageContext.request.contextPath}/">
                <i class="fas fa-home"></i> Tableau de bord
            </a>
            <a href="${pageContext.request.contextPath}/reservations">
                <i class="fas fa-calendar-check"></i> Reservations
            </a>
            <a href="${pageContext.request.contextPath}/vehicules" class="active">
                <i class="fas fa-car"></i> Vehicules
            </a>
            <a href="${pageContext.request.contextPath}/hotels">
                <i class="fas fa-hotel"></i> Hotels
            </a>
            <a href="${pageContext.request.contextPath}/planification">
                <i class="fas fa-route"></i> Planification
            </a>
        </nav>
    </aside>

    <main class="main">
        <div class="page-header">
            <h1><i class="fas fa-car" style="color:#1a73e8;"></i> <%= isEdit ? "Modifier le" : "Nouveau" %> vehicule</h1>
        </div>

        <div class="form-card">
            <div class="form-card-header">
                <h2><i class="fas fa-<%= isEdit ? "edit" : "plus-circle" %>"></i> <%= isEdit ? "Modifier les informations" : "Remplir les informations" %></h2>
            </div>
            <div class="form-card-body">
                <form action="${pageContext.request.contextPath}/<%= isEdit ? "vehicules/update" : "vehicules" %>" method="POST">
                    <% if (isEdit && vehicule != null) { %>
                        <input type="hidden" name="id" value="<%= vehicule.getId() %>"/>
                    <% } %>

                    <div class="form-group">
                        <label for="reference"><i class="fas fa-tag"></i> Reference</label>
                        <input type="text" id="reference" name="reference" class="form-control" required placeholder="ex: VH-2026-001"
                               value="<%= isEdit && vehicule != null ? vehicule.getReference() : "" %>" maxlength="50"/>
                        <p class="help-text">Identifiant unique du vehicule (50 caracteres max)</p>
                    </div>

                    <div class="form-group">
                        <label for="place"><i class="fas fa-chair"></i> Nombre de places</label>
                        <input type="number" id="place" name="place" class="form-control" min="1" max="50" required placeholder="ex: 5"
                               value="<%= isEdit && vehicule != null ? vehicule.getPlace() : "" %>"/>
                    </div>

                    <div class="form-group">
                        <label for="typeCarburant"><i class="fas fa-gas-pump"></i> Type de carburant</label>
                        <select id="typeCarburant" name="typeCarburant" class="form-control" required>
                            <option value="">-- Selectionner un type --</option>
                            <% if (typesCarburant != null) {
                                for (TypeCarburant t : typesCarburant) {
                                    boolean selected = isEdit && vehicule != null && vehicule.getTypeCarburant() != null 
                                                       && vehicule.getTypeCarburant().getId().equals(t.getId());
                            %>
                            <option value="<%= t.getId() %>" <%= selected ? "selected" : "" %>><%= t.getLibelle() %></option>
                            <% }} %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="vitesseMoyenne"><i class="fas fa-tachometer-alt"></i> Vitesse moyenne (km/h)</label>
                        <input type="number" id="vitesseMoyenne" name="vitesseMoyenne" class="form-control" min="1" max="200" step="0.01" required placeholder="ex: 60"
                               value="<%= isEdit && vehicule != null ? vehicule.getVitesseMoyenne() : "60.0" %>"/>
                    </div>
					
					<div class="form-group">
                        <label for="heureDisponibilite"><i class="fas fa-clock"></i> Heure Disponibilité (HH:MM)</label>
                        <input type="time" id="heureDisponibilite" name="heureDisponibilite" class="form-control"
                               value="<%= (isEdit && vehicule != null && vehicule.getHeureDisponibilite() != null) ? vehicule.getHeureDisponibilite().toString() : "00:00:00" %>"/>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-<%= isEdit ? "save" : "plus" %>"></i> <%= isEdit ? "Mettre a jour" : "Creer" %>
                        </button>
                        <a href="${pageContext.request.contextPath}/vehicules" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</body>
</html>
