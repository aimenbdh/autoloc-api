
package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Maintenance;
import java.util.List;

public interface IMaintenanceService {

    Maintenance create(Maintenance maintenance);

    Maintenance findById(Long id);

    List<Maintenance> findAll();

    Maintenance update(Long id, Maintenance maintenance);

    void deleteById(Long id);

    // Atelier 6 : affecter une maintenance a un vehicule
    Maintenance affecterVehicule(Long idMaintenance, Long idVehicule);

    // Atelier 7
    List<Maintenance> findByVehicule(Long idVehicule);
}
