package com.bnpparibas.irb.droitscommunication.camunda;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Implémentation de simulation du {@link CamundaProcessPort} : trace les interactions,
 * le temps de brancher le vrai moteur Camunda externe (client REST/OpenAPI).
 *
 * <p>Le statut de la demande est, en attendant, piloté par nos endpoints (projection
 * locale). L'impl. réelle déléguera l'autorité du cycle de vie au moteur.
 */
@Component
@Slf4j
public class CamundaProcessStub implements CamundaProcessPort {

    @Override
    public void demarrer(UUID businessKey, Map<String, Object> variables) {
        log.info("[STUB Camunda] start process businessKey={} variables={}", businessKey, variables);
    }

    @Override
    public void completerTache(UUID businessKey, String action, Map<String, Object> variables) {
        log.info("[STUB Camunda] complete task businessKey={} action={} variables={}",
                businessKey, action, variables);
    }

    @Override
    public void correlerMessage(UUID businessKey, String message, Map<String, Object> variables) {
        log.info("[STUB Camunda] correlate message='{}' businessKey={} variables={}",
                message, businessKey, variables);
    }
}
