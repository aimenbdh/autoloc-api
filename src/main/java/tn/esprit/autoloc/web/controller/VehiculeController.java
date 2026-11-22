package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IVehiculeService;

import java.util.List;

@Tag(name = "Vehicules", description = "Gestion du parc de véhicules")
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @Operation(summary = "Lister les vehicules")
    @ApiResponse(responseCode = "200", description = "Liste des vehicules")
    @GetMapping
    public List<Vehicule> findAll() {
        return vehiculeService.findAll();
    }

    @Operation(summary = "Consulter un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Vehicule introuvable")
    })
    @GetMapping("/{id}")
    public Vehicule findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @Operation(summary = "Créer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicule créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicule create(@RequestBody Vehicule vehicule) {
        return vehiculeService.create(vehicule);
    }

    @Operation(summary = "Modifier un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicule modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Vehicule introuvable")
    })
    @PutMapping("/{id}")
    public Vehicule update(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
                      @RequestBody Vehicule vehicule) {
        return vehiculeService.update(id, vehicule);
    }

    @Operation(summary = "Supprimer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicule supprimé"),
            @ApiResponse(responseCode = "404", description = "Vehicule introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }
}
