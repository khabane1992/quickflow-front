package com.bnpparibas.irb.droitscommunication.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/** Rôle d'un profil, porteur d'un ensemble de {@link PermissionDTO}. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private String code;
    private Set<PermissionDTO> permissions;
}
