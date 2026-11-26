package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IMaintenanceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeService vehiculeService;

    @Override
    @Transactional
    public Maintenance create(Maintenance maintenance) {
        if (maintenance.getIdMaintenance() != null) {
            throw new IllegalArgumentException("Une nouvelle maintenance ne doit pas avoir d'identifiant");
        }
        valider(maintenance);
        maintenance.setVehicule(chargerVehicule(maintenance.getVehicule()));
        return maintenanceRepository.save(maintenance);
    }

    @Override
    public Maintenance findById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
    }

    @Override
    public List<Maintenance> findAll() {
        return maintenanceRepository.findAll();
    }

    @Override
    @Transactional
    public Maintenance update(Long id, Maintenance maintenance) {
        Maintenance existante = findById(id);
        valider(maintenance);
        existante.setDateDebut(maintenance.getDateDebut());
        existante.setDateFin(maintenance.getDateFin());
        existante.setDescription(maintenance.getDescription());
        existante.setVehicule(chargerVehicule(maintenance.getVehicule()));
        return maintenanceRepository.save(existante);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Maintenance existante = findById(id);
        maintenanceRepository.delete(existante);
    }

    // Atelier 6
    @Override
    @Transactional
    public Maintenance affecterVehicule(Long idMaintenance, Long idVehicule) {
        Maintenance maintenance = findById(idMaintenance);          // 404 si absente
        Vehicule vehicule = vehiculeService.findById(idVehicule);   // 404 si absent
        maintenance.setVehicule(vehicule);
        return maintenanceRepository.save(maintenance);
    }

    // Atelier 7
    @Override
    public List<Maintenance> findByVehicule(Long idVehicule) {
        vehiculeService.findById(idVehicule);   // 404 si le vehicule n'existe pas
        return maintenanceRepository.findByVehicule_IdVehicule(idVehicule);
    }

    // Atelier 8
    @Override
    public List<Maintenance> findByVehiculeJpql(Long idVehicule) {
        vehiculeService.findById(idVehicule);   // 404 si le vehicule n'existe pas
        return maintenanceRepository.findMaintenancesByVehiculeIdJpql(idVehicule);
    }

    private void valider(Maintenance maintenance) {
        if (maintenance.getDateDebut() == null || maintenance.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de maintenance sont obligatoires");
        }
        if (maintenance.getDateFin().isBefore(maintenance.getDateDebut())) {
            throw new IllegalArgumentException(
                    "La date de fin doit être supérieure ou égale à la date de début");
        }
    }

    /** Remplace le véhicule envoyé ({"idVehicule": 1}) par le véhicule réel : 404 s'il n'existe pas. */
    private Vehicule chargerVehicule(Vehicule vehicule) {
        if (vehicule == null) {
            return null;
        }
        if (vehicule.getIdVehicule() == null) {
            throw new IllegalArgumentException("L'identifiant du véhicule est obligatoire");
        }
        return vehiculeService.findById(vehicule.getIdVehicule());
    }
}
