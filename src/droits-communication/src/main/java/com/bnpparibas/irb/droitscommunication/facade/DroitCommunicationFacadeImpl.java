package com.bnpparibas.irb.droitscommunication.facade;

import com.bnpparibas.irb.droitscommunication.dto.ClientDemandeResponse;
import com.bnpparibas.irb.droitscommunication.dto.DecisionRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationSummary;
import com.bnpparibas.irb.droitscommunication.dto.RapportResponse;
import com.bnpparibas.irb.droitscommunication.dto.StatutCount;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.enums.DecisionClient;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.exception.TransitionInvalideException;
import com.bnpparibas.irb.droitscommunication.mapper.DroitCommunicationMapper;
import com.bnpparibas.irb.droitscommunication.service.DroitCommunicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DroitCommunicationFacadeImpl implements DroitCommunicationFacade {

    private final DroitCommunicationService service;
    private final DroitCommunicationMapper mapper;

    public DroitCommunicationFacadeImpl(DroitCommunicationService service,
                                        DroitCommunicationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public DroitCommunicationResponse createDroitCommunication(DroitCommunicationRequest request) {
        DroitCommunicationEntity entity = mapper.toEntity(request);
        return toResponse(service.createDroitCommunication(entity));
    }

    @Override
    public DroitCommunicationResponse getById(Long id) {
        return toResponse(service.getById(id));
    }

    @Override
    public Page<DroitCommunicationSummary> search(StatutDemande statut, String initiateur, String q, Pageable pageable) {
        return service.search(statut, initiateur, q, pageable).map(mapper::toSummary);
    }

    @Override
    public Page<DroitCommunicationSummary> corbeilleValidation(String valideur, Pageable pageable) {
        return service.corbeilleValidation(valideur, pageable).map(mapper::toSummary);
    }

    @Override
    public List<StatutCount> compterParStatut(String initiateur) {
        return service.compterParStatut(initiateur);
    }

    @Override
    public RapportResponse getRapport(Long id) {
        DroitCommunicationEntity demande = service.getById(id);
        if (demande.getStatut() != StatutDemande.CLOTURE) {
            throw new TransitionInvalideException(
                    "Le rapport n'est disponible que pour une demande clôturée (statut actuel : "
                            + demande.getStatut() + ")");
        }
        List<ClientDemandeResponse> clients = clients(id);
        long conformes = clients.stream().filter(c -> c.decision() == DecisionClient.CONFORME).count();
        long nonConformes = clients.stream().filter(c -> c.decision() == DecisionClient.NON_CONFORME).count();
        return new RapportResponse(
                demande.getId(),
                demande.getNumeroDemande(),
                demande.getOrganisme(),
                demande.getReferenceDemande(),
                demande.getInitiateur(),
                demande.getValideur(),
                demande.getDateModification(),
                demande.getNombreClients(),
                conformes,
                nonConformes,
                clients);
    }

    // --- Phase 2 ---

    @Override
    public DroitCommunicationResponse lancerTraitementSab(Long id) {
        return toResponse(service.lancerTraitementSab(id));
    }

    // --- Phase 3 ---

    @Override
    public DroitCommunicationResponse definirDecisionClient(Long demandeId, Long clientId, DecisionRequest request) {
        return toResponse(service.definirDecisionClient(demandeId, clientId, request.decision()));
    }

    @Override
    public DroitCommunicationResponse envoyerEnValidation(Long id, String valideur) {
        return toResponse(service.envoyerEnValidation(id, valideur));
    }

    @Override
    public DroitCommunicationResponse annulerValidation(Long id) {
        return toResponse(service.annulerValidation(id));
    }

    @Override
    public DroitCommunicationResponse valider(Long id) {
        return toResponse(service.valider(id));
    }

    @Override
    public DroitCommunicationResponse rejeter(Long id, String motif) {
        return toResponse(service.rejeter(id, motif));
    }

    // --- Helpers ---

    private DroitCommunicationResponse toResponse(DroitCommunicationEntity demande) {
        return mapper.toResponse(demande, clients(demande.getId()));
    }

    private List<ClientDemandeResponse> clients(Long demandeId) {
        return service.getClients(demandeId).stream().map(mapper::toClientResponse).toList();
    }
}
