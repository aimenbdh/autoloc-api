package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IReservationService reservationService;

    @Override
    @Transactional
    public Contrat create(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        valider(contrat);
        contrat.setReservation(chargerReservation(contrat.getReservation()));
        // Synchroniser les deux cotes de la composition Contrat -> Paiement
        for (Paiement paiement : contrat.getPaiements()) {
            validerPaiement(paiement);
            paiement.setContrat(contrat);
        }
        return contratRepository.save(contrat);
    }

    @Override
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    @Transactional
    public Contrat update(Long id, Contrat contrat) {
        Contrat existant = findById(id);
        valider(contrat);
        // seuls les attributs modifiables sont recopies : les paiements restent inchanges
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        return contratRepository.save(existant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Contrat existant = findById(id);
        contratRepository.delete(existant);
    }

    // Atelier 7
    @Override
    public List<Contrat> findByVehicule(Long idVehicule) {
        if (!vehiculeRepository.existsById(idVehicule)) {
            throw new ResourceNotFoundException("Vehicule", idVehicule);
        }
        return contratRepository.findContratsByVehiculeId(idVehicule);
    }

    private void valider(Contrat contrat) {
        if (contrat.getMontantTotal() == null
                || contrat.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Le montant total est obligatoire et ne peut pas être négatif");
        }
    }

    private void validerPaiement(Paiement paiement) {
        if (paiement.getMontant() == null || paiement.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant d'un paiement est obligatoire et doit être positif");
        }
    }

    /** Charge la réservation réelle (404 si absente) et refuse une réservation qui a déjà un contrat. */
    private Reservation chargerReservation(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        if (reservation.getIdReservation() == null) {
            throw new IllegalArgumentException("L'identifiant de la réservation est obligatoire");
        }
        Reservation existante = reservationService.findById(reservation.getIdReservation());
        if (existante.getContrat() != null) {
            throw new IllegalArgumentException(
                    "La réservation " + existante.getIdReservation() + " possède déjà un contrat");
        }
        return existante;
    }
}
