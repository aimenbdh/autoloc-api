package tn.esprit.autoloc.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tn.esprit.autoloc.domain.Vehicule;

import java.util.Comparator;
import java.util.List;

/**
 * Reponse de l'affectation d'un equipement : le vehicule et la liste de ses equipements.
 */
@Schema(description = "Véhicule et liste de ses équipements")
public record VehiculeEquipementsDTO(
        Long idVehicule,
        String immatriculation,
        String marque,
        String modele,
        List<EquipementDTO> equipements
) {
    public static VehiculeEquipementsDTO fromEntity(Vehicule v) {
        List<EquipementDTO> equipements = v.getEquipements().stream()
                .map(EquipementDTO::fromEntity)
                .sorted(Comparator.comparing(EquipementDTO::idEquipement))
                .toList();
        return new VehiculeEquipementsDTO(
                v.getIdVehicule(), v.getImmatriculation(), v.getMarque(), v.getModele(), equipements);
    }
}
