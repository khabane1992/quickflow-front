package com.bnpparibas.irb.droitscommunication.sab;

import com.bnpparibas.irb.droitscommunication.enums.Coherence;
import com.bnpparibas.irb.droitscommunication.enums.PositionCompte;
import com.bnpparibas.irb.droitscommunication.enums.TypeIncoherence;

import java.math.BigDecimal;

/**
 * Résultat renvoyé par SAB pour un client donné (objet de transport du port SAB).
 */
public record SabResultat(
        String idSab,
        String nomSab,
        Coherence coherence,
        TypeIncoherence typeIncoherence,
        String numeroCompte,
        BigDecimal solde,
        PositionCompte position,
        String adresse,
        String telephone
) {
}
