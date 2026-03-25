ALTER TABLE reservation ADD COLUMN client VARCHAR(255);
-- Assurer la présence du champ d'heure de disponibilité véhicule
ALTER TABLE vehicule
	ADD COLUMN IF NOT EXISTS heure_disponibilite TIME NOT NULL DEFAULT '00:00:00';