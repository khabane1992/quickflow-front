package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.enums.Coherence;
import com.bnpparibas.irb.droitscommunication.enums.DecisionClient;
import com.bnpparibas.irb.droitscommunication.enums.PositionCompte;
import com.bnpparibas.irb.droitscommunication.enums.TypeIncoherence;

import java.math.BigDecimal;

/**
 * Client (ligne .xlsx + enrichissement SAB) renvoyé dans le détail d'une demande
 * (écran "Résultats SAB").
 */
public record ClientDemandeResponse(
        Long id,
        String nomFourni,
        String cin,
        String numeroRc,
        String idSab,
        String nomSab,
        Coherence coherence,
        TypeIncoherence typeIncoherence,
        String numeroCompte,
        BigDecimal solde,
        PositionCompte position,
        String adresse,
        String telephone,
        DecisionClient decision
) {
}
