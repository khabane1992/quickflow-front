package com.bnpparibas.irb.droitscommunication.camunda;

import java.util.Map;
import java.util.UUID;

/**
 * Port vers le moteur <b>Camunda 7 externe</b> (consommé via son API REST/OpenAPI).
 * Orchestration du flow de validation ; notre statut local reste une projection.
 *
 * <p>Impl. courante : {@link CamundaProcessStub}. L'impl. réelle utilisera un client
 * REST (RestClient / client généré) vers {@code app.camunda.base-url}.
 */
public interface CamundaProcessPort {

    /** Démarre une instance de process pour la demande (businessKey = clé métier). */
    void demarrer(UUID businessKey, Map<String, Object> variables);

    /**
     * Fait avancer le process en complétant la tâche utilisateur courante.
     *
     * @param businessKey clé métier (identifie l'instance)
     * @param action      action métier ("ENVOYER_VALIDATION", "ANNULER", "VALIDER", "REJETER")
     * @param variables   variables à transmettre au process
     */
    void completerTache(UUID businessKey, String action, Map<String, Object> variables);

    /** Corrèle un message au process (ex. "sabTermine" en fin de batch). */
    void correlerMessage(UUID businessKey, String message, Map<String, Object> variables);
}
