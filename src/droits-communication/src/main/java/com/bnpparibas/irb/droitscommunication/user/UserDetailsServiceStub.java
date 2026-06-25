package com.bnpparibas.irb.droitscommunication.user;

import com.bnpparibas.irb.droitscommunication.user.dto.InternalUserDTO;
import com.bnpparibas.irb.droitscommunication.user.dto.PermissionDTO;
import com.bnpparibas.irb.droitscommunication.user.dto.ProfileDTO;
import com.bnpparibas.irb.droitscommunication.user.dto.RoleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Implémentation de simulation du {@link UserDetailsService} : renvoie un utilisateur
 * <b>déterministe</b> (profil initiateur, actif) pour n'importe quel {@code uid}, afin de
 * pouvoir développer/tester l'authentification mock en local.
 *
 * <p>À remplacer par le vrai client du microservice utilisateurs (impl. annotée {@code @Primary}).
 */
@Component
@Slf4j
public class UserDetailsServiceStub implements UserDetailsService {

    @Override
    public InternalUserDTO fetchUser(String uid) {
        log.info("[STUB Users] fetchUser uid={}", uid);

        ProfileDTO profile = ProfileDTO.builder()
                .code("DC_INITIATEUR")
                .roles(Set.of(RoleDTO.builder()
                        .code("DC_ROLE_INITIATEUR")
                        .permissions(Set.of(
                                PermissionDTO.builder().code("DC_CREER_DEMANDE").build(),
                                PermissionDTO.builder().code("DC_LISTER_DEMANDE").build(),
                                PermissionDTO.builder().code("DC_TRAITER_SAB").build()))
                        .build()))
                .build();

        return InternalUserDTO.builder()
                .uid(uid)
                .nom("Utilisateur")
                .prenom(uid)
                .email(uid + "@bnpparibas.com")
                .enabled(true)
                .profile(profile)
                .build();
    }
}
