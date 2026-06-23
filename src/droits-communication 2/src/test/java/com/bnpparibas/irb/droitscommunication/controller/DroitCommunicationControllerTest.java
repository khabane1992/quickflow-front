package com.bnpparibas.irb.droitscommunication.controller;

import com.bnpparibas.irb.droitscommunication.TestFixtures;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationResponse;
import com.bnpparibas.irb.droitscommunication.exception.RessourceIntrouvableException;
import com.bnpparibas.irb.droitscommunication.facade.DroitCommunicationFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DroitCommunicationControllerTest {

    @Mock
    private DroitCommunicationFacade facade;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new DroitCommunicationController(facade))
                .setControllerAdvice(new com.bnpparibas.irb.droitscommunication.exception.GlobalExceptionHandler())
                .build();
    }

    private DroitCommunicationResponse response() {
        var e = TestFixtures.entity();
        return new DroitCommunicationResponse(
                e.getId(), e.getOrganisme(), e.getAdressePostale(), e.getComplementAdresse(),
                e.getObjetDemande(), e.getCanalDemande(), e.getReferenceDemande(),
                e.getDateReception(), e.getTypesDemande(), e.getDocumentReference(),
                e.getDocumentNom(), e.getDocumentContentType(), e.getStatut(),
                e.getDateCreation(), e.getDateModification());
    }

    @Test
    void create_devrait_retourner_201() throws Exception {
        DroitCommunicationRequest request = TestFixtures.request();
        when(facade.createDroitCommunication(any())).thenReturn(response());

        mockMvc.perform(post("/api/droits-communication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.organisme").value("DGFIP"));
    }

    @Test
    void create_devrait_retourner_400_si_organisme_manquant() throws Exception {
        // organisme null -> viole @NotBlank
        String invalidJson = """
                {
                  "adressePostale": "12 rue de la Paix",
                  "objetDemande": "Test",
                  "canalDemande": "COURRIER",
                  "dateReception": "2026-06-20",
                  "typesDemande": ["SOLDE"]
                }
                """;

        mockMvc.perform(post("/api/droits-communication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.organisme").exists());
    }

    @Test
    void create_devrait_retourner_400_si_types_vides() throws Exception {
        String invalidJson = """
                {
                  "organisme": "DGFIP",
                  "adressePostale": "12 rue de la Paix",
                  "objetDemande": "Test",
                  "canalDemande": "COURRIER",
                  "dateReception": "2026-06-20",
                  "typesDemande": []
                }
                """;

        mockMvc.perform(post("/api/droits-communication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.typesDemande").exists());
    }

    @Test
    void getById_devrait_retourner_200() throws Exception {
        when(facade.getById(1L)).thenReturn(response());

        mockMvc.perform(get("/api/droits-communication/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_devrait_retourner_404_si_introuvable() throws Exception {
        when(facade.getById(eq(99L)))
                .thenThrow(new RessourceIntrouvableException("Demande introuvable pour l'id 99"));

        mockMvc.perform(get("/api/droits-communication/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getAll_devrait_retourner_la_liste() throws Exception {
        when(facade.getAll()).thenReturn(List.of(response()));

        mockMvc.perform(get("/api/droits-communication"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
