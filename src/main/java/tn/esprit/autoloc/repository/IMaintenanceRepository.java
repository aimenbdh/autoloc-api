
package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Maintenance;

import java.util.List;

public interface IMaintenanceRepository
        extends JpaRepository<Maintenance, Long> {

    // Atelier 7 : maintenances d'un vehicule, requete derivee du nom de la methode
    List<Maintenance> findByVehicule_IdVehicule(Long idVehicule);
}
