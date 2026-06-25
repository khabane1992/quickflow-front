package com.bnpparibas.irb.droitscommunication.user;

import com.bnpparibas.irb.droitscommunication.user.dto.InternalUserDTO;

/**
 * Port d'accès au référentiel utilisateurs interne. Implémenté par un stub tant que le vrai
 * microservice n'est pas branché (cf. {@code UserDetailsServiceStub}).
 */
public interface UserDetailsService {

    /**
     * Récupère les détails d'un utilisateur par son {@code uid}.
     *
     * @return l'utilisateur, ou {@code null} s'il est introuvable.
     */
    InternalUserDTO fetchUser(String uid);
}
