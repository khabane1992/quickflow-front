package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Détail d'une demande (création / consultation).
 * Le fichier est référencé par son {@code documentId} (MS Document) ; ses métadonnées
 * (nom, taille, type) ne sont pas dupliquées ici.
 */
public record DroitCommunicationResponse(
        Long id,
        String numeroDemande,
        UUID businessKey,
        String organisme,
        String adressePostale,
        String complementAdresse,
        String objetDemande,
        CanalDemande canalDemande,
        String referenceDemande,
        LocalDate dateReception,
        Set<TypeDemande> typesDemande,
        UUID documentId,
        int nombreClients,
        StatutDemande statut,
        String initiateur,
        String valideur,
        String motifRejet,
        List<ClientDemandeResponse> clients,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {
}
