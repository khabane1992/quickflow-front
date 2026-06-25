package com.bnpparibas.irb.droitscommunication.service;

import com.bnpparibas.irb.droitscommunication.camunda.CamundaProcessPort;
import com.bnpparibas.irb.droitscommunication.document.DocumentAttachmentPort;
import com.bnpparibas.irb.droitscommunication.dto.StatutCount;
import com.bnpparibas.irb.droitscommunication.entity.ClientDemande;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.enums.DecisionClient;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.exception.ReferentielInvalideException;
import com.bnpparibas.irb.droitscommunication.exception.RessourceIntrouvableException;
import com.bnpparibas.irb.droitscommunication.exception.TransitionInvalideException;
import com.bnpparibas.irb.droitscommunication.referentiel.OrganismeRepository;
import com.bnpparibas.irb.droitscommunication.repository.ClientDemandeRepository;
import com.bnpparibas.irb.droitscommunication.repository.DroitCommunicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DroitCommunicationServiceImpl implements DroitCommunicationService {

    private final DroitCommunicationRepository repository;
    private final OrganismeRepository organismeRepository;
    private final ClientDemandeRepository clientRepository;
    private final DocumentAttachmentPort documentAttachmentPort;
    private final CamundaProcessPort camundaProcessPort;
    private final TraitementSabLauncher traitementSabLauncher;

    public DroitCommunicationServiceImpl(DroitCommunicationRepository repository,
                                         OrganismeRepository organismeRepository,
                                         ClientDemandeRepository clientRepository,
                                         DocumentAttachmentPort documentAttachmentPort,
                                         CamundaProcessPort camundaProcessPort,
                                         TraitementSabLauncher traitementSabLauncher) {
        this.repository = repository;
        this.organismeRepository = organismeRepository;
        this.clientRepository = clientRepository;
        this.documentAttachmentPort = documentAttachmentPort;
        this.camundaProcessPort = camundaProcessPort;
        this.traitementSabLauncher = traitementSabLauncher;
    }

    // ----------------------------------------------------------------------
    // Création / consultation
    // ----------------------------------------------------------------------

    @Override
    @Transactional
    public DroitCommunicationEntity createDroitCommunication(DroitCommunicationEntity entity) {
        validerOrganisme(entity);

        entity.setBusinessKey(UUID.randomUUID());
        entity.setStatut(StatutDemande.TRAITEMENT_SAB);

        DroitCommunicationEntity saved = repository.save(entity);

        if (saved.getNumeroDemande() == null) {
            int annee = saved.getDateReception().getYear();
            saved.setNumeroDemande(String.format("DC-%d-%04d", annee, saved.getId()));
        }

        // Commit du fichier .xlsx au MS Document (ownerRef = businessKey).
        String userUid = StringUtils.hasText(saved.getInitiateur()) ? saved.getInitiateur() : "SYSTEM";
        documentAttachmentPort.commit(saved.getBusinessKey(), List.of(saved.getDocumentId()), userUid);

        saved = repository.save(saved);

        // Démarrage de l'instance de process (Camunda externe).
        camundaProcessPort.demarrer(saved.getBusinessKey(), Map.of(
                "numeroDemande", saved.getNumeroDemande(),
                "organisme", saved.getOrganisme()));

        return saved;
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
    public List<ClientDemande> getClients(Long demandeId) {
        return clientRepository.findByDemandeIdOrderById(demandeId);
    }

    // ----------------------------------------------------------------------
    // Listing
    // ----------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Page<DroitCommunicationEntity> search(StatutDemande statut, String initiateur, String q, Pageable pageable) {
        String recherche = StringUtils.hasText(q) ? q.trim() : null;
        String init = StringUtils.hasText(initiateur) ? initiateur : null;
        return repository.search(statut, init, recherche, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DroitCommunicationEntity> corbeilleValidation(String valideur, Pageable pageable) {
        String val = StringUtils.hasText(valideur) ? valideur : null;
        return repository.corbeilleValidation(val, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatutCount> compterParStatut(String initiateur) {
        String init = StringUtils.hasText(initiateur) ? initiateur : null;
        return repository.compterParStatut(init);
    }

    // ----------------------------------------------------------------------
    // Phase 2 : traitement SAB (batch)
    // ----------------------------------------------------------------------

    @Override
    public DroitCommunicationEntity lancerTraitementSab(Long id) {
        DroitCommunicationEntity demande = getById(id);
        exigerStatut(demande, StatutDemande.TRAITEMENT_SAB);
        // Lancement hors transaction : le job a ses propres transactions et son listener
        // fait passer la demande en RESULTATS_SAB à la fin.
        traitementSabLauncher.lancer(id);
        return getById(id);
    }

    // ----------------------------------------------------------------------
    // Phase 3 : flow de validation (statut = projection, Camunda = orchestrateur)
    // ----------------------------------------------------------------------

    @Override
    @Transactional
    public DroitCommunicationEntity definirDecisionClient(Long demandeId, Long clientId, DecisionClient decision) {
        DroitCommunicationEntity demande = getById(demandeId);
        exigerStatut(demande, StatutDemande.RESULTATS_SAB);

        ClientDemande client = clientRepository.findById(clientId)
                .filter(c -> c.getDemandeId().equals(demandeId))
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Client " + clientId + " introuvable dans la demande " + demandeId));
        client.setDecision(decision);
        clientRepository.save(client);
        return demande;
    }

    @Override
    @Transactional
    public DroitCommunicationEntity envoyerEnValidation(Long id, String valideur) {
        DroitCommunicationEntity demande = getById(id);
        exigerStatut(demande, StatutDemande.RESULTATS_SAB);

        demande.setValideur(valideur);
        demande.setMotifRejet(null);
        demande.setStatut(StatutDemande.EN_ATTENTE_VALIDATION);
        DroitCommunicationEntity saved = repository.save(demande);

        camundaProcessPort.completerTache(saved.getBusinessKey(), "ENVOYER_VALIDATION",
                Map.of("valideur", valideur));
        return saved;
    }

    @Override
    @Transactional
    public DroitCommunicationEntity annulerValidation(Long id) {
        DroitCommunicationEntity demande = getById(id);
        exigerStatut(demande, StatutDemande.EN_ATTENTE_VALIDATION);

        demande.setValideur(null);
        demande.setStatut(StatutDemande.RESULTATS_SAB);
        DroitCommunicationEntity saved = repository.save(demande);

        camundaProcessPort.completerTache(saved.getBusinessKey(), "ANNULER", Map.of());
        return saved;
    }

    @Override
    @Transactional
    public DroitCommunicationEntity valider(Long id) {
        DroitCommunicationEntity demande = getById(id);
        exigerStatut(demande, StatutDemande.EN_ATTENTE_VALIDATION);

        demande.setStatut(StatutDemande.CLOTURE);
        DroitCommunicationEntity saved = repository.save(demande);

        camundaProcessPort.completerTache(saved.getBusinessKey(), "VALIDER", Map.of());
        return saved;
    }

    @Override
    @Transactional
    public DroitCommunicationEntity rejeter(Long id, String motif) {
        DroitCommunicationEntity demande = getById(id);
        exigerStatut(demande, StatutDemande.EN_ATTENTE_VALIDATION);

        // Le rejet renvoie la demande à l'initiateur (avec motif) pour correction.
        demande.setMotifRejet(motif);
        demande.setValideur(null);
        demande.setStatut(StatutDemande.RESULTATS_SAB);
        DroitCommunicationEntity saved = repository.save(demande);

        camundaProcessPort.completerTache(saved.getBusinessKey(), "REJETER", Map.of("motif", motif));
        return saved;
    }

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    private void validerOrganisme(DroitCommunicationEntity entity) {
        if (!organismeRepository.existsByCode(entity.getOrganisme())) {
            throw new ReferentielInvalideException("Organisme inconnu : " + entity.getOrganisme());
        }
    }

    private void exigerStatut(DroitCommunicationEntity demande, StatutDemande attendu) {
        if (demande.getStatut() != attendu) {
            throw new TransitionInvalideException(
                    "Action impossible : la demande " + demande.getNumeroDemande()
                            + " est au statut " + demande.getStatut()
                            + " (statut requis : " + attendu + ")");
        }
    }
}
