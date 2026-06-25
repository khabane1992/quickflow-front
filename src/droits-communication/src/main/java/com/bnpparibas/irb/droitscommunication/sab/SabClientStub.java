package com.bnpparibas.irb.droitscommunication.sab;

import com.bnpparibas.irb.droitscommunication.entity.LigneDemandeStaging;
import com.bnpparibas.irb.droitscommunication.enums.Coherence;
import com.bnpparibas.irb.droitscommunication.enums.PositionCompte;
import com.bnpparibas.irb.droitscommunication.enums.TypeIncoherence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implémentation de simulation du port {@link SabClient}.
 * Reproduit, de façon <b>déterministe</b> (par CIN/RC + nom), la distribution de la
 * maquette : clients inexistants / nom différent / douteux / cohérents.
 * À remplacer par l'appel réel à SAB (REST/MQ) — fournir un autre bean {@code @Primary}.
 */
@Component
@Slf4j
public class SabClientStub implements SabClient {

    @Override
    public List<SabResultat> consulter(List<LigneDemandeStaging> lignes) {
        log.debug("[STUB SAB] consultation d'un lot de {} client(s)", lignes.size());
        List<SabResultat> resultats = new ArrayList<>(lignes.size());
        for (LigneDemandeStaging ligne : lignes) {
            resultats.add(simuler(ligne));
        }
        return resultats;
    }

    private SabResultat simuler(LigneDemandeStaging ligne) {
        String cle = Objects.toString(ligne.getCin(), "")
                + Objects.toString(ligne.getNumeroRc(), "")
                + Objects.toString(ligne.getNomRaisonSociale(), "");
        int bucket = Math.floorMod(cle.hashCode(), 10);
        String suffixe = String.format("%06d", Math.floorMod(cle.hashCode(), 1_000_000));

        if (bucket <= 1) { // introuvable SAB (rouge)
            return new SabResultat(null, null,
                    Coherence.INCOHERENT, TypeIncoherence.INEXISTANT,
                    null, null, PositionCompte.NUL, null, null);
        }
        if (bucket == 2) { // ID Nat trouvé sous un autre nom (rouge)
            return new SabResultat("C-" + suffixe, "Titulaire SAB " + suffixe,
                    Coherence.INCOHERENT, TypeIncoherence.NOM_DIFFERENT,
                    "007 810 " + suffixe, new BigDecimal("1245.00"), PositionCompte.CREDITEUR,
                    "Adresse SAB " + suffixe, "+212 6 00 " + suffixe);
        }
        if (bucket <= 4) { // douteux / contentieux (orange)
            boolean debiteur = bucket == 3;
            return new SabResultat("E-" + suffixe, ligne.getNomRaisonSociale(),
                    Coherence.DOUTEUX, TypeIncoherence.AUCUNE,
                    "007 810 " + suffixe,
                    debiteur ? new BigDecimal("-12480.00") : new BigDecimal("128940.75"),
                    debiteur ? PositionCompte.DEBITEUR : PositionCompte.CREDITEUR,
                    "Zone Indus. " + suffixe + ", Casablanca", "+212 5 22 " + suffixe);
        }
        // cohérent (vert)
        return new SabResultat("C-" + suffixe, ligne.getNomRaisonSociale(),
                Coherence.COHERENT, TypeIncoherence.AUCUNE,
                "007 810 " + suffixe, new BigDecimal("12480.50"), PositionCompte.CREDITEUR,
                "Rue " + suffixe + ", Casablanca", "+212 6 61 " + suffixe);
    }
}
