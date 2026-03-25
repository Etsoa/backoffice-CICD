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
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isEdit ? "Modifier" : "Nouvelle" %> reservation</title>
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
            <h1><i class="fas fa-calendar-check" style="color:#1a73e8;"></i> <%= isEdit ? "Modifier la" : "Nouvelle" %> reservation</h1>
        </div>

        <div class="form-card">
            <div class="form-card-header">
                <h2><i class="fas fa-<%= isEdit ? "edit" : "plus-circle" %>"></i> <%= isEdit ? "Modifier les informations" : "Remplir les informations" %></h2>
            </div>
            <div class="form-card-body">
                <form action="${pageContext.request.contextPath}/<%= isEdit ? "reservations/update" : "reservations" %>" method="POST">
                    <% if (isEdit && reservation != null) { %>
                        <input type="hidden" name="id" value="<%= reservation.getId() %>"/>
                    <% } %>

                    <div class="form-group">
                        <label for="reference"><i class="fas fa-hashtag"></i> Reference</label>
                        <input type="number" id="reference" name="reference" class="form-control" required placeholder="ex: 4631"
                               value="<%= isEdit && reservation != null ? reservation.getReference() : "" %>"/>
                    </div>

                    <div class="form-group">
                        <label for="nombre"><i class="fas fa-users"></i> Nombre de personnes</label>
                        <input type="number" id="nombre" name="nombre" class="form-control" min="1" required placeholder="ex: 2"
                               value="<%= isEdit && reservation != null ? reservation.getNombre() : "" %>"/>
                    </div>

                    <div class="form-group">
                        <label for="date"><i class="fas fa-calendar"></i> Date</label>
                        <input type="date" id="date" name="date" class="form-control" required
                               value="<%= isEdit && reservation != null ? reservation.getDate() : "" %>"/>
                    </div>

                    <div class="form-group">
                        <label for="heure"><i class="fas fa-clock"></i> Heure</label>
                        <input type="time" id="heure" name="heure" class="form-control" required
                               value="<%= isEdit && reservation != null ? reservation.getHeure().toString().substring(0, 5) : "" %>"/>
                    </div>

                    <div class="form-group">
                        <label for="hotel"><i class="fas fa-hotel"></i> Hotel</label>
                        <select id="hotel" name="hotel" class="form-control" required>
                            <option value="">-- Selectionner un hotel --</option>
                            <% if (hotels != null) {
                                for (Hotel h : hotels) {
                                    boolean selected = isEdit && reservation != null && reservation.getHotel() == h.getId();
                            %>
                            <option value="<%= h.getId() %>" <%= selected ? "selected" : "" %>><%= h.getLibelle() %></option>
                            <% }} %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="client"><i class="fas fa-user"></i> Nom du client</label>
                        <input type="text" id="client" name="client" class="form-control" placeholder="Nom du client"
                               value="<%= isEdit && reservation != null && reservation.getClient() != null ? reservation.getClient() : "" %>"/>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-<%= isEdit ? "save" : "plus" %>"></i> <%= isEdit ? "Mettre a jour" : "Creer" %>
                        </button>
                        <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary">
                            <i class="fas fa-times"></i> Annuler
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</body>
</html>
