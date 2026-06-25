package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Position du compte renvoyée par SAB pour un client.
 */
public enum PositionCompte {

    CREDITEUR("Créditeur"),
    DEBITEUR("Débiteur"),
    NUL("Nul / sans compte");

    private final String libelle;

    PositionCompte(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
