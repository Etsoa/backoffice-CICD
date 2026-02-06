<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Nouvelle réservation</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        form { max-width: 500px; margin-top: 20px; }
        label { display: block; margin-top: 10px; font-weight: bold; }
        input, select { width: 100%; padding: 8px; margin-top: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
        .btn { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; margin-top: 15px; font-size: 14px; }
        .btn:hover { background-color: #45a049; }
        .btn-back { background-color: #666; text-decoration: none; color: white; padding: 10px 20px; border-radius: 4px; }
        .btn-back:hover { background-color: #555; }
    </style>
</head>
<body>
    <h1>Nouvelle réservation</h1>

    <form action="${pageContext.request.contextPath}/reservations" method="POST">
        <label for="idClient">ID Client (4 chiffres)</label>
        <input type="text" id="idClient" name="idClient" maxlength="4" pattern="[0-9]{4}" required placeholder="ex: 0001"/>

        <label for="nbPassager">Nombre de passagers</label>
        <input type="number" id="nbPassager" name="nbPassager" min="1" required placeholder="ex: 2"/>

        <label for="dateHeureArrivee">Date et heure d'arrivée</label>
        <input type="datetime-local" id="dateHeureArrivee" name="dateHeureArrivee" required/>

        <label for="idHotel">ID Hôtel</label>
        <input type="number" id="idHotel" name="idHotel" min="1" required placeholder="ex: 1"/>

        <button type="submit" class="btn">Créer la réservation</button>
    </form>

    <br>
    <a href="${pageContext.request.contextPath}/reservations" class="btn-back">Retour à la liste</a>
</body>
</html>
