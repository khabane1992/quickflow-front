package com.bnpparibas.irb.droitscommunication.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Corps de réponse standard en cas d'erreur.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        Map<String, String> details
) {
}
