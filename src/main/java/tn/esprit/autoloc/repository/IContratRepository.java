
package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Contrat;

import java.util.List;

public interface IContratRepository
        extends JpaRepository<Contrat, Long> {

    // Atelier 7 : contrats d'un vehicule (Contrat -> Reservation -> Vehicule), requete JPQL
    @Query("SELECT c FROM Contrat c JOIN c.reservation r WHERE r.vehicule.idVehicule = :idVehicule")
    List<Contrat> findContratsByVehiculeId(@Param("idVehicule") Long idVehicule);
}
