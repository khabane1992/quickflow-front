package com.bnpparibas.irb.droitscommunication.referentiel;

import com.bnpparibas.irb.droitscommunication.dto.ReferentielItem;
import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Service de consultation des référentiels alimentant les drop-downs du formulaire.
 * - Canal de la demande / Type de demande : enums (stables).
 * - Organisme : table en base (évolutif).
 * Le complément d'adresse est un champ texte libre (maquette) : pas de référentiel.
 */
@Service
public class ReferentielService {

    private final OrganismeRepository organismeRepository;

    public ReferentielService(OrganismeRepository organismeRepository) {
        this.organismeRepository = organismeRepository;
    }

    @Transactional(readOnly = true)
    public List<ReferentielItem> getOrganismes() {
        return organismeRepository.findByActifTrueOrderByLibelleAsc().stream()
                .map(o -> new ReferentielItem(o.getCode(), o.getLibelle()))
                .toList();
    }

    public List<ReferentielItem> getCanauxDemande() {
        return Arrays.stream(CanalDemande.values())
                .map(c -> new ReferentielItem(c.name(), c.getLibelle()))
                .toList();
    }

    public List<ReferentielItem> getTypesDemande() {
        return Arrays.stream(TypeDemande.values())
                .map(t -> new ReferentielItem(t.name(), t.getLibelle()))
                .toList();
    }
}
