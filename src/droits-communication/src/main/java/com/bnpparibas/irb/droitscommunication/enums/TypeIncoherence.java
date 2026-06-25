package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Nature de l'incohérence quand un client est INCOHERENT (rouge).
 * Colonne "Type" des clients incohérents de la maquette.
 */
public enum TypeIncoherence {

    AUCUNE("—"),
    INEXISTANT("Inexistant SAB"),
    NOM_DIFFERENT("Nom différent / ID Nat trouvé");

    private final String libelle;

    TypeIncoherence(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
