
package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Vehicule;

import java.util.List;

public interface IVehiculeRepository
        extends JpaRepository<Vehicule, Long> {

    // Atelier 7 : vehicules d'un contrat (Contrat -> Reservation -> Vehicule), requete JPQL
    @Query("SELECT r.vehicule FROM Contrat c JOIN c.reservation r WHERE c.idContrat = :idContrat")
    List<Vehicule> findVehiculesByContratId(@Param("idContrat") Long idContrat);

    // Atelier 7 : vehicules d'une maintenance, requete derivee du nom de la methode
    List<Vehicule> findByMaintenances_IdMaintenance(Long idMaintenance);

    // Atelier 8 : vehicules d'une maintenance en JPQL (on navigue dans les entites Java)
    @Query("SELECT m.vehicule FROM Maintenance m WHERE m.idMaintenance = :idMaintenance")
    List<Vehicule> findVehiculesByMaintenanceIdJpql(@Param("idMaintenance") Long idMaintenance);

    // Atelier 8 : vehicules d'une maintenance en SQL natif (on travaille sur les tables et colonnes MySQL)
    @Query(value = """
            SELECT v.*
            FROM vehicule v
            JOIN maintenance m ON m.id_vehicule = v.id_vehicule
            WHERE m.id_maintenance = :idMaintenance
            """, nativeQuery = true)
    List<Vehicule> findVehiculesByMaintenanceIdSql(@Param("idMaintenance") Long idMaintenance);
}
