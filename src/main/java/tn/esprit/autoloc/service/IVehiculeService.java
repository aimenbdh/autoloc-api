
package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;
import java.util.List;

public interface IVehiculeService {

    Vehicule create(Vehicule vehicule);

    Vehicule findById(Long id);

    List<Vehicule> findAll();

    Vehicule update(Long id, Vehicule vehicule);

    void deleteById(Long id);

    // Atelier 6 : affecter un equipement a un vehicule
    Vehicule affecterEquipement(Long idVehicule, Long idEquipement);

    // Atelier 7
    List<Vehicule> findByContrat(Long idContrat);

    List<Vehicule> findByMaintenance(Long idMaintenance);

    // Atelier 8
    List<Vehicule> findByMaintenanceJpql(Long idMaintenance);

    List<Vehicule> findByMaintenanceSql(Long idMaintenance);
}
