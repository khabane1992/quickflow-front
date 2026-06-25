package com.bnpparibas.irb.droitscommunication.user;

import com.bnpparibas.irb.droitscommunication.user.dto.InternalUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implémentation de simulation du {@link UserService} : trace l'appel de synchronisation,
 * le temps de brancher la persistance locale des utilisateurs.
 */
@Component
@Slf4j
public class UserServiceStub implements UserService {

    @Override
    public void findOrSyncUserByUid(String uid, InternalUserDTO userDetails) {
        log.info("[STUB Users] findOrSyncUserByUid uid={}", uid);
    }
}
