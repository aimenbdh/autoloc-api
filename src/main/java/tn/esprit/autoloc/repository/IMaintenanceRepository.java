
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

    // Atelier 8 : maintenances d'un vehicule en JPQL.
    // JOIN FETCH charge le vehicule dans la meme requete : le DTO de reponse (qui affiche
    // l'immatriculation) ne declenche pas de requete supplementaire (chargement LAZY).
    @Query("SELECT m FROM Maintenance m JOIN FETCH m.vehicule v WHERE v.idVehicule = :idVehicule")
    List<Maintenance> findMaintenancesByVehiculeIdJpql(@Param("idVehicule") Long idVehicule);
}
