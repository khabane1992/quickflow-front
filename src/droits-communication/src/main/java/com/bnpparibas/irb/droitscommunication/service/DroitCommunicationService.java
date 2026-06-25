package com.bnpparibas.irb.droitscommunication.service;

import com.bnpparibas.irb.droitscommunication.dto.StatutCount;
import com.bnpparibas.irb.droitscommunication.entity.ClientDemande;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.enums.DecisionClient;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Logique métier des demandes de droit de communication, incluant le traitement SAB
 * (Phase 2) et le flow de validation (Phase 3, orchestré par Camunda externe).
 */
public interface DroitCommunicationService {

    // --- Création / consultation ---

    DroitCommunicationEntity createDroitCommunication(DroitCommunicationEntity entity);

    DroitCommunicationEntity getById(Long id);

    List<ClientDemande> getClients(Long demandeId);

    // --- Listing ---

    Page<DroitCommunicationEntity> search(StatutDemande statut, String initiateur, String q, Pageable pageable);

    Page<DroitCommunicationEntity> corbeilleValidation(String valideur, Pageable pageable);

    List<StatutCount> compterParStatut(String initiateur);

    // --- Phase 2 : traitement SAB (batch) ---

    /** Lance le batch SAB (TRAITEMENT_SAB requis) ; à la fin la demande passe en RESULTATS_SAB. */
    DroitCommunicationEntity lancerTraitementSab(Long id);

    // --- Phase 3 : flow de validation ---

    DroitCommunicationEntity definirDecisionClient(Long demandeId, Long clientId, DecisionClient decision);

    DroitCommunicationEntity envoyerEnValidation(Long id, String valideur);

    DroitCommunicationEntity annulerValidation(Long id);

    DroitCommunicationEntity valider(Long id);

    DroitCommunicationEntity rejeter(Long id, String motif);
}
