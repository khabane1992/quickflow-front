package com.bnpparibas.irb.droitscommunication.mapper;

import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;

/**
 * Mapping entre la requête, l'entité persistée et la réponse.
 * Les champs de suivi (id, statut, dates d'audit) sont ignorés à la création :
 * ils sont gérés par la base / l'auditing JPA.
 */
@Mapper(componentModel = "spring")
public interface DroitCommunicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "dateModification", ignore = true)
    DroitCommunicationEntity toEntity(DroitCommunicationRequest request);

    DroitCommunicationResponse toResponse(DroitCommunicationEntity entity);
}
