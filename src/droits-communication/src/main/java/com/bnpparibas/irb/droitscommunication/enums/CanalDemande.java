package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Référentiel stable : canal par lequel la demande est reçue.
 * La maquette n'expose que deux valeurs ("Mail", "Courrier"),
 * codées en enum car le référentiel évolue rarement.
 * Alimente le drop-down "Canal de la demande" du formulaire.
 */
public enum CanalDemande {

    MAIL("Mail"),
    COURRIER("Courrier");

    private final String libelle;

    CanalDemande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
