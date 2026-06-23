package com.bnpparibas.irb.droitscommunication.controller;

import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.facade.DroitCommunicationFacade;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API des demandes de droit de communication.
 */
@RestController
@RequestMapping("/api/droits-communication")
public class DroitCommunicationController {

    private final DroitCommunicationFacade facade;

    public DroitCommunicationController(DroitCommunicationFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DroitCommunicationResponse create(@Valid @RequestBody DroitCommunicationRequest request) {
        return facade.createDroitCommunication(request);
    }

    @GetMapping("/{id}")
    public DroitCommunicationResponse getById(@PathVariable Long id) {
        return facade.getById(id);
    }

    @GetMapping
    public List<DroitCommunicationResponse> getAll() {
        return facade.getAll();
    }
}
