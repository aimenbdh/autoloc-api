# Atelier 5 — API REST, tests Postman et documentation Swagger

- Préfixe commun : `server.servlet.context-path=/api` → `http://localhost:8080/api/...`
- Swagger UI : `http://localhost:8080/api/swagger-ui.html` — OpenAPI JSON : `http://localhost:8080/api/v3/api-docs`
- Collection Postman : `docs/postman/AutoLoc.postman_collection.json` + environnement `docs/postman/local.postman_environment.json` (`baseUrl = http://localhost:8080`)

## A. Endpoints

Tous les contrôleurs sont dans `tn.esprit.autoloc.web.controller`, annotés `@RestController`, `@RequestMapping` (sans `/api`), `@RequiredArgsConstructor`, avec un champ `final` typé par l'interface `I...Service`.

| Contrôleur | Verbe et route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|
| ContratController | GET /api/contrats | findAll() | 200 | — |
| ContratController | GET /api/contrats/{id} | findById(id) | 200 | 404 |
| ContratController | POST /api/contrats | create(contrat) | 201 | 400 |
| ContratController | PUT /api/contrats/{id} | update(id, contrat) | 200 | 404, 400 |
| ContratController | DELETE /api/contrats/{id} | deleteById(id) | 204 | 404 |
| PaiementController | GET /api/paiements | findAll() | 200 | — |
| PaiementController | GET /api/paiements/{id} | findById(id) | 200 | 404 |
| PaiementController | POST/PUT/DELETE /api/paiements | — (lecture seule) | — | 405 |
| AgenceController | GET /api/agences | findAll() | 200 | — |
| AgenceController | GET /api/agences/{id} | findById(id) | 200 | 404 |
| AgenceController | POST /api/agences | create(agence) | 201 | 400 (nom vide) |
| AgenceController | PUT /api/agences/{id} | update(id, agence) | 200 | 404, 400 |
| AgenceController | DELETE /api/agences/{id} | deleteById(id) | 204 | 404 |
| EmployeController | GET /api/employes | findAll() | 200 | — |
| EmployeController | GET /api/employes/{id} | findById(id) | 200 | 404 |
| EmployeController | POST /api/employes | create(employe) | 201 | 400 (nom vide) |
| EmployeController | PUT /api/employes/{id} | update(id, employe) | 200 | 404, 400 |
| EmployeController | DELETE /api/employes/{id} | deleteById(id) | 204 | 404 |
| VehiculeController | GET /api/vehicules | findAll() | 200 | — |
| VehiculeController | GET /api/vehicules/{id} | findById(id) | 200 | 404 |
| VehiculeController | POST /api/vehicules | create(vehicule) | 201 | 400 (tarif négatif) |
| VehiculeController | PUT /api/vehicules/{id} | update(id, vehicule) | 200 | 404, 400 |
| VehiculeController | DELETE /api/vehicules/{id} | deleteById(id) | 204 | 404 |
| EquipementController | GET /api/equipements | findAll() | 200 | — |
| EquipementController | GET /api/equipements/{id} | findById(id) | 200 | 404 |
| EquipementController | POST /api/equipements | create(equipement) | 201 | 400 (libellé vide) |
| EquipementController | PUT /api/equipements/{id} | update(id, equipement) | 200 | 404, 400 |
| EquipementController | DELETE /api/equipements/{id} | deleteById(id) | 204 | 404 |
| ClientController | GET /api/clients | findAll() | 200 | — |
| ClientController | GET /api/clients/{id} | findById(id) | 200 | 404 |
| ClientController | POST /api/clients | create(client) | 201 | 400 (nom ou email vide) |
| ClientController | PUT /api/clients/{id} | update(id, client) | 200 | 404, 400 |
| ClientController | DELETE /api/clients/{id} | deleteById(id) | 204 | 404 |
| ReservationController | GET /api/reservations | findAll() | 200 | — |
| ReservationController | GET /api/reservations/{id} | findById(id) | 200 | 404 |
| ReservationController | POST /api/reservations | create(reservation) | 201 | 400 (date de fin antérieure) |
| ReservationController | PUT /api/reservations/{id} | update(id, reservation) | 200 | 404, 400 |
| ReservationController | DELETE /api/reservations/{id} | deleteById(id) | 204 | 404 |
| MaintenanceController | GET /api/maintenances | findAll() | 200 | — |
| MaintenanceController | GET /api/maintenances/{id} | findById(id) | 200 | 404 |
| MaintenanceController | POST /api/maintenances | create(maintenance) | 201 | 400 (date de fin antérieure) |
| MaintenanceController | PUT /api/maintenances/{id} | update(id, maintenance) | 200 | 404, 400 |
| MaintenanceController | DELETE /api/maintenances/{id} | deleteById(id) | 204 | 404 |

Les 404 viennent de `ResourceNotFoundException` et les 400 de `IllegalArgumentException`, toutes deux traduites par `GlobalExceptionHandler` (corps JSON : `statut`, `message`, `horodatage`). Les contrôleurs ne produisent eux-mêmes aucun code d'erreur.

## B. Annotations Jackson

