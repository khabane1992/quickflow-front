package com.bnpparibas.irb.droitscommunication.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Permission élémentaire d'un rôle (ex. {@code DC_CREER_DEMANDE}). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    private String code;
}
