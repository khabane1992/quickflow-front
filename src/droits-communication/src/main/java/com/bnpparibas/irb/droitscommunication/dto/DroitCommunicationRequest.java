package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Payload de création d'une demande de droit de communication.
 * Reflète les champs du formulaire "Nouvelle demande".
 */
public record DroitCommunicationRequest(

        @NotBlank(message = "L'organisme est obligatoire")
        String organisme,

        @NotBlank(message = "L'adresse postale est obligatoire")
        String adressePostale,

        // Facultatif (champ libre/facultatif dans la user story)
        String complementAdresse,

        @NotBlank(message = "L'objet de la demande est obligatoire")
        @Size(max = 2000, message = "L'objet ne peut dépasser 2000 caractères")
        String objetDemande,

        @NotNull(message = "Le canal de la demande est obligatoire")
        CanalDemande canalDemande,

        String referenceDemande,

        @NotNull(message = "La date de réception est obligatoire")
        LocalDate dateReception,

        @NotEmpty(message = "Au moins un type de demande doit être sélectionné")
        Set<TypeDemande> typesDemande,

        // Référence du document fournie par le microservice d'upload (facultatif)
        String documentReference,
        String documentNom,
        String documentContentType
) {
}
