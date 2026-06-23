package com.bnpparibas.irb.droitscommunication.controller;

import com.bnpparibas.irb.droitscommunication.dto.ReferentielItem;
import com.bnpparibas.irb.droitscommunication.referentiel.ReferentielService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expose les référentiels nécessaires au formulaire "Nouvelle demande".
 * Endpoints en lecture seule, consommés pour alimenter les drop-downs.
 */
@RestController
@RequestMapping("/api/referentiels")
public class ReferentielController {

    private final ReferentielService referentielService;

    public ReferentielController(ReferentielService referentielService) {
        this.referentielService = referentielService;
    }

    @GetMapping("/organismes")
    public List<ReferentielItem> organismes() {
        return referentielService.getOrganismes();
    }

    @GetMapping("/complements-adresse")
    public List<ReferentielItem> complementsAdresse() {
        return referentielService.getComplementsAdresse();
    }

    @GetMapping("/canaux-demande")
    public List<ReferentielItem> canauxDemande() {
        return referentielService.getCanauxDemande();
    }

    @GetMapping("/types-demande")
    public List<ReferentielItem> typesDemande() {
        return referentielService.getTypesDemande();
    }
}
