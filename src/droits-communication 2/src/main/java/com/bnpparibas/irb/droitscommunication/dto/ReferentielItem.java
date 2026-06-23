package com.bnpparibas.irb.droitscommunication.dto;

/**
 * Représentation uniforme d'un item de référentiel pour les drop-downs du front.
 * code = valeur envoyée au back, libelle = texte affiché.
 */
public record ReferentielItem(String code, String libelle) {
}
