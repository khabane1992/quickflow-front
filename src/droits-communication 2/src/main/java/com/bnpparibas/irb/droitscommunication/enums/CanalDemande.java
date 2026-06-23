package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Référentiel stable : canal par lequel la demande est reçue.
 * Codé en enum car les valeurs évoluent rarement.
 * Alimente le drop-down "Canal de la demande" du formulaire.
 */
public enum CanalDemande {

    COURRIER("Courrier"),
    EMAIL("Email"),
    GUICHET("Guichet"),
    TELEPHONE("Téléphone"),
    PORTAIL("Portail en ligne");

    private final String libelle;

    CanalDemande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
