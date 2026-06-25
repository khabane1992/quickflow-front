package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;

/**
 * Compteur par statut, pour alimenter les onglets de "Mes demandes"
 * (Toutes / Traitement / Résultats / Validation / Clôturées).
 */
public record StatutCount(StatutDemande statut, long count) {
}
