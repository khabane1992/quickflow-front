package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Référentiel stable : type(s) de demande.
 * La maquette montre une multi-sélection : Solde, Position du compte, RIB,
 * Adresse client, Mouvement de compte -> manipulé en Set côté entité.
 */
public enum TypeDemande {

    SOLDE("Solde"),
    POSITION_COMPTE("Position du compte"),
    RIB("RIB"),
    ADRESSE_CLIENT("Adresse client"),
    MOUVEMENT_COMPTE("Mouvement de compte");

    private final String libelle;

    TypeDemande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
