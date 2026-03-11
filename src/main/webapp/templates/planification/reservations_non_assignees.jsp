<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.backoffice.models.Reservation" %>
<%@ page import="com.backoffice.dto.RegroupementDTO" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Réservations non assignées</title>
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
        .btn-secondary { background: #64748b; color: #fff; }
        .btn-secondary:hover { background: #475569; }

        .card { background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; margin-bottom: 20px; }
        .card-header { padding: 16px 20px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
        .card-header h3 { font-size: 15px; font-weight: 600; color: #1e293b; display: flex; align-items: center; gap: 8px; }
        .card-header h3 i { color: #dc2626; }
        .card-body { padding: 20px; }

        .alert { display: flex; align-items: center; gap: 10px; padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; font-weight: 500; }
        .alert-warning { background: #fef3c7; color: #b45309; border: 1px solid #fcd34d; }
        .alert-info { background: #e8f0fe; color: #1a73e8; border: 1px solid #bfdbfe; }
        .alert-success { background: #d1fae5; color: #065f46; border: 1px solid #6ee7b7; }

        .table { width: 100%; border-collapse: collapse; }
        .table thead th { padding: 12px 16px; font-size: 11px; font-weight: 700; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px; text-align: left; background: #f8fafc; border-bottom: 2px solid #e2e8f0; }
        .table tbody td { padding: 14px 16px; font-size: 14px; color: #334155; border-bottom: 1px solid #f1f5f9; font-weight: 500; }
        .table tbody tr:hover { background: #f8fafc; }

        .badge { display: inline-flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: 600; }
        .badge-danger { background: #fef2f2; color: #dc2626; }
        .badge-warning { background: #fef3c7; color: #b45309; }

        .empty-state { text-align: center; padding: 60px 20px; }
        .empty-state i { font-size: 48px; color: #10b981; margin-bottom: 16px; }
        .empty-state h2 { font-size: 20px; font-weight: 600; color: #475569; margin-bottom: 8px; }
        .empty-state p { color: #94a3b8; font-size: 14px; }

        .back-link { margin-bottom: 20px; }

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
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/planification?date=${dateSelectionnee}" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Retour au planning
            </a>
        </div>

        <div class="page-header">
            <h1><i class="fas fa-exclamation-triangle" style="color: #dc2626;"></i> Réservations non assignées</h1>
            <p>Réservations pour lesquelles aucun véhicule n'a pu être attribué (capacité insuffisante ou tous les véhicules utilisés)</p>
        </div>

        <%
        @SuppressWarnings("unchecked")
        List<Reservation> nonAssignees = (List<Reservation>) request.getAttribute("reservationsNonAssignees");
        String dateSelectionnee = (String) request.getAttribute("dateSelectionnee");

        if (nonAssignees != null && !nonAssignees.isEmpty()) {
            int totalPersonnes = 0;
            for (Reservation r : nonAssignees) {
                totalPersonnes += r.getNombre();
            }
        %>
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i>
                <strong><%= nonAssignees.size() %> réservation(s)</strong> non assignée(s) pour le <strong><%= dateSelectionnee %></strong>
                — Total: <strong><%= totalPersonnes %> personne(s)</strong> sans transport
            </div>

            <div class="card">
                <div class="card-header">
                    <h3><i class="fas fa-ban"></i> Détail des réservations sans véhicule</h3>
                </div>
                <div class="card-body">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Référence</th>
                                <th>Heure RDV</th>
                                <th>Personnes</th>
                                <th>Hôtel</th>
                                <th>Statut</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% for (Reservation resa : nonAssignees) { %>
                            <tr>
                                <td><strong>#<%= resa.getReference() %></strong></td>
                                <td><%= resa.getHeure() %></td>
                                <td>
                                    <span class="badge badge-danger">
                                        <i class="fas fa-users"></i> <%= resa.getNombre() %> pers.
                                    </span>
                                </td>
                                <td>Hôtel ID: <%= resa.getHotel() %></td>
                                <td>
                                    <span class="badge badge-warning">
                                        <i class="fas fa-car-crash"></i> Sans véhicule
                                    </span>
                                </td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i>
                <strong>Causes possibles :</strong>
                <ul style="margin-left: 20px; margin-top: 8px;">
                    <li>Tous les véhicules sont déjà utilisés pour d'autres transferts</li>
                    <li>Aucun véhicule disponible n'a assez de places pour le groupe</li>
                    <li>La capacité maximale des véhicules est dépassée</li>
                </ul>
            </div>

        <% } else if (dateSelectionnee != null) { %>
            <div class="empty-state">
                <i class="fas fa-check-circle"></i>
                <h2>Toutes les réservations sont assignées</h2>
                <p>Aucune réservation sans véhicule pour la date sélectionnée.</p>
            </div>
        <% } else { %>
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i>
                Sélectionnez une date sur la page de planification pour voir les réservations non assignées.
            </div>
        <% } %>
    </main>
</body>
</html>
