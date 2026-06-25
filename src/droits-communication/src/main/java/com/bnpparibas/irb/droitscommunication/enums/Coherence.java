package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Niveau de cohérence d'un client après confrontation de la liste fournie (.xlsx)
 * au référentiel SAB. Code couleur de la maquette (vert / orange / rouge).
 */
public enum Coherence {

    COHERENT("Cohérent"),
    DOUTEUX("Douteux"),
    INCOHERENT("Incohérent");

    private final String libelle;

    Coherence(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
