package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientService clientService;
    private final IVehiculeService vehiculeService;

    @Override
    @Transactional
    public Reservation create(Reservation reservation) {
        if (reservation.getIdReservation() != null) {
            throw new IllegalArgumentException("Une nouvelle réservation ne doit pas avoir d'identifiant");
        }
        valider(reservation);
        chargerClientEtVehicule(reservation, reservation);
        if (reservation.getStatut() == null) {
            reservation.setStatut(StatutReservation.EN_ATTENTE);
        }
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional
    public Reservation update(Long id, Reservation reservation) {
        Reservation existante = findById(id);
        valider(reservation);
        existante.setDateDebut(reservation.getDateDebut());
        existante.setDateFin(reservation.getDateFin());
        if (reservation.getStatut() != null) {
            existante.setStatut(reservation.getStatut());
        }
        chargerClientEtVehicule(reservation, existante);
        return reservationRepository.save(existante);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Reservation existante = findById(id);
        reservationRepository.delete(existante);
    }

    private void valider(Reservation reservation) {
        if (reservation.getDateDebut() == null || reservation.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de réservation sont obligatoires");
        }
        if (reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new IllegalArgumentException(
                    "La date de fin doit être postérieure ou égale à la date de début");
        }
        if (reservation.getClient() == null || reservation.getClient().getIdClient() == null) {
            throw new IllegalArgumentException("Le client de la réservation est obligatoire");
        }
        if (reservation.getVehicule() == null || reservation.getVehicule().getIdVehicule() == null) {
            throw new IllegalArgumentException("Le véhicule de la réservation est obligatoire");
        }
    }

    /** Charge le client et le véhicule réels à partir des identifiants reçus : 404 s'ils n'existent pas. */
    private void chargerClientEtVehicule(Reservation source, Reservation cible) {
        cible.setClient(clientService.findById(source.getClient().getIdClient()));
        cible.setVehicule(vehiculeService.findById(source.getVehicule().getIdVehicule()));
    }
}
