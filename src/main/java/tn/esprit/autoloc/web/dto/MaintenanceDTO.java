package tn.esprit.autoloc.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;

import java.time.LocalDate;

/**
 * Reponse de l'affectation d'une maintenance : on n'expose que les champs utiles,
 * le vehicule est resume par son identifiant, son immatriculation et sa marque/modele.
 */
@Schema(description = "Maintenance et véhicule auquel elle est affectée")
public record MaintenanceDTO(
        Long idMaintenance,
        LocalDate dateDebut,
        LocalDate dateFin,
        String description,
        Long idVehicule,
        String immatriculation,
        String vehicule
) {
    public static MaintenanceDTO fromEntity(Maintenance m) {
        Vehicule v = m.getVehicule();
        return new MaintenanceDTO(
                m.getIdMaintenance(),
                m.getDateDebut(),
                m.getDateFin(),
                m.getDescription(),
                v != null ? v.getIdVehicule() : null,
                v != null ? v.getImmatriculation() : null,
                v != null ? v.getMarque() + " " + v.getModele() : null);
    }
}
