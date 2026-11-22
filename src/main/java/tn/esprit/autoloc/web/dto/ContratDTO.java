package tn.esprit.autoloc.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tn.esprit.autoloc.domain.Contrat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Contrat (sans ses paiements) et identifiant de sa réservation")
public record ContratDTO(
        Long idContrat,
        LocalDate dateSignature,
        BigDecimal montantTotal,
        boolean valide,
        Long idReservation
) {
    public static ContratDTO fromEntity(Contrat c) {
        return new ContratDTO(c.getIdContrat(), c.getDateSignature(), c.getMontantTotal(), c.isValide(),
                c.getReservation() != null ? c.getReservation().getIdReservation() : null);
    }
}
