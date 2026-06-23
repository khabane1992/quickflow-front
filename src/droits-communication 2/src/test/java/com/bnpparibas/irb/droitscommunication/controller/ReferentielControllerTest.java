package com.bnpparibas.irb.droitscommunication.controller;

import com.bnpparibas.irb.droitscommunication.dto.ReferentielItem;
import com.bnpparibas.irb.droitscommunication.referentiel.ReferentielService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReferentielControllerTest {

    @Mock
    private ReferentielService referentielService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReferentielController(referentielService))
                .build();
    }

    @Test
    void organismes() throws Exception {
        when(referentielService.getOrganismes())
                .thenReturn(List.of(new ReferentielItem("DGFIP", "Direction Générale des Finances Publiques")));

        mockMvc.perform(get("/api/referentiels/organismes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("DGFIP"));
    }

    @Test
    void complementsAdresse() throws Exception {
        when(referentielService.getComplementsAdresse())
                .thenReturn(List.of(new ReferentielItem("BAT", "Bâtiment")));

        mockMvc.perform(get("/api/referentiels/complements-adresse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("BAT"));
    }

    @Test
    void canauxDemande() throws Exception {
        when(referentielService.getCanauxDemande())
                .thenReturn(List.of(new ReferentielItem("COURRIER", "Courrier")));

        mockMvc.perform(get("/api/referentiels/canaux-demande"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("COURRIER"));
    }

    @Test
    void typesDemande() throws Exception {
        when(referentielService.getTypesDemande())
                .thenReturn(List.of(new ReferentielItem("SOLDE", "Solde")));

        mockMvc.perform(get("/api/referentiels/types-demande"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("SOLDE"));
    }
}
