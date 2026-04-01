# Règles métier de planification (Sprint 7)

## Regroupements temporels
- Intervalle : [heure de la première réservation du groupe, heure + délai_attente].
- Les réservations reportées d’un groupe précédent sont ajoutées sans contrainte d’heure (elles attendent déjà).
- Heure de départ commune du groupe = heure la plus tardive parmi les réservations du groupe (tous les véhicules partent ensemble à cette heure).

## Disponibilité véhicules
- Un véhicule est disponible si son heureRetour précédente (ou son heureDisponibilite métier) est ≤ heureDepartGroupe + delai_attente ; sinon il est indisponible pour ce regroupement.
- Un même véhicule peut être réutilisé sur un groupe ultérieur dès que son heureRetour est passée (rotations multiples dans la journée).
- Si aucun véhicule n’est disponible : toutes les réservations du groupe sont marquées non assignées et reportées vers le groupe suivant (pas de décalage d’heure du groupe).

## Priorité des réservations dans un groupe
1) Restes/reportées du groupe précédent en premier.
2) Puis par nombre de passagers décroissant.
3) orderIndex fixé après tri initial (sert au premier choix de chaque véhicule).

## Sélection des véhicules
- Tri : véhicules déjà utilisés (trajets > 0) priorisés, puis capacité décroissante.
- Choix “meilleur véhicule” pour une réservation entière : capacité ≥ besoin, le plus proche en taille, puis moins de trajets, Diesel avant Essence, sinon aléa.

## Remplissage et splits
- Premier pick d’un véhicule : réservation prioritaire (ordre + pax).
- Remplissage des places restantes :
  - Exact fit avant tout.
  - Sinon écart de capacité minimal.
  - Restes créés dans le même groupe priorisés pour boucher les trous.
  - Réservations déjà entamées ensuite.
  - Puis plus petite taille, puis référence pour stabilité.
- Si besoin > capacité restante : split. La partie non embarquée devient un reste marqué "resteInCurrentGroup".

## Propagation des non-assignées / restes
- Les restes créés sont ajoutés aux non-assignées du groupe et remplacent l’original dans la liste principale.
- Les non-assignées (y compris restes) sont collectées par référence d’objet et reportées au groupe suivant.
- Nettoyage en fin de génération : on retire des non-assignées toute réservation finalement assignée ailleurs.

## Itinéraires
- Après affectation, on calcule l’itinéraire de chaque véhicule avec l’heure de départ commune du groupe.
- Le retour à l’aéroport met à jour l’heure de retour du véhicule pour les groupes suivants.

## Principes de priorité globaux
1) Respect de l’intervalle de regroupement et départ commun du groupe (heure max des résas du groupe).
2) Restes/reportées avant nouvelles réservations dans le groupe.
3) Remplissage optimal : exact fit > écart minimal > terminer les splits > petites tailles.
4) Aucun véhicule dispo : tout est reporté, rien n’est perdu.
5) Les restes sont re-considérés à chaque groupe suivant comme des réservations normales.
