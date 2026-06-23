package com.bnpparibas.irb.droitscommunication.facade;

import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;

import java.util.List;

/**
 * Point d'entrée orchestrant mapping + service pour le contrôleur.
 */
public interface DroitCommunicationFacade {

    DroitCommunicationResponse createDroitCommunication(DroitCommunicationRequest request);

    DroitCommunicationResponse getById(Long id);

    List<DroitCommunicationResponse> getAll();
}
