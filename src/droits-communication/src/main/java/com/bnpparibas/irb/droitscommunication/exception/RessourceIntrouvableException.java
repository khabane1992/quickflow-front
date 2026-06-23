package com.bnpparibas.irb.droitscommunication.exception;

/**
 * Levée lorsqu'une ressource demandée n'existe pas (demande, référentiel...).
 */
public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
