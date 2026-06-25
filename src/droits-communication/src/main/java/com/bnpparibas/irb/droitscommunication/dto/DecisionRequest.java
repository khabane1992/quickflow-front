package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.enums.DecisionClient;
import jakarta.validation.constraints.NotNull;

/**
 * Corps de l'action "poser une décision" sur un client (conforme / non conforme).
 */
public record DecisionRequest(

        @NotNull(message = "La décision est obligatoire")
        DecisionClient decision
) {
}
