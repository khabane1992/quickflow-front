package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Représentation renvoyée au client après création/consultation d'une demande.
 */
public record DroitCommunicationResponse(
        Long id,
        String organisme,
        String adressePostale,
        String complementAdresse,
        String objetDemande,
        CanalDemande canalDemande,
        String referenceDemande,
        LocalDate dateReception,
        Set<TypeDemande> typesDemande,
        String documentReference,
        String documentNom,
        String documentContentType,
        StatutDemande statut,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {
}
