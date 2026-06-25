package com.bnpparibas.irb.droitscommunication.user;

import com.bnpparibas.irb.droitscommunication.user.dto.InternalUserDTO;

/**
 * Port de synchronisation de l'utilisateur dans le référentiel local (création à la volée /
 * mise à jour) à partir des détails fournis par le référentiel interne.
 */
public interface UserService {

    void findOrSyncUserByUid(String uid, InternalUserDTO userDetails);
}
