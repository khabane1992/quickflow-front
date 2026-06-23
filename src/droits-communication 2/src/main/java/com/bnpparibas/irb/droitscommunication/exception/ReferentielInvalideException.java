package com.bnpparibas.irb.droitscommunication.exception;

/**
 * Levée lorsqu'une valeur de référentiel envoyée n'existe pas / n'est pas active.
 */
public class ReferentielInvalideException extends RuntimeException {

    public ReferentielInvalideException(String message) {
        super(message);
    }
}
