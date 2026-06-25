package com.bnpparibas.irb.droitscommunication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Corps de l'action "Rejeter" (valideur) : motif obligatoire renvoyé à l'initiateur.
 */
public record RejetRequest(

        @NotBlank(message = "Le motif de rejet est obligatoire")
        @Size(max = 500, message = "Le motif ne peut dépasser 500 caractères")
        String motif
) {
}
