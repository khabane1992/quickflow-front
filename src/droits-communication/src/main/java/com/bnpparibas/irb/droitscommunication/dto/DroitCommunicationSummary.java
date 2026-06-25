package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;

import java.time.LocalDate;

/**
 * Ligne de tableau de la vue "Mes demandes" / "Corbeille de validation".
 * Vue allégée (sans la liste des clients) pour le listing paginé.
 */
public record DroitCommunicationSummary(
        Long id,
        String numeroDemande,
        LocalDate dateReception,
        String referenceDemande,
        String organisme,
        CanalDemande canalDemande,
        int nombreClients,
        StatutDemande statut,
        String initiateur,
        String valideur
) {
}
