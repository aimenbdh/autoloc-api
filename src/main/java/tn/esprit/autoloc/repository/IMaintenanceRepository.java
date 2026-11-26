
package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Maintenance;

import java.util.List;

public interface IMaintenanceRepository
        extends JpaRepository<Maintenance, Long> {

    // Atelier 7 : maintenances d'un vehicule, requete derivee du nom de la methode
    List<Maintenance> findByVehicule_IdVehicule(Long idVehicule);

    // Atelier 8 : maintenances d'un vehicule en JPQL
    @Query("SELECT m FROM Maintenance m WHERE m.vehicule.idVehicule = :idVehicule")
    List<Maintenance> findMaintenancesByVehiculeIdJpql(@Param("idVehicule") Long idVehicule);
}
