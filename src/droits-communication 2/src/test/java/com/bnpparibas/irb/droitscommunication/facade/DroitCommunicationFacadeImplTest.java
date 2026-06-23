package com.bnpparibas.irb.droitscommunication.facade;

import com.bnpparibas.irb.droitscommunication.TestFixtures;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.mapper.DroitCommunicationMapper;
import com.bnpparibas.irb.droitscommunication.service.DroitCommunicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroitCommunicationFacadeImplTest {

    @Mock
    private DroitCommunicationService service;
    @Mock
    private DroitCommunicationMapper mapper;

    @InjectMocks
    private DroitCommunicationFacadeImpl facade;

    @Test
    void create_devrait_enchainer_mapping_service_mapping() {
        DroitCommunicationRequest request = TestFixtures.request();
        DroitCommunicationEntity entity = TestFixtures.entity();
        DroitCommunicationResponse response = responseFrom(entity);

        when(mapper.toEntity(request)).thenReturn(entity);
        when(service.createDroitCommunication(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        DroitCommunicationResponse result = facade.createDroitCommunication(request);

        assertThat(result).isEqualTo(response);
        verify(mapper).toEntity(request);
        verify(service).createDroitCommunication(entity);
        verify(mapper).toResponse(entity);
    }

    @Test
    void getById_devrait_mapper_le_resultat() {
        DroitCommunicationEntity entity = TestFixtures.entity();
        DroitCommunicationResponse response = responseFrom(entity);

        when(service.getById(1L)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        assertThat(facade.getById(1L)).isEqualTo(response);
    }

    @Test
    void getAll_devrait_mapper_chaque_element() {
        DroitCommunicationEntity entity = TestFixtures.entity();
        DroitCommunicationResponse response = responseFrom(entity);

        when(service.getAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(any())).thenReturn(response);

        assertThat(facade.getAll()).containsExactly(response);
    }

    private DroitCommunicationResponse responseFrom(DroitCommunicationEntity e) {
        return new DroitCommunicationResponse(
                e.getId(), e.getOrganisme(), e.getAdressePostale(), e.getComplementAdresse(),
                e.getObjetDemande(), e.getCanalDemande(), e.getReferenceDemande(),
                e.getDateReception(), e.getTypesDemande(), e.getDocumentReference(),
                e.getDocumentNom(), e.getDocumentContentType(), e.getStatut(),
                e.getDateCreation(), e.getDateModification());
    }
}
