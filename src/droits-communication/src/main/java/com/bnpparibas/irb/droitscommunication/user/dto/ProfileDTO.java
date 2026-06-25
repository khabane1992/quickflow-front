package com.bnpparibas.irb.droitscommunication.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/** Profil applicatif d'un utilisateur (code + rôles), aligné sur {@code ProfileEnum}. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private String code;
    private Set<RoleDTO> roles;
}
