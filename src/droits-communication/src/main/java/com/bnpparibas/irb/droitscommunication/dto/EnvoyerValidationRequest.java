package com.bnpparibas.irb.droitscommunication.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Corps de l'action "Envoyer pour validation" : affecte un valideur à la demande.
 */
public record EnvoyerValidationRequest(

        @NotBlank(message = "Le valideur destinataire est obligatoire")
        String valideur
) {
}
