package com.bnpparibas.irb.droitscommunication.exception;

/**
 * Levée quand une transition de workflow est demandée depuis un statut
 * qui ne l'autorise pas (ex. valider une demande qui n'est pas en attente).
 * Mappée en HTTP 409 Conflict.
 */
public class TransitionInvalideException extends RuntimeException {

    public TransitionInvalideException(String message) {
        super(message);
    }
}
