package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Référentiel stable : type(s) de demande.
 * La maquette montre une multi-sélection (Solde, Position du compte, RIB,
 * Adresse du compte, Mouvement du compte) -> manipulé en Set côté entité.
 */
public enum TypeDemande {

    SOLDE("Solde"),
    POSITION_COMPTE("Position du compte"),
    RIB("RIB"),
    ADRESSE_COMPTE("Adresse du compte"),
    MOUVEMENT_COMPTE("Mouvement du compte");

    private final String libelle;

    TypeDemande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
