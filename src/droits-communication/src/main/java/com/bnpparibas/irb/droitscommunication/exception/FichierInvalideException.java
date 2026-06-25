package com.bnpparibas.irb.droitscommunication.exception;

/**
 * Levée quand le fichier .xlsx est illisible, mal structuré ou vide.
 * Mappée en HTTP 422 Unprocessable Entity.
 */
public class FichierInvalideException extends RuntimeException {

    public FichierInvalideException(String message) {
        super(message);
    }

    public FichierInvalideException(String message, Throwable cause) {
        super(message, cause);
    }
}
