<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.backoffice.dto.VehiculePlanningDTO" %>
<%@ page import="com.backoffice.dto.ReservationPlanningDTO" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Planification</title>
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
        .page-header { margin-bottom: 24px; }
        .page-header h1 { font-size: 26px; font-weight: 700; color: #1e293b; margin-bottom: 4px; }
        .page-header p { color: #64748b; font-size: 14px; }

        .btn { display: inline-flex; align-items: center; gap: 8px; padding: 10px 20px; border-radius: 8px; font-size: 13px; font-weight: 600; text-decoration: none; transition: all 0.2s; border: none; cursor: pointer; font-family: inherit; }
        .btn-primary { background: #1a73e8; color: #fff; }
        .btn-primary:hover { background: #1557b0; }

        .card { background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
        .card-header { padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
        .card-header h3 { font-size: 15px; font-weight: 600; color: #1e293b; display: flex; align-items: center; gap: 8px; }
        .card-header h3 i { color: #1a73e8; }
        .card-body { padding: 20px; }

        .date-form { display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap; }
        .date-form .form-group { display: flex; flex-direction: column; gap: 6px; }
        .date-form .form-group label { font-size: 12px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; }
        .form-control { padding: 9px 14px; border: 1px solid #e2e8f0; border-radius: 8px; font-size: 14px; font-family: inherit; transition: border-color 0.2s; }
        .form-control:focus { outline: none; border-color: #1a73e8; box-shadow: 0 0 0 3px rgba(26,115,232,0.1); }

        .alert { display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; font-weight: 500; }
        .alert-error { background: #fef2f2; color: #dc2626; border: 1px solid #fecaca; }
        .alert-info { background: #e8f0fe; color: #1a73e8; border: 1px solid #bfdbfe; }

        /* Vehicle section */
        .vehicle-section { margin-bottom: 32px; }
        .vehicle-header { background: #f8fafc; color: #334155; padding: 14px 24px; border-radius: 10px 10px 0 0; display: flex; justify-content: space-between; align-items: center; border: 1px solid #e2e8f0; border-bottom: none; }
        .vehicle-header .vh-ref { font-size: 16px; font-weight: 700; letter-spacing: 0.3px; color: #1e293b; }
        .vehicle-header .vh-meta { display: flex; gap: 16px; font-size: 13px; font-weight: 500; color: #64748b; }
        .vehicle-header .vh-count { font-size: 13px; color: #64748b; }

        .planning-table { width: 100%; border-collapse: collapse; background: #fff; border: 1px solid #e2e8f0; border-top: none; }
        .planning-table thead th { padding: 10px 16px; font-size: 11px; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; text-align: left; background: #f8fafc; border-bottom: 2px solid #e2e8f0; }
        .planning-table tbody td { padding: 12px 16px; font-size: 13px; color: #334155; border-bottom: 1px solid #f1f5f9; font-weight: 500; }
        .planning-table tbody tr:hover { background: #f8fafc; }
        .planning-table tbody tr:last-child td { border-bottom: none; }

        .td-time { font-variant-numeric: tabular-nums; font-weight: 600; color: #1e293b; }
        .td-hotel { color: #1a73e8; font-weight: 600; }
        .td-ref { color: #64748b; }

        .retour-row { background: #f0fdf4; border: 1px solid #e2e8f0; border-top: none; border-radius: 0 0 10px 10px; padding: 12px 24px; display: flex; justify-content: space-between; align-items: center; }
        .retour-row .retour-label { font-size: 13px; font-weight: 600; color: #166534; }
        .retour-row .retour-time { font-size: 16px; font-weight: 700; color: #166534; }

        .empty-state { text-align: center; padding: 60px 20px; }
        .empty-state i { font-size: 48px; color: #cbd5e1; margin-bottom: 16px; }
        .empty-state h2 { font-size: 20px; font-weight: 600; color: #475569; margin-bottom: 8px; }
        .empty-state p { color: #94a3b8; font-size: 14px; }

        @media (max-width: 768px) {
            .sidebar { display: none; }
            .main { margin-left: 0; padding: 20px; }
            .planning-table { font-size: 12px; }
            .planning-table thead th, .planning-table tbody td { padding: 8px 10px; }
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
            <a href="${pageContext.request.contextPath}/vehicules">
                <i class="fas fa-car"></i> Vehicules
            </a>
            <a href="${pageContext.request.contextPath}/hotels">
                <i class="fas fa-hotel"></i> Hotels
            </a>
            <a href="${pageContext.request.contextPath}/planification" class="active">
                <i class="fas fa-route"></i> Planification
            </a>
        </nav>
    </aside>

    <main class="main">
        <div class="page-header">
            <h1>Planification</h1>
            <p>Planning des vehicules pour les transferts</p>
        </div>

        <div class="card">
            <div class="card-header">
                <h3><i class="fas fa-calendar-alt"></i> Selectionner une date</h3>
            </div>
            <div class="card-body">
                <form method="GET" action="${pageContext.request.contextPath}/planification" class="date-form">
                    <div class="form-group">
                        <label for="date">Date</label>
                        <input type="date" id="date" name="date" class="form-control"
                               value="${dateSelectionnee != null ? dateSelectionnee : ''}" required>
                    </div>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-search"></i> Afficher
                    </button>
                    <% if (request.getAttribute("dateSelectionnee") != null) { %>
                        <a href="${pageContext.request.contextPath}/planification/vehicules-non-assignes?date=${dateSelectionnee}"
                           class="btn btn-primary" style="background:#16a34a; margin-left:8px;">
                            <i class="fas fa-car"></i> Véhicules non assignés
                        </a>
                    <% } %>
                </form>
            </div>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <i class="fas fa-exclamation-triangle"></i>
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <%
        @SuppressWarnings("unchecked")
        List<VehiculePlanningDTO> planning = (List<VehiculePlanningDTO>) request.getAttribute("planning");

        if (planning != null && !planning.isEmpty()) {
        %>
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i>
                Planning du <strong>${dateSelectionnee}</strong> &mdash; <%= planning.size() %> vehicule(s) utilise(s)
            </div>

            <% for (VehiculePlanningDTO vehiculePlanning : planning) {
                int totalPersonnes = 0;
                for (ReservationPlanningDTO rp : vehiculePlanning.getReservations()) {
                    totalPersonnes += rp.getReservation().getNombre();
                }
            %>
                <div class="vehicle-section">
                    <div class="vehicle-header">
                        <span class="vh-ref"><%= vehiculePlanning.getVehicule().getReference() %></span>
                        <div class="vh-meta">
                            <span><%= vehiculePlanning.getVehicule().getPlace() %> places (<%= totalPersonnes %> occupees)</span>
                            <span><%= vehiculePlanning.getVehicule().getTypeCarburant().getLibelle() %></span>
                            <span><i class="fas fa-map-signs"></i> <%= vehiculePlanning.getDestinationString() %></span>
                            <span><%= String.format("%.0f", vehiculePlanning.getVehicule().getVitesseMoyenne()) %> km/h</span>
                            <span><i class="fas fa-clock"></i> Depart : <%= vehiculePlanning.getHeureDepart() %></span>
                            <span><i class="fas fa-road"></i> <%= vehiculePlanning.getDistanceTotale() != null ? String.format("%.1f", vehiculePlanning.getDistanceTotale()) : "0.0" %> km total</span>
                        </div>
                        <span class="vh-count"><%= vehiculePlanning.getReservations().size() %> reservation(s)</span>
                    </div>

                    <table class="planning-table">
                        <thead>
                            <tr>
                                <th>Ordre</th>
                                <th>Reference</th>
                                <th>Hotel</th>
                                <th>Personnes</th>
                                <th>Heure RDV</th>
                                <th>Depart segment</th>
                                <th>Arrivee hotel</th>
                                <th>Distance (TNR)</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% int ordre = 1;
                        for (ReservationPlanningDTO resPlanning : vehiculePlanning.getReservations()) { %>
                            <tr>
                                <td><%= ordre++ %></td>
                                <td class="td-ref">#<%= resPlanning.getReservation().getReference() %></td>
                                <td class="td-hotel"><%= resPlanning.getHotelLibelle() %></td>
                                <td><%= resPlanning.getReservation().getNombre() %></td>
                                <td class="td-time"><%= resPlanning.getReservation().getHeure() %></td>
                                <td class="td-time"><%= resPlanning.getHeureDepart() %></td>
                                <td class="td-time"><%= resPlanning.getHeureRetour() %></td>
                                <td><%= String.format("%.1f", resPlanning.getDistanceKm()) %> km</td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>

                    <div class="retour-row">
                        <span class="retour-label"><i class="fas fa-plane-arrival"></i> Retour a l'aeroport (TNR)</span>
                        <span class="retour-time"><%= vehiculePlanning.getHeureRetourAeroport() %></span>
                    </div>
                </div>
            <% } %>

        <% } else if (request.getAttribute("dateSelectionnee") != null) { %>
            <div class="empty-state">
                <i class="fas fa-calendar-times"></i>
                <h2>Aucune reservation</h2>
                <p>Aucune reservation trouvee pour la date selectionnee.</p>
            </div>
        <% } else { %>
            <div class="empty-state">
                <i class="fas fa-calendar-day"></i>
                <h2>Selectionnez une date</h2>
                <p>Veuillez selectionner une date pour afficher le planning des vehicules.</p>
            </div>
        <% } %>
    </main>
</body>
</html>
