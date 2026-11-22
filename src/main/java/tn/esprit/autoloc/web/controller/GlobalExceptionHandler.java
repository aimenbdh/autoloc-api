package tn.esprit.autoloc.web.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Traduit les exceptions de la couche service en reponses HTTP lisibles :
 * ResourceNotFoundException -> 404, IllegalArgumentException / validation / JSON illisible -> 400,
 * violation de contrainte en base (doublon, suppression bloquee par une cle etrangere) -> 409.
 * Corps JSON : statut, message, horodatage.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return construire(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return construire(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Atelier 6 : erreurs du validator (@Valid sur un DTO) -> 400 avec le detail par champ
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            erreurs.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        ResponseEntity<Map<String, Object>> reponse =
                construire(HttpStatus.BAD_REQUEST, "Données invalides");
        reponse.getBody().put("erreurs", erreurs);
        return reponse;
    }

    // JSON mal forme, date hors format AAAA-MM-JJ, valeur d'enumeration inconnue...
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonIllisible(HttpMessageNotReadableException ex) {
        return construire(HttpStatus.BAD_REQUEST,
                "Corps JSON illisible : vérifiez la syntaxe, les dates (AAAA-MM-JJ) et les valeurs d'énumération");
    }

    // Contrainte de la base : valeur unique deja utilisee, ou element encore reference par un autre
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflit(DataIntegrityViolationException ex) {
        return construire(HttpStatus.CONFLICT,
                "Opération impossible : valeur déjà utilisée ou élément encore lié à d'autres données");
    }

    private ResponseEntity<Map<String, Object>> construire(HttpStatus statut, String message) {
        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("statut", statut.value());
        corps.put("message", message);
        corps.put("horodatage", LocalDateTime.now().withNano(0).toString());
        return ResponseEntity.status(statut).body(corps);
    }
}
