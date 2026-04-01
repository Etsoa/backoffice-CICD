Voici le plan de mise en conformité avec tes règles, et les écarts actuels du code :

## Écarts majeurs à corriger
- Déclenchement de regroupement : le code ne déclenche pas sur “retour véhicule” ; il parcourt les réservations triées par heure.
- Intervalle : aujourd’hui basé sur la première résa du groupe (+délai). Pas de logique “retour véhicule” + “pas de déclenchement si aucun non assigné / résa dans l’intervalle”.
- Priorité non-assignés : pas de tri par ancienneté (date/heure initiales). Phase unique (reportées+vols) au lieu de Phase 1 non assignés puis Phase 2 vols.
- Traitement par réservation : on remplit véhicule par véhicule ; pas “pour chaque réservation, choisir le véhicule optimal et la placer”.
- Départ véhicule plein avec non assignés déclenché par retour : non implémenté.
- Cas “aucun vol dans l’intervalle mais non assignés présents” : non implémenté (départ immédiat).
- Choix véhicule “capacity minimal ≥ besoin” + tie-break trajets/carburant : actuellement priorité véhicule déjà utilisé puis capacité décroissante (inverse de demandé).
- Multi-véhicules dans même intervalle : le code traite tous les véhicules dispo, mais l’ordre et la stratégie ne correspondent pas à “par réservation” + véhicules reconsidérés à chaque réservation.

## Plan de mise en œuvre (par étapes)
1) Déclenchement des regroupements
   - Introduire une timeline combinée “retours véhicules” + “réservations” (triée par heure).
   - Ne déclencher un regroupement que s’il y a au moins une résa ou non assigné dans l’intervalle [heure_event, heure_event+delai].
   - Si aucun véhicule dispo pour l’intervalle, reporter tout (déjà présent) mais s’assurer que l’événement “retour véhicule” suivant peut déclencher.
2) Construction des groupes
   - Intervalle basé sur l’événement déclencheur (vol ou retour véhicule).
   - Agréger : non assignés en attente + résas dont l’heure est dans l’intervalle.
3) Priorisation des réservations dans le groupe
   - Phase 1 : non assignés, tri par nombre desc, puis ancienneté (date/heure), puis référence.
   - Phase 2 : résas “vols” du groupe, tri par nombre desc, puis heure, puis ref.
4) Affectation “une réservation à la fois”
   - Boucle sur les réservations (phase 1 puis phase 2) : pour chaque résa, re-calculer le véhicule optimal.
   - Véhicule optimal : capacité libre minimale ≥ besoin ; tie-break : moins de trajets ; puis carburant (Diesel > Essence) ; puis plus petite capacité si égalité.
   - Si aucun véhicule adapté : splitter (selon règle 7), sinon laisser non assigné pour report.
5) Départs
   - Regroupement déclenché par retour + non assignés : si un véhicule devient plein → départ immédiat.
   - Si non assignés présents et aucun vol dans l’intervalle → départ immédiat à l’heure de retour.
   - Sinon, départ commun = max(heure vols du groupe).
6) Multi-véhicules dans l’intervalle
   - Si d’autres véhicules reviennent dans l’intervalle, ils rejoignent le même groupe (pas de nouveau groupe), mais on reste sur l’algorithme “par réservation, choix véhicule optimal à chaque itération”.
7) Splits et reports
   - Si pas de véhicule suffisant : split autorisé immédiat.
   - Report des restes et non assignés au groupe suivant (déjà en place, à garder).






⭕ Intervalle basé sur l’événement déclencheur (vol ou retour véhicule) plutôt que sur la première résa rencontrée.
⭕ Départ immédiat si regroupement déclenché par retour + non assignés et le véhicule devient plein.
⭕ Départ immédiat si non assignés présents et aucun vol dans l’intervalle (cas “aucun vol”).
⭕ Multi-véhicules revenant dans l’intervalle : ils rejoignent le même groupe, mais la logique de déclenchement par retour n’est pas encore en place.