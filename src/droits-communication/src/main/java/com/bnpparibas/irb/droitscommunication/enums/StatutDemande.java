package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Statut métier d'une demande de droit de communication.
 * Permet le suivi du cycle de vie (création -> traitement -> clôture).
 */
public enum StatutDemande {

    BROUILLON("Brouillon"),
    SOUMISE("Soumise"),
    EN_COURS("En cours de traitement"),
    TRAITEE("Traitée"),
    REJETEE("Rejetée");

    private final String libelle;

    StatutDemande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
