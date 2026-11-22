
package tn.esprit.autoloc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    private String nom;
    private String ville;
    private String adresse;
    private String telephone;

    // Une agence possède plusieurs véhicules
    @JsonIgnore
    @OneToMany(
            mappedBy = "agence",
            fetch = FetchType.LAZY
    )
    private List<Vehicule> vehicules = new ArrayList<>();

    // Une agence possède plusieurs employés
    @JsonIgnore
    @OneToMany(
            mappedBy = "agence",
            fetch = FetchType.LAZY
    )
    private List<Employe> employes = new ArrayList<>();
}