| Association | Annotation retenue | Justification |
|---|---|---|
| Contrat.paiements ↔ Paiement.contrat | `@JsonManagedReference("contrat-paiements")` côté liste, `@JsonBackReference("contrat-paiements")` côté parent | Vraie composition (`cascade = ALL`, `orphanRemoval`) : les paiements sont créés **dans** le JSON du contrat ; Jackson rattache automatiquement chaque paiement à son contrat à la lecture. |
| Contrat.reservation ↔ Reservation.contrat | `@JsonIgnore` sur `Reservation.contrat` (côté inverse) | Le contrat désigne sa réservation par identifiant ; afficher le contrat dans la réservation ferait boucler. |
| Agence.vehicules / Agence.employes ↔ Vehicule.agence / Employe.agence | `@JsonIgnore` sur les listes de `Agence` | Véhicules et employés sont créés seuls et désignent leur agence par `{"agence": {"idAgence": 1}}`. |
| Client.reservations ↔ Reservation.client | `@JsonIgnore` sur `Client.reservations` | Même logique : la réservation est créée seule. **Pas** de `@JsonBackReference` sur `Reservation.client`, sinon le client serait ignoré à la lecture et le POST créerait une réservation sans client. |
| Vehicule.maintenances / Vehicule.reservations | `@JsonIgnore` sur les listes de `Vehicule` | Maintenances et réservations sont créées seules avec `{"vehicule": {"idVehicule": 1}}`. |
| Equipement.vehicules ↔ Vehicule.equipements | `@JsonIgnore` sur `Equipement.vehicules` (côté `mappedBy`) | Côté propriétaire (`Vehicule.equipements`) laissé visible. |

Proxys LAZY : `spring.jackson.serialization.fail-on-empty-beans=false` (palliatif provisoire, remplacé par les DTO au chapitre 10).

**Différence `@JsonBackReference` / `@JsonIgnore`** : `@JsonBackReference` n'est pas écrit en sortie mais est **renseigné à la lecture** à partir du parent qui contient la `@JsonManagedReference` (le paiement reçoit automatiquement son contrat). `@JsonIgnore` supprime l'attribut dans les deux sens : il n'est ni écrit ni lu.

## C. Postman et Swagger

| Outil | Ce qu'il apporte | Exemple concret dans l'atelier |
|---|---|---|
| Postman | Tester : requêtes rejouables, variables (`{{baseUrl}}`, `{{contratId}}`), assertions automatiques (`pm.test`), scénarios enchaînés. | Scénario de cascade : POST d'un contrat avec 2 paiements → 201, puis GET `/api/paiements` montre les 2 paiements, puis DELETE du contrat → 204 et GET du paiement → 404. Vérification aussi du 405 sur POST `/api/paiements`. |
| Swagger UI | Documenter et explorer : contrat OpenAPI généré à partir du code, toujours synchronisé, essayable (« Try it out »). | Groupes « Contrats », « Paiements »… avec résumés et codes 200/201/204/400/404 documentés ; section *Schemas* déduite des entités. |

## D. Anomalies SonarQube for IDE

> À compléter après l'analyse : clic droit sur `web.controller` > « Analyze with SonarQube for IDE ».

| Règle | Explication | Correction |
|---|---|---|
|  |  |  |
|  |  |  |

## E. Questions

1. **Composant qui reçoit les requêtes** : le `DispatcherServlet`, point d'entrée unique. Il interroge le `HandlerMapping`, qui retrouve la méthode du contrôleur à partir du verbe HTTP et de l'URL (`@RequestMapping` + `@GetMapping`…).
2. **`ContratController` dépend de `IContratService`** : le contrôleur reste mince (aucune logique métier, aucun accès direct à la base) ; dépendre de l'interface découple le contrôleur de l'implémentation (remplaçable, testable avec un mock) et respecte l'organisation en couches. Passer par le repository contournerait les règles métier du service.
3. **201 / 204 / 404** : un POST crée une ressource → *201 Created* (avec la ressource créée) ; un DELETE réussi n'a rien à renvoyer → *204 No Content*. Une ressource absente est une erreur du client (identifiant inexistant) → *404*, alors que *500* signale une panne du serveur ; `ResourceNotFoundException` est traduite en 404 par le `GlobalExceptionHandler`.
4. **`PaiementController` en lecture seule** : un paiement n'existe que dans un contrat (composition, `cascade = ALL`) ; il est créé, modifié et supprimé via son contrat. Un POST `/api/paiements` reçoit **405 Method Not Allowed**, car aucune méthode n'est associée à ce verbe sur cette route.
5. **Risques d'exposer les entités JPA** : couplage fort entre API et base, champs internes visibles, boucles de sérialisation, proxys LAZY, possibilité de modifier des champs non prévus (sur-affectation). `Contrat.paiements` utilise `@JsonManagedReference` car c'est une composition : les paiements doivent être lus dans le JSON du contrat et rattachés à lui ; `Agence.vehicules` utilise `@JsonIgnore` car un véhicule est créé seul et désigne son agence par identifiant : la liste n'a pas à être lue ni écrite.
