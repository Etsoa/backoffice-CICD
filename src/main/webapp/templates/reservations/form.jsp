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
        <label for="reference">Référence</label>
        <input type="number" id="reference" name="reference" required placeholder="ex: 4631"/>

        <label for="nombre">Nombre de personnes</label>
        <input type="number" id="nombre" name="nombre" min="1" required placeholder="ex: 2"/>

        <label for="date">Date</label>
        <input type="date" id="date" name="date" required/>

        <label for="heure">Heure</label>
        <input type="time" id="heure" name="heure" required/>

        <label for="hotel">ID Hôtel</label>
        <input type="number" id="hotel" name="hotel" min="1" required placeholder="ex: 1"/>

        <button type="submit" class="btn">Créer la réservation</button>
    </form>

    <br>
    <a href="${pageContext.request.contextPath}/reservations" class="btn-back">Retour à la liste</a>
</body>
</html>
