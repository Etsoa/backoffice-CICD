<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BackOffice - Accueil</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Inter', 'Segoe UI', sans-serif; background-color: #f0f4f8; min-height: 100vh; }

        /* Sidebar */
        .sidebar { position: fixed; left: 0; top: 0; bottom: 0; width: 260px; background: #ffffff; border-right: 1px solid #e2e8f0; display: flex; flex-direction: column; z-index: 100; }
        .sidebar-brand { padding: 24px 20px; border-bottom: 1px solid #e2e8f0; display: flex; align-items: center; gap: 12px; }
        .sidebar-brand i { font-size: 24px; color: #1a73e8; }
        .sidebar-brand span { font-size: 18px; font-weight: 700; color: #1e293b; }
        .sidebar-nav { padding: 16px 12px; flex: 1; }
        .sidebar-nav a { display: flex; align-items: center; gap: 12px; padding: 12px 16px; color: #64748b; text-decoration: none; border-radius: 8px; font-size: 14px; font-weight: 500; transition: all 0.2s; margin-bottom: 4px; }
        .sidebar-nav a:hover, .sidebar-nav a.active { background: #e8f0fe; color: #1a73e8; }
        .sidebar-nav a i { width: 20px; text-align: center; font-size: 16px; }

        /* Main */
        .main { margin-left: 260px; padding: 32px 40px; }
        .page-header { margin-bottom: 32px; }
        .page-header h1 { font-size: 26px; font-weight: 700; color: #1e293b; margin-bottom: 4px; }
        .page-header p { color: #64748b; font-size: 14px; }

        /* Module cards */
        .modules { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 24px; }
        .module-card { background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; padding: 32px 28px; transition: all 0.25s ease; position: relative; overflow: hidden; }
        .module-card:hover { transform: translateY(-4px); box-shadow: 0 12px 24px rgba(26,115,232,0.12); border-color: #1a73e8; }
        .module-icon { width: 56px; height: 56px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-bottom: 20px; }
        .module-icon.blue { background: #e8f0fe; color: #1a73e8; }
        .module-icon.green { background: #e6f4ea; color: #34a853; }
        .module-icon.orange { background: #fef3e2; color: #ea8600; }
        .module-icon.purple { background: #f3e8fd; color: #9334e6; }
        .module-card h2 { font-size: 18px; font-weight: 600; color: #1e293b; margin-bottom: 8px; }
        .module-card p { color: #64748b; font-size: 13px; line-height: 1.5; margin-bottom: 24px; }
        .module-card .btn { display: inline-flex; align-items: center; gap: 8px; padding: 10px 20px; background: #1a73e8; color: #ffffff; text-decoration: none; border-radius: 8px; font-size: 13px; font-weight: 600; transition: background 0.2s; }
        .module-card .btn:hover { background: #1557b0; }
        .module-card .btn i { font-size: 12px; }

        /* Stats row */
        .stats-row { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 20px; margin-bottom: 32px; }
        .stat-card { background: #ffffff; border-radius: 10px; border: 1px solid #e2e8f0; padding: 20px 24px; }
        .stat-card .stat-label { font-size: 12px; color: #64748b; font-weight: 500; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 8px; }
        .stat-card .stat-value { font-size: 28px; font-weight: 700; color: #1e293b; }

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
            <a href="${pageContext.request.contextPath}/" class="active">
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
            <a href="${pageContext.request.contextPath}/planification">
                <i class="fas fa-route"></i> Planification
            </a>
        </nav>
    </aside>

    <main class="main">
        <div class="page-header">
            <h1>Tableau de bord</h1>
            <p>Bienvenue sur le BackOffice de gestion des transferts</p>
        </div>

        <div class="modules">
            <div class="module-card">
                <div class="module-icon blue">
                    <i class="fas fa-calendar-check"></i>
                </div>
                <h2>Reservations</h2>
                <p>Consulter, creer et gerer les reservations des clients pour les transferts</p>
                <a href="${pageContext.request.contextPath}/reservations" class="btn">
                    Acceder <i class="fas fa-arrow-right"></i>
                </a>
            </div>

            <div class="module-card">
                <div class="module-icon green">
                    <i class="fas fa-car"></i>
                </div>
                <h2>Vehicules</h2>
                <p>Gerer le parc de vehicules, les types de carburant et les capacites</p>
                <a href="${pageContext.request.contextPath}/vehicules" class="btn">
                    Acceder <i class="fas fa-arrow-right"></i>
                </a>
            </div>

            <div class="module-card">
                <div class="module-icon purple">
                    <i class="fas fa-route"></i>
                </div>
                <h2>Planification</h2>
                <p>Generer le planning optimal des vehicules pour les transferts aeroport</p>
                <a href="${pageContext.request.contextPath}/planification" class="btn">
                    Acceder <i class="fas fa-arrow-right"></i>
                </a>
            </div>
        </div>
    </main>
</body>
</html>
