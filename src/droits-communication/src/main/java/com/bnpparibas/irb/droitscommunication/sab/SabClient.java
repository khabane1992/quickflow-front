package com.bnpparibas.irb.droitscommunication.sab;

import com.bnpparibas.irb.droitscommunication.entity.LigneDemandeStaging;

import java.util.List;

/**
 * Port d'accès au système SAB (core banking).
 * Confronte un lot de lignes clients (table tampon) au référentiel SAB et renvoie,
 * pour chaque ligne (dans le même ordre), son enrichissement.
 *
 * <p>Appel pensé <b>par lot</b> (un appel pour tout le chunk), conformément au besoin
 * d'envoi en masse. Impl. courante : {@link SabClientStub}.
 */
public interface SabClient {

    List<SabResultat> consulter(List<LigneDemandeStaging> lignes);
}
