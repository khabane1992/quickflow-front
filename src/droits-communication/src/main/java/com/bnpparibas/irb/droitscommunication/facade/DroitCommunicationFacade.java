package com.bnpparibas.irb.droitscommunication.facade;

import com.bnpparibas.irb.droitscommunication.dto.DecisionRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationSummary;
import com.bnpparibas.irb.droitscommunication.dto.RapportResponse;
import com.bnpparibas.irb.droitscommunication.dto.StatutCount;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Point d'entrée orchestrant mapping + service pour les contrôleurs.
 */
public interface DroitCommunicationFacade {

    DroitCommunicationResponse createDroitCommunication(DroitCommunicationRequest request);

    DroitCommunicationResponse getById(Long id);

    Page<DroitCommunicationSummary> search(StatutDemande statut, String initiateur, String q, Pageable pageable);

    Page<DroitCommunicationSummary> corbeilleValidation(String valideur, Pageable pageable);

    List<StatutCount> compterParStatut(String initiateur);

    RapportResponse getRapport(Long id);

    // --- Phase 2 ---
    DroitCommunicationResponse lancerTraitementSab(Long id);

    // --- Phase 3 ---
    DroitCommunicationResponse definirDecisionClient(Long demandeId, Long clientId, DecisionRequest request);

    DroitCommunicationResponse envoyerEnValidation(Long id, String valideur);

    DroitCommunicationResponse annulerValidation(Long id);

    DroitCommunicationResponse valider(Long id);

    DroitCommunicationResponse rejeter(Long id, String motif);
}
