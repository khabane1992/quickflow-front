package com.bnpparibas.irb.droitscommunication.facade;

import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.mapper.DroitCommunicationMapper;
import com.bnpparibas.irb.droitscommunication.service.DroitCommunicationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DroitCommunicationFacadeImpl implements DroitCommunicationFacade {

    private final DroitCommunicationService service;
    private final DroitCommunicationMapper mapper;

    public DroitCommunicationFacadeImpl(DroitCommunicationService service,
                                        DroitCommunicationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public DroitCommunicationResponse createDroitCommunication(DroitCommunicationRequest request) {
        DroitCommunicationEntity entity = mapper.toEntity(request);
        DroitCommunicationEntity saved = service.createDroitCommunication(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public DroitCommunicationResponse getById(Long id) {
        return mapper.toResponse(service.getById(id));
    }

    @Override
    public List<DroitCommunicationResponse> getAll() {
        return service.getAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
