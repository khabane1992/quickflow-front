package com.bnpparibas.irb.droitscommunication.mapper;

import com.bnpparibas.irb.droitscommunication.dto.ClientDemandeResponse;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationSummary;
import com.bnpparibas.irb.droitscommunication.entity.ClientDemande;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapping entre la requête, l'entité persistée et les réponses (détail + résumé).
 * Les champs gérés côté serveur (numéro, statut, nombre de clients, valideur, dates)
 * ne sont pas dérivés de la requête.
 */
@Mapper(componentModel = "spring")
public interface DroitCommunicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numeroDemande", ignore = true)
    @Mapping(target = "businessKey", ignore = true)
    @Mapping(target = "nombreClients", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "valideur", ignore = true)
    @Mapping(target = "motifRejet", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    DroitCommunicationEntity toEntity(DroitCommunicationRequest request);

    @Mapping(target = "clients", source = "clients")
    DroitCommunicationResponse toResponse(DroitCommunicationEntity entity, List<ClientDemandeResponse> clients);

    DroitCommunicationSummary toSummary(DroitCommunicationEntity entity);

    ClientDemandeResponse toClientResponse(ClientDemande client);
}
