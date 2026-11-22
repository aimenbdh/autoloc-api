package tn.esprit.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEmployeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;
    private final IAgenceService agenceService;

    @Override
    @Transactional
    public Employe create(Employe employe) {
        if (employe.getIdEmploye() != null) {
            throw new IllegalArgumentException("Un nouvel employé ne doit pas avoir d'identifiant");
        }
        valider(employe);
        employe.setAgence(chargerAgence(employe.getAgence()));
        return employeRepository.save(employe);
    }

    @Override
    public Employe findById(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }

    @Override
    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    @Override
    @Transactional
    public Employe update(Long id, Employe employe) {
        Employe existant = findById(id);
        valider(employe);
        existant.setNom(employe.getNom());
        existant.setPrenom(employe.getPrenom());
        existant.setRole(employe.getRole());
        existant.setAgence(chargerAgence(employe.getAgence()));
        return employeRepository.save(existant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Employe existant = findById(id);
        employeRepository.delete(existant);
    }

    private void valider(Employe employe) {
        if (employe.getNom() == null || employe.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'employé est obligatoire");
        }
    }

    /** Remplace l'agence envoyée ({"idAgence": 1}) par l'agence réelle : 404 si elle n'existe pas. */
    private Agence chargerAgence(Agence agence) {
        if (agence == null) {
            return null;
        }
        if (agence.getIdAgence() == null) {
            throw new IllegalArgumentException("L'identifiant de l'agence est obligatoire");
        }
        return agenceService.findById(agence.getIdAgence());
    }
}
