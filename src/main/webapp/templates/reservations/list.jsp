<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.backoffice.models.Reservation" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reservations</title>
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
        .btn-success { background: #34a853; color: #fff; }
        .btn-success:hover { background: #2d9249; }
        .btn-warning { background: #ea8600; color: #fff; }
        .btn-warning:hover { background: #d47a00; }
        .btn-danger { background: #ea4335; color: #fff; }
        .btn-danger:hover { background: #d33426; }
        .btn-secondary { background: #e2e8f0; color: #64748b; }
        .btn-secondary:hover { background: #cbd5e1; color: #475569; }
        .btn-sm { padding: 6px 14px; font-size: 12px; }

        .message { display: flex; align-items: center; gap: 10px; padding: 12px 16px; background: #e6f4ea; color: #1e7e34; border-radius: 8px; margin-bottom: 20px; font-size: 14px; font-weight: 500; border: 1px solid #b7dfbf; }
        .message.error { background: #fdecea; color: #c62828; border-color: #f5c6cb; }
        .message i { font-size: 16px; }

        .card { background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
        .card-header { padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
        .card-header h3 { font-size: 15px; font-weight: 600; color: #1e293b; display: flex; align-items: center; gap: 8px; }
        .card-header h3 i { color: #1a73e8; }
        .card-body { padding: 20px; }

        .filter-row { display: flex; gap: 16px; align-items: flex-end; flex-wrap: wrap; }
        .filter-group { display: flex; flex-direction: column; gap: 6px; }
        .filter-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
        .filter-group .separator { font-size: 13px; color: #94a3b8; font-weight: 500; padding-bottom: 10px; }
        .form-control { padding: 9px 14px; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 14px; font-family: inherit; transition: border-color 0.2s; }
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
        .badge-blue { background: #e8f0fe; color: #1a73e8; }

        @media (max-width: 768px) {
            .sidebar { display: none; }
            .main { margin-left: 0; padding: 20px; }
            .filter-row { flex-direction: column; align-items: stretch; }
        }
    </style>
</head>
<body>
    <aside class="sidebar">
        <div class="sidebar-brand">
            <i class="fas fa-building"></i>
            <span>BackOffice ETU3341 -3256 -3326</span>
        </div>
        <nav class="sidebar-nav">
            <a href="${pageContext.request.contextPath}/">
                <i class="fas fa-home"></i> Tableau de bord
            </a>
            <a href="${pageContext.request.contextPath}/reservations" class="active">
                <i class="fas fa-calendar-check"></i> Reservations
            </a>
            <a href="${pageContext.request.contextPath}/vehicules">
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
            <h1><i class="fas fa-calendar-check" style="color:#1a73e8; margin-right:8px;"></i> Reservations</h1>
            <a href="${pageContext.request.contextPath}/reservations/new" class="btn btn-primary">
                <i class="fas fa-plus"></i> Nouvelle reservation
            </a>
        </div>

        <% if (request.getAttribute("message") != null) { 
            String msg = request.getAttribute("message").toString();
            boolean isError = msg.startsWith("Erreur");
        %>
            <div class="message<%= isError ? " error" : "" %>">
                <i class="fas fa-<%= isError ? "exclamation-circle" : "check-circle" %>"></i>
                <%= msg %>
            </div>
        <% } %>

        <div class="card">
            <div class="card-header">
                <h3><i class="fas fa-filter"></i> Filtrer</h3>
                <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary btn-sm">
                    <i class="fas fa-redo"></i> Reinitialiser
                </a>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/reservations" method="GET">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label for="dateFiltre">Date exacte</label>
                            <input type="date" id="dateFiltre" name="dateFiltre" class="form-control"
                                   value="<%= request.getAttribute("dateFiltre") != null ? request.getAttribute("dateFiltre") : "" %>"/>
                        </div>
                        <div class="filter-group">
                            <span class="separator">ou</span>
                        </div>
                        <div class="filter-group">
                            <label for="dateDebut">Date debut</label>
                            <input type="date" id="dateDebut" name="dateDebut" class="form-control"
                                   value="<%= request.getAttribute("dateDebut") != null ? request.getAttribute("dateDebut") : "" %>"/>
                        </div>
                        <div class="filter-group">
                            <label for="dateFin">Date fin</label>
                            <input type="date" id="dateFin" name="dateFin" class="form-control"
                                   value="<%= request.getAttribute("dateFin") != null ? request.getAttribute("dateFin") : "" %>"/>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm">
                            <i class="fas fa-search"></i> Filtrer
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <%
            List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
            Map<Integer, String> hotelMap = (Map<Integer, String>) request.getAttribute("hotelMap");
        %>

        <div class="stats-bar">
            <i class="fas fa-list" style="color:#1a73e8;"></i>
            <strong><%= reservations != null ? reservations.size() : 0 %></strong> reservation(s)
        </div>

        <div class="card">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Reference</th>
                        <th>Nombre</th>
                        <th>Date</th>
                        <th>Heure</th>
                        <th>Hotel</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (reservations != null) {
                            for (Reservation r : reservations) {
                                String hotelName = hotelMap != null && hotelMap.get(r.getHotel()) != null
                                    ? hotelMap.get(r.getHotel())
                                    : "Hotel #" + r.getHotel();
                    %>
                    <tr>
                        <td><%= r.getId() %></td>
                        <td><span class="badge badge-blue"><%= r.getReference() %></span></td>
                        <td><%= r.getNombre() %></td>
                        <td><%= r.getDate() %></td>
                        <td><%= r.getHeure() %></td>
                        <td><%= hotelName %></td>
                        <td class="actions-cell">
                            <a href="${pageContext.request.contextPath}/reservations/edit?id=<%= r.getId() %>" class="btn btn-warning btn-sm">
                                <i class="fas fa-edit"></i>
                            </a>
                            <a href="${pageContext.request.contextPath}/reservations/delete?id=<%= r.getId() %>" class="btn btn-danger btn-sm" onclick="return confirm('Supprimer cette reservation ?');">
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
