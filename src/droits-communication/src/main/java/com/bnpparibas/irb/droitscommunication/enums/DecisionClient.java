package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Décision de l'initiateur sur un client, à partir des résultats SAB.
 * Valeur par défaut déduite de la cohérence (INCOHERENT -> NON_CONFORME),
 * modifiable avant l'envoi en validation.
 */
public enum DecisionClient {

    CONFORME("Conforme"),
    NON_CONFORME("Non conforme");

    private final String libelle;

    DecisionClient(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
