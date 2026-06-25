package com.bnpparibas.irb.droitscommunication.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Données du rapport d'une demande clôturée (écran "Aperçu rapport / RAPPORT PDF").
 * La génération du binaire PDF est assurée par un microservice dédié : on expose ici
 * les données nécessaires.
 */
public record RapportResponse(
        Long demandeId,
        String numeroDemande,
        String organisme,
        String referenceDemande,
        String initiateur,
        String valideur,
        LocalDateTime dateCloture,
        int nombreClients,
        long nombreConformes,
        long nombreNonConformes,
        List<ClientDemandeResponse> clients
) {
}
