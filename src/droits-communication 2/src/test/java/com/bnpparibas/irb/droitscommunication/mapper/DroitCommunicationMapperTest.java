package com.bnpparibas.irb.droitscommunication.mapper;

import com.bnpparibas.irb.droitscommunication.TestFixtures;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Vérifie le mapping généré par MapStruct (impl réelle, pas un mock).
 */
class DroitCommunicationMapperTest {

    private final DroitCommunicationMapper mapper =
            Mappers.getMapper(DroitCommunicationMapper.class);

    @Test
    void toEntity_devrait_copier_les_champs_et_ignorer_le_suivi() {
        DroitCommunicationRequest request = TestFixtures.request();

        DroitCommunicationEntity entity = mapper.toEntity(request);

        assertThat(entity.getOrganisme()).isEqualTo("DGFIP");
        assertThat(entity.getCanalDemande()).isEqualTo(request.canalDemande());
        assertThat(entity.getTypesDemande()).isEqualTo(request.typesDemande());
        assertThat(entity.getDocumentReference()).isEqualTo("doc-uuid-123");
        // Champs de suivi ignorés à la création
        assertThat(entity.getId()).isNull();
        assertThat(entity.getStatut()).isNull();
        assertThat(entity.getDateCreation()).isNull();
    }

    @Test
    void toResponse_devrait_copier_tous_les_champs() {
        DroitCommunicationEntity entity = TestFixtures.entity();

        DroitCommunicationResponse response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.organisme()).isEqualTo("DGFIP");
        assertThat(response.statut()).isEqualTo(entity.getStatut());
        assertThat(response.typesDemande()).isEqualTo(entity.getTypesDemande());
        assertThat(response.dateCreation()).isEqualTo(entity.getDateCreation());
    }
}
