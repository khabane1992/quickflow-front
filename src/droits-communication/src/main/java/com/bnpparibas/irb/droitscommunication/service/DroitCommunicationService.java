package com.bnpparibas.irb.droitscommunication.service;

import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;

import java.util.List;

/**
 * Logique métier des demandes de droit de communication.
 */
public interface DroitCommunicationService {

    DroitCommunicationEntity createDroitCommunication(DroitCommunicationEntity entity);

    DroitCommunicationEntity getById(Long id);

    List<DroitCommunicationEntity> getAll();
}
