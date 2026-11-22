package tn.esprit.autoloc.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;

import java.math.BigDecimal;

@Schema(description = "Véhicule (champs principaux)")
public record VehiculeDTO(
        Long idVehicule,
        String immatriculation,
        String marque,
        String modele,
        CategorieVehicule categorie,
        BigDecimal tarifJournalier,
        StatutVehicule statut
) {
    public static VehiculeDTO fromEntity(Vehicule v) {
        return new VehiculeDTO(v.getIdVehicule(), v.getImmatriculation(), v.getMarque(),
                v.getModele(), v.getCategorie(), v.getTarifJournalier(), v.getStatut());
    }
}
