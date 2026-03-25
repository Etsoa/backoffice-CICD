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
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; flex-wrap: wrap; gap: 16px; }
        .page-header h1 { font-size: 26px; font-weight: 700; color: #1e293b; }

        .btn { display: inline-flex; align-items: center; gap: 8px; padding: 10px 20px; border-radius: 8px; font-size: 13px; font-weight: 600; text-decoration: none; transition: all 0.2s; border: none; cursor: pointer; }
        .btn-primary { background: #1a73e8; color: #fff; }
        .btn-primary:hover { background: #1557b0; }

        .card { background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
        .card-header { padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
        .card-header h3 { font-size: 15px; font-weight: 600; color: #1e293b; display: flex; align-items: center; gap: 8px; }
        .card-header h3 i { color: #16a34a; }
        .card-body { padding: 0; }

        .stats-bar { display: flex; align-items: center; gap: 8px; padding: 16px 20px; font-size: 14px; color: #64748b; border-bottom: 1px solid #e2e8f0; }
        .stats-bar strong { color: #16a34a; font-size: 18px; }

        .data-table { width: 100%; border-collapse: collapse; }
        .data-table thead th { padding: 12px 16px; text-align: left; font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; background: #f8fafc; border-bottom: 2px solid #e2e8f0; }
        .data-table tbody td { padding: 14px 16px; font-size: 14px; color: #334155; border-bottom: 1px solid #f1f5f9; }
        .data-table tbody tr:hover { background: #f8fafc; }
        .data-table tbody tr:last-child td { border-bottom: none; }

        .badge { display: inline-flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: 600; }
        .badge-green { background: #dcfce7; color: #166534; }
        .badge-yellow { background: #fef9c3; color: #854d0e; }
        .badge-blue { background: #dbeafe; color: #1e40af; }

        .empty-state { text-align: center; padding: 60px 20px; }
        .empty-state i { font-size: 48px; color: #cbd5e1; margin-bottom: 16px; }
        .empty-state h2 { font-size: 20px; font-weight: 600; color: #475569; margin-bottom: 8px; }
        .empty-state p { color: #94a3b8; font-size: 14px; }

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
            <span>BackOffice ETU3341 -3256 -3326</span>
        </div>
        <nav class="sidebar-nav">
            <a href="${pageContext.request.contextPath}/">
                <i class="fas fa-home"></i> Tableau de bord
            </a>
            <a href="${pageContext.request.contextPath}/reservations">
                <i class="fas fa-calendar-check"></i> Reservations
            </a>
            <a href="${pageContext.request.contextPath}/vehicules">
                <i class="fas fa-car"></i> Vehicules
            </a>
            <a href="${pageContext.request.contextPath}/hotels">
                <i class="fas fa-hotel"></i> Hotels
            </a>
            <a href="${pageContext.request.contextPath}/planification" class="active">
                <i class="fas fa-route"></i> Planification
            </a>
            <a href="${pageContext.request.contextPath}/distances">
                <i class="fas fa-map-marked-alt"></i> Distances
            </a>
        </nav>
    </aside>

    <main class="main">
        <div class="page-header">
            <h1><i class="fas fa-car" style="color: #16a34a;"></i> Véhicules non assignés</h1>
            <a href="${pageContext.request.contextPath}/planification?date=${dateSelectionnee}" class="btn btn-primary">
                <i class="fas fa-arrow-left"></i> Retour au planning
            </a>
        </div>

        <%
            @SuppressWarnings("unchecked")
            List<Vehicule> vehicules = (List<Vehicule>) request.getAttribute("vehiculesLibres");
        %>

        <div class="card">
            <div class="card-header">
                <h3><i class="fas fa-car-side"></i> Véhicules disponibles le ${dateSelectionnee}</h3>
            </div>
            <% if (vehicules != null && !vehicules.isEmpty()) { %>
                <div class="stats-bar">
                    <i class="fas fa-check-circle" style="color: #16a34a;"></i>
                    <strong><%= vehicules.size() %></strong> véhicule(s) non utilisé(s)
                </div>
                <div class="card-body">
                    <table class="data-table">
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
                                <td><strong><%= v.getReference() %></strong></td>
                                <td><span class="badge badge-blue"><%= v.getPlace() %> places</span></td>
                                <td>
                                    <% String carb = v.getTypeCarburant() != null ? v.getTypeCarburant().getLibelle() : "-"; %>
                                    <% if ("Diesel".equals(carb)) { %>
                                        <span class="badge badge-green"><i class="fas fa-gas-pump"></i> <%= carb %></span>
                                    <% } else if ("Essence".equals(carb)) { %>
                                        <span class="badge badge-yellow"><i class="fas fa-gas-pump"></i> <%= carb %></span>
                                    <% } else { %>
                                        <span class="badge badge-blue"><i class="fas fa-bolt"></i> <%= carb %></span>
                                    <% } %>
                                </td>
                                <td><%= String.format("%.0f", v.getVitesseMoyenne()) %> km/h</td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <div class="card-body">
                    <div class="empty-state">
                        <i class="fas fa-car-side"></i>
                        <h2>Tous les véhicules sont assignés</h2>
                        <p>Tous les véhicules ont été utilisés pour les réservations de cette date.</p>
                    </div>
                </div>
            <% } %>
        </div>
    </main>
</body>
</html>
