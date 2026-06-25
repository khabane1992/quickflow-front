package com.bnpparibas.irb.droitscommunication.service;

import com.bnpparibas.irb.droitscommunication.batch.TraitementSabBatchConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

/**
 * Lance le job {@code traitementSabJob} pour une demande donnée.
 * En Phase 2 l'appel est synchrone (déclenché par notre endpoint) ; en Phase 3 c'est
 * une tâche de service Camunda qui déclenchera ce lancement.
 */
@Service
public class TraitementSabLauncher {

    private final JobLauncher jobLauncher;
    private final Job traitementSabJob;

    public TraitementSabLauncher(JobLauncher jobLauncher, Job traitementSabJob) {
        this.jobLauncher = jobLauncher;
        this.traitementSabJob = traitementSabJob;
    }

    public void lancer(Long demandeId) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("demandeId", demandeId)
                    .addLong("ts", System.currentTimeMillis()) // unicité d'exécution
                    .toJobParameters();
            jobLauncher.run(traitementSabJob, params);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Échec du lancement du " + TraitementSabBatchConfig.JOB_NAME
                            + " pour la demande " + demandeId, e);
        }
    }
}
