package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.service.IEmployeService;

import java.util.List;

@Tag(name = "Employes", description = "Gestion des employés des agences")
@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final IEmployeService employeService;

    @Operation(summary = "Lister les employes")
    @ApiResponse(responseCode = "200", description = "Liste des employes")
    @GetMapping
    public List<Employe> findAll() {
        return employeService.findAll();
    }

    @Operation(summary = "Consulter un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employe trouvé"),
            @ApiResponse(responseCode = "404", description = "Employe introuvable")
    })
    @GetMapping("/{id}")
    public Employe findById(@Parameter(description = "Identifiant du employé") @PathVariable Long id) {
        return employeService.findById(id);
    }

    @Operation(summary = "Créer un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employe créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employe create(@RequestBody Employe employe) {
        return employeService.create(employe);
    }

    @Operation(summary = "Modifier un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employe modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Employe introuvable")
    })
    @PutMapping("/{id}")
    public Employe update(@Parameter(description = "Identifiant du employé") @PathVariable Long id,
                      @RequestBody Employe employe) {
        return employeService.update(id, employe);
    }

    @Operation(summary = "Supprimer un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employe supprimé"),
            @ApiResponse(responseCode = "404", description = "Employe introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du employé") @PathVariable Long id) {
        employeService.deleteById(id);
    }
}
