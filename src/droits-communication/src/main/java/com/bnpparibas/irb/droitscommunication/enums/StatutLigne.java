package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Statut de traitement d'une ligne de la table tampon (parsing → envoi SAB).
 */
public enum StatutLigne {

    /** Ligne parsée, en attente d'envoi à SAB. */
    EN_ATTENTE,

    /** Ligne traitée par SAB (résultat consolidé en ClientDemande). */
    TRAITE,

    /** Erreur de traitement (voir messageErreur). */
    ERREUR
}
