package com.bnpparibas.irb.droitscommunication.user;

import java.util.Arrays;

/**
 * Profils autorisés à utiliser le module Droits de Communication. Le {@link QfSimplifiedAuthFilter}
 * vérifie via {@link #exists(String)} que le code de profil du JWT (préfixe {@code DC_} retiré)
 * correspond à l'une de ces valeurs.
 */
public enum ProfileEnum {

    /** Crée et suit ses propres demandes. */
    INITIATEUR,

    /** Valide / rejette les demandes envoyées en validation. */
    VALIDATEUR,

    /** Accès complet (administration). */
    ADMIN;

    public static boolean exists(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        return Arrays.stream(values()).anyMatch(p -> p.name().equalsIgnoreCase(code.trim()));
    }
}
