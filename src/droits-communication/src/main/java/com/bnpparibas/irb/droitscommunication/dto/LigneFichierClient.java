package com.bnpparibas.irb.droitscommunication.dto;

/**
 * Ligne client parsée depuis le fichier .xlsx (avant insertion en table tampon).
 */
public record LigneFichierClient(
        int numeroLigne,
        String nomRaisonSociale,
        String cin,
        String numeroRc
) {
}
