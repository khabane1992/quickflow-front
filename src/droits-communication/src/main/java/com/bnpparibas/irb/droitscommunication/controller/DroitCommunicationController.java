package com.bnpparibas.irb.droitscommunication.controller;

import com.bnpparibas.irb.droitscommunication.dto.DecisionRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationSummary;
import com.bnpparibas.irb.droitscommunication.dto.EnvoyerValidationRequest;
import com.bnpparibas.irb.droitscommunication.dto.RapportResponse;
import com.bnpparibas.irb.droitscommunication.dto.RejetRequest;
import com.bnpparibas.irb.droitscommunication.dto.StatutCount;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.facade.DroitCommunicationFacade;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API des demandes de droit de communication.
 * P1 : création (référence document) + listing. P2 : traitement SAB (batch).
 * P3 : flow de validation (orchestré par Camunda externe via nos endpoints).
 */
@RestController
@RequestMapping("/api/droits-communication")
public class DroitCommunicationController {

    private final DroitCommunicationFacade facade;

    public DroitCommunicationController(DroitCommunicationFacade facade) {
        this.facade = facade;
    }

    // --- Création / consultation / listing ---

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DroitCommunicationResponse create(@Valid @RequestBody DroitCommunicationRequest request) {
        return facade.createDroitCommunication(request);
    }

    @GetMapping("/{id}")
    public DroitCommunicationResponse getById(@PathVariable Long id) {
        return facade.getById(id);
    }

    @GetMapping
    public Page<DroitCommunicationSummary> list(
            @RequestParam(required = false) StatutDemande statut,
            @RequestParam(required = false) String initiateur,
            @RequestParam(required = false) String q,
            @PageableDefault(size = 10, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {
        return facade.search(statut, initiateur, q, pageable);
    }

    @GetMapping("/stats")
    public List<StatutCount> stats(@RequestParam(required = false) String initiateur) {
        return facade.compterParStatut(initiateur);
    }

    /** Corbeille de validation (vue valideur) : demandes en attente, filtrables par valideur. */
    @GetMapping("/corbeille-validation")
    public Page<DroitCommunicationSummary> corbeille(
            @RequestParam(required = false) String valideur,
            @PageableDefault(size = 10, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {
        return facade.corbeilleValidation(valideur, pageable);
    }

    // --- Phase 2 : traitement SAB ---

    /** Lance le traitement SAB (batch) : parsing du .xlsx + envoi par lots. */
    @PostMapping("/{id}/traitement-sab")
    public DroitCommunicationResponse traitementSab(@PathVariable Long id) {
        return facade.lancerTraitementSab(id);
    }

    // --- Phase 3 : flow de validation ---

    /** Pose une décision (conforme / non conforme) sur un client. */
    @PostMapping("/{id}/clients/{clientId}/decision")
    public DroitCommunicationResponse decision(@PathVariable Long id,
                                               @PathVariable Long clientId,
                                               @Valid @RequestBody DecisionRequest request) {
        return facade.definirDecisionClient(id, clientId, request);
    }

    @PostMapping("/{id}/envoyer-validation")
    public DroitCommunicationResponse envoyerValidation(@PathVariable Long id,
                                                        @Valid @RequestBody EnvoyerValidationRequest request) {
        return facade.envoyerEnValidation(id, request.valideur());
    }

    @PostMapping("/{id}/annuler-validation")
    public DroitCommunicationResponse annulerValidation(@PathVariable Long id) {
        return facade.annulerValidation(id);
    }

    @PostMapping("/{id}/valider")
    public DroitCommunicationResponse valider(@PathVariable Long id) {
        return facade.valider(id);
    }

    @PostMapping("/{id}/rejeter")
    public DroitCommunicationResponse rejeter(@PathVariable Long id,
                                              @Valid @RequestBody RejetRequest request) {
        return facade.rejeter(id, request.motif());
    }

    /** Données du rapport (demande clôturée). */
    @GetMapping("/{id}/rapport")
    public RapportResponse rapport(@PathVariable Long id) {
        return facade.getRapport(id);
    }
}
