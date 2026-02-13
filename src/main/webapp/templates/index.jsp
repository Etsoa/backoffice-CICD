<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>BackOffice - Accueil</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5; }
        .container { max-width: 800px; margin: 50px auto; padding: 20px; }
        h1 { color: #333; text-align: center; margin-bottom: 40px; }
        .modules { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
        .module { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); text-align: center; transition: transform 0.2s, box-shadow 0.2s; }
        .module:hover { transform: translateY(-5px); box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
        .module h2 { margin: 0 0 15px 0; color: #333; }
        .module p { color: #666; margin-bottom: 20px; font-size: 14px; }
        .module a { display: inline-block; padding: 10px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; }
        .module a:hover { background-color: #45a049; }
        .module.reservations a { background-color: #4CAF50; }
        .module.reservations a:hover { background-color: #45a049; }
        .module.vehicules a { background-color: #2196F3; }
        .module.vehicules a:hover { background-color: #1976D2; }
        .module.hotels a { background-color: #ff9800; }
        .module.hotels a:hover { background-color: #f57c00; }
        .emoji { font-size: 48px; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🏢 BackOffice - Gestion</h1>
        
        <div class="modules">
            <div class="module reservations">
                <div class="emoji">📅</div>
                <h2>Réservations</h2>
                <p>Gérer les réservations des clients</p>
                <a href="${pageContext.request.contextPath}/reservations">Accéder</a>
            </div>
            
            <div class="module vehicules">
                <div class="emoji">🚗</div>
                <h2>Véhicules</h2>
                <p>Gérer le parc de véhicules</p>
                <a href="${pageContext.request.contextPath}/vehicules">Accéder</a>
            </div>
            
            <div class="module hotels">
                <div class="emoji">🏨</div>
                <h2>Hôtels</h2>
                <p>Gérer les hôtels partenaires</p>
                <a href="${pageContext.request.contextPath}/hotels">Accéder</a>
            </div>
        </div>
    </div>
</body>
</html>
