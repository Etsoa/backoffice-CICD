<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.backoffice.models.Vehicule" %>
<%@ page import="com.backoffice.models.TypeCarburant" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicules</title>
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
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
        .page-header h1 { font-size: 26px; font-weight: 700; color: #1e293b; }

        .btn { display: inline-flex; align-items: center; gap: 8px; padding: 10px 20px; border-radius: 8px; font-size: 13px; font-weight: 600; text-decoration: none; transition: all 0.2s; border: none; cursor: pointer; }
        .btn-primary { background: #1a73e8; color: #fff; }
        .btn-primary:hover { background: #1557b0; }
        .btn-warning { background: #ea8600; color: #fff; }
        .btn-warning:hover { background: #d47a00; }
        .btn-danger { background: #ea4335; color: #fff; }
        .btn-danger:hover { background: #d33426; }
        .btn-secondary { background: #e2e8f0; color: #64748b; }
        .btn-secondary:hover { background: #cbd5e1; color: #475569; }
        .btn-sm { padding: 6px 14px; font-size: 12px; }

        .message { display: flex; align-items: center; gap: 10px; padding: 12px 16px; background: #e6f4ea; color: #1e7e34; border-radius: 8px; margin-bottom: 20px; font-size: 14px; font-weight: 500; border: 1px solid #b7dfbf; }
        .message i { font-size: 16px; }

        .card { background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
        .card-header { padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
        .card-header h3 { font-size: 15px; font-weight: 600; color: #1e293b; display: flex; align-items: center; gap: 8px; }
        .card-header h3 i { color: #1a73e8; }
        .card-body { padding: 20px; }

        .filter-row { display: flex; gap: 16px; align-items: flex-end; flex-wrap: wrap; }
        .filter-group { display: flex; flex-direction: column; gap: 6px; }
        .filter-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
        .form-control { padding: 9px 14px; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 14px; font-family: inherit; transition: border-color 0.2s; min-width: 160px; }
        .form-control:focus { outline: none; border-color: #1a73e8; box-shadow: 0 0 0 3px rgba(26,115,232,0.1); }

        .stats-bar { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; font-size: 14px; color: #64748b; }
        .stats-bar strong { color: #1a73e8; font-size: 18px; }

        .data-table { width: 100%; border-collapse: collapse; }
        .data-table thead th { padding: 12px 16px; text-align: left; font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; background: #f8fafc; border-bottom: 2px solid #e2e8f0; }
        .data-table tbody td { padding: 14px 16px; font-size: 14px; color: #334155; border-bottom: 1px solid #f1f5f9; }
        .data-table tbody tr:hover { background: #f8fafc; }
        .data-table tbody tr:last-child td { border-bottom: none; }
        .actions-cell { display: flex; gap: 6px; }

        .badge { display: inline-flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: 600; }
        .badge-D { background: #fce4ec; color: #c62828; }
        .badge-Es { background: #fff3e0; color: #e65100; }
        .badge-H { background: #e8f5e9; color: #2e7d32; }
        .badge-El { background: #e3f2fd; color: #1565c0; }

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
            <h1><i class="fas fa-car" style="color:#1a73e8; margin-right:8px;"></i> Vehicules</h1>
            <a href="${pageContext.request.contextPath}/vehicules/new" class="btn btn-primary">
                <i class="fas fa-plus"></i> Nouveau vehicule
            </a>
        </div>

        <% if (request.getAttribute("message") != null) { %>
            <div class="message">
                <i class="fas fa-check-circle"></i>
                <%= request.getAttribute("message") %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-header">
                <h3><i class="fas fa-filter"></i> Filtrer</h3>
                <a href="${pageContext.request.contextPath}/vehicules" class="btn btn-secondary btn-sm">
                    <i class="fas fa-redo"></i> Reinitialiser
                </a>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/vehicules" method="GET">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label for="typeCarburant">Type de carburant</label>
                            <select id="typeCarburant" name="typeCarburant" class="form-control">
                                <option value="">-- Tous --</option>
                                <%
                                    @SuppressWarnings("unchecked")
                                    List<TypeCarburant> types = (List<TypeCarburant>) request.getAttribute("typesCarburant");
                                    String filtreActuel = (String) request.getAttribute("typeCarburantFiltre");
                                    if (types != null) {
                                        for (TypeCarburant t : types) {
                                            boolean selected = filtreActuel != null && filtreActuel.equals(String.valueOf(t.getId()));
                                %>
                                <option value="<%= t.getId() %>" <%= selected ? "selected" : "" %>><%= t.getLibelle() %></option>
                                <% }} %>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">
                            <i class="fas fa-search"></i> Filtrer
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h3><i class="fas fa-clock"></i> Disponibilite</h3>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/vehicules" method="GET">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label for="dispoDate">Date</label>
                            <input type="date" id="dispoDate" name="dispoDate" class="form-control"
                                   value="${dispoDate != null ? dispoDate : ''}" required>
                        </div>
                        <div class="filter-group">
                            <label for="dispoHeure">Heure</label>
                            <input type="time" id="dispoHeure" name="dispoHeure" class="form-control"
                                   value="${dispoHeure != null ? dispoHeure : ''}" required>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">
                            <i class="fas fa-search"></i> Voir disponibilite
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <%
            Boolean filtreDispoActif = (Boolean) request.getAttribute("filtreDispoActif");
            if (filtreDispoActif != null && filtreDispoActif) {
        %>
            <div class="message" style="background:#e8f0fe; color:#1a73e8; border-color:#bfdbfe;">
                <i class="fas fa-info-circle"></i>
                Vehicules disponibles le <strong>${dispoDate}</strong> a <strong>${dispoHeure}</strong>
            </div>
        <% } %>

        <%
            List<Vehicule> vehicules = (List<Vehicule>) request.getAttribute("vehicules");
        %>

        <div class="stats-bar">
            <i class="fas fa-list" style="color:#1a73e8;"></i>
            <strong><%= vehicules != null ? vehicules.size() : 0 %></strong> vehicule(s)
        </div>

        <div class="card">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Reference</th>
                        <th>Places</th>
                        <th>Type carburant</th>
                        <th>Vitesse moy.</th>
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
                            <span class="badge badge-<%= v.getTypeCarburant().getCode() %>">
                                <i class="fas fa-gas-pump"></i> <%= v.getTypeCarburant().getLibelle() %>
                            </span>
                        </td>
                        <td><%= String.format("%.0f", v.getVitesseMoyenne()) %> km/h</td>
                        <td class="actions-cell">
                            <a href="${pageContext.request.contextPath}/vehicules/edit?id=<%= v.getId() %>" class="btn btn-warning btn-sm">
                                <i class="fas fa-edit"></i>
                            </a>
                            <a href="${pageContext.request.contextPath}/vehicules/delete?id=<%= v.getId() %>" class="btn btn-danger btn-sm" onclick="return confirm('Supprimer ce vehicule ?');">
                                <i class="fas fa-trash"></i>
                            </a>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                </tbody>
            </table>
        </div>
    </main>
</body>
</html>
