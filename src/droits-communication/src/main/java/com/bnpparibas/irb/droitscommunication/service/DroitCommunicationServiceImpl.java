package com.bnpparibas.irb.droitscommunication.service;

import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.exception.ReferentielInvalideException;
import com.bnpparibas.irb.droitscommunication.exception.RessourceIntrouvableException;
import com.bnpparibas.irb.droitscommunication.referentiel.ComplementAdresseRepository;
import com.bnpparibas.irb.droitscommunication.referentiel.OrganismeRepository;
import com.bnpparibas.irb.droitscommunication.repository.DroitCommunicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DroitCommunicationServiceImpl implements DroitCommunicationService {

    private final DroitCommunicationRepository repository;
    private final OrganismeRepository organismeRepository;
    private final ComplementAdresseRepository complementAdresseRepository;

    public DroitCommunicationServiceImpl(DroitCommunicationRepository repository,
                                         OrganismeRepository organismeRepository,
                                         ComplementAdresseRepository complementAdresseRepository) {
        this.repository = repository;
        this.organismeRepository = organismeRepository;
        this.complementAdresseRepository = complementAdresseRepository;
    }

    @Override
    @Transactional
    public DroitCommunicationEntity createDroitCommunication(DroitCommunicationEntity entity) {
        validerReferentiels(entity);
        if (entity.getStatut() == null) {
            entity.setStatut(StatutDemande.SOUMISE);
        }
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public DroitCommunicationEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Demande introuvable pour l'id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DroitCommunicationEntity> getAll() {
        return repository.findAll();
    }

    /**
     * Vérifie que les valeurs de référentiels stockés en base existent et sont actives.
     * Les enums sont déjà garantis valides par le binding Spring.
     */
    private void validerReferentiels(DroitCommunicationEntity entity) {
        if (!organismeRepository.existsByCode(entity.getOrganisme())) {
            throw new ReferentielInvalideException(
                    "Organisme inconnu : " + entity.getOrganisme());
        }
        // Le complément d'adresse est facultatif : on ne valide que s'il est renseigné.
        if (StringUtils.hasText(entity.getComplementAdresse())
                && !complementAdresseRepository.existsByCode(entity.getComplementAdresse())) {
            throw new ReferentielInvalideException(
                    "Complément d'adresse inconnu : " + entity.getComplementAdresse());
        }
    }
}
