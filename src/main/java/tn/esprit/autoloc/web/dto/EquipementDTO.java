package tn.esprit.autoloc.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tn.esprit.autoloc.domain.Equipement;

@Schema(description = "Équipement (identifiant et libellé)")
public record EquipementDTO(Long idEquipement, String libelle) {

    public static EquipementDTO fromEntity(Equipement e) {
        return new EquipementDTO(e.getIdEquipement(), e.getLibelle());
    }
}
