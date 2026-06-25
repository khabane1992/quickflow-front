package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Payload de création d'une demande de droit de communication.
 * Aligné sur le formulaire "Nouvelle demande" de la maquette.
 *
 * <p>Le fichier .xlsx a déjà été uploadé par le front au microservice Document, qui a
 * renvoyé un {@code documentId} : on ne reçoit donc que cet id (pas le binaire).
 */
public record DroitCommunicationRequest(

        @NotBlank(message = "L'organisme est obligatoire")
        String organisme,

        @NotNull(message = "Le canal de la demande est obligatoire")
        CanalDemande canalDemande,

        @NotNull(message = "La date de réception est obligatoire")
        @PastOrPresent(message = "La date de réception ne peut pas être dans le futur")
        LocalDate dateReception,

        @NotBlank(message = "La référence de la demande est obligatoire")
        String referenceDemande,

        // Facultatif (maquette : pas d'astérisque).
        String adressePostale,

        // Champ libre facultatif (maquette : "Bâtiment, étage, etc.").
        String complementAdresse,

        @NotEmpty(message = "Au moins un type de demande doit être sélectionné")
        Set<TypeDemande> typesDemande,

        // Facultatif, 255 caractères max (maquette).
        @Size(max = 255, message = "L'objet ne peut dépasser 255 caractères")
        String objetDemande,

        // Identifiant (UUID) du fichier .xlsx dans le microservice Document (obligatoire).
        @NotNull(message = "L'identifiant du document (fichier .xlsx) est obligatoire")
        UUID documentId,

        // Initiateur (owner) — sans auth, fourni par l'appelant.
        String initiateur
) {
}
