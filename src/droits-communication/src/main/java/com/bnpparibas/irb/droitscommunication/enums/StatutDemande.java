package com.bnpparibas.irb.droitscommunication.enums;

/**
 * Statut métier d'une demande de droit de communication.
 * Reflète le cycle de vie de la maquette "Mes demandes" :
 *
 * <pre>
 *   TRAITEMENT_SAB ──(résultats SAB reçus)──▶ RESULTATS_SAB
 *        ▲                                        │
 *        │                                        │ (envoyer pour validation)
 *        │                                        ▼
 *        │                                EN_ATTENTE_VALIDATION
 *        │                                  │            │
 *        │        (annuler / rejeter)◀──────┘            │ (valider)
 *        └────────────── RESULTATS_SAB ◀─────┘           ▼
 *                                                      CLOTURE
 * </pre>
 *
 * Les codes (clés maquette) sont exposés tels quels au front via {@code name()}.
 */
public enum StatutDemande {

    /** "traitement" — la demande vient d'être soumise, SAB traite la liste. */
    TRAITEMENT_SAB("Traitement SAB en cours"),

    /** "resultats" — SAB a renvoyé les résultats, consultables par l'initiateur. */
    RESULTATS_SAB("Résultats SAB disponibles"),

    /** "attente" — envoyée pour validation, affectée à un valideur. */
    EN_ATTENTE_VALIDATION("En attente de validation"),

    /** "cloture" — validée, rapport PDF disponible. */
    CLOTURE("Clôturé");

    private final String libelle;

    StatutDemande(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
