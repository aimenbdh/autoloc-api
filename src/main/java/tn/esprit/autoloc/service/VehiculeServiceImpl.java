package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IEquipementService equipementService;
    private final IAgenceService agenceService;
    // repositories utilises uniquement pour verifier l'existence (evite une dependance circulaire entre services)
    private final IContratRepository contratRepository;
    private final IMaintenanceRepository maintenanceRepository;

    @Override
    @Transactional
    public Vehicule create(Vehicule vehicule) {
        if (vehicule.getIdVehicule() != null) {
            throw new IllegalArgumentException("Un nouveau véhicule ne doit pas avoir d'identifiant");
        }
        valider(vehicule);
        if (vehicule.getStatut() == null) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
        }
        vehicule.setAgence(chargerAgence(vehicule.getAgence()));
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    @Transactional
    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existant = findById(id);
        valider(vehicule);
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setCategorie(vehicule.getCategorie());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        if (vehicule.getStatut() != null) {
            existant.setStatut(vehicule.getStatut());
        }
        existant.setAgence(chargerAgence(vehicule.getAgence()));
        return vehiculeRepository.save(existant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Vehicule existant = findById(id);
        vehiculeRepository.delete(existant);
    }

    // Atelier 6
    @Override
    @Transactional
    public Vehicule affecterEquipement(Long idVehicule, Long idEquipement) {
        Vehicule vehicule = findById(idVehicule);                            // 404 si absent
        Equipement equipement = equipementService.findById(idEquipement);   // 404 si absent
        boolean dejaAffecte = vehicule.getEquipements().stream()
                .anyMatch(e -> e.getIdEquipement().equals(idEquipement));
        if (dejaAffecte) {
            throw new IllegalArgumentException(
                    "L'équipement " + idEquipement + " est déjà affecté au véhicule " + idVehicule);
        }
        // Vehicule est le cote proprietaire de la relation ManyToMany
        vehicule.getEquipements().add(equipement);
        return vehiculeRepository.save(vehicule);
    }

    // Atelier 7
    @Override
    public List<Vehicule> findByContrat(Long idContrat) {
        if (!contratRepository.existsById(idContrat)) {
            throw new ResourceNotFoundException("Contrat", idContrat);
        }
        return vehiculeRepository.findVehiculesByContratId(idContrat);
    }

    @Override
    public List<Vehicule> findByMaintenance(Long idMaintenance) {
        if (!maintenanceRepository.existsById(idMaintenance)) {
            throw new ResourceNotFoundException("Maintenance", idMaintenance);
        }
        return vehiculeRepository.findByMaintenances_IdMaintenance(idMaintenance);
    }

    // Atelier 8
    @Override
    public List<Vehicule> findByMaintenanceJpql(Long idMaintenance) {
        verifierMaintenance(idMaintenance);
        return vehiculeRepository.findVehiculesByMaintenanceIdJpql(idMaintenance);
    }

    @Override
    public List<Vehicule> findByMaintenanceSql(Long idMaintenance) {
        verifierMaintenance(idMaintenance);
        return vehiculeRepository.findVehiculesByMaintenanceIdSql(idMaintenance);
    }

    private void verifierMaintenance(Long idMaintenance) {
        if (!maintenanceRepository.existsById(idMaintenance)) {
            throw new ResourceNotFoundException("Maintenance", idMaintenance);
        }
    }

    private void valider(Vehicule vehicule) {
        if (vehicule.getImmatriculation() == null || vehicule.getImmatriculation().isBlank()) {
            throw new IllegalArgumentException("L'immatriculation est obligatoire");
        }
        if (vehicule.getMarque() == null || vehicule.getMarque().isBlank()
                || vehicule.getModele() == null || vehicule.getModele().isBlank()) {
            throw new IllegalArgumentException("La marque et le modèle sont obligatoires");
        }
        if (vehicule.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie du véhicule est obligatoire");
        }
        if (vehicule.getTarifJournalier() == null
                || vehicule.getTarifJournalier().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Le tarif journalier est obligatoire et ne peut pas être négatif");
        }
    }

    /** Remplace l'agence envoyée ({"idAgence": 1}) par l'agence réelle : 404 si elle n'existe pas. */
    private Agence chargerAgence(Agence agence) {
        if (agence == null) {
            return null;
        }
        if (agence.getIdAgence() == null) {
            throw new IllegalArgumentException("L'identifiant de l'agence est obligatoire");
        }
        return agenceService.findById(agence.getIdAgence());
    }
}
