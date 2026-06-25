package com.bnpparibas.irb.droitscommunication.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Détails d'un utilisateur tels que renvoyés par le microservice utilisateurs interne
 * (ici simulé par un stub). Sert de {@code principal} dans le contexte de sécurité.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalUserDTO {
    private String uid;
    private String nom;
    private String prenom;
    private String email;
    private Boolean enabled;
    private ProfileDTO profile;
}
