package com.bnpparibas.irb.droitscommunication.batch;

import com.bnpparibas.irb.droitscommunication.document.DocumentContentPort;
import com.bnpparibas.irb.droitscommunication.entity.ClientDemande;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.entity.LigneDemandeStaging;
import com.bnpparibas.irb.droitscommunication.enums.Coherence;
import com.bnpparibas.irb.droitscommunication.enums.DecisionClient;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.enums.StatutLigne;
import com.bnpparibas.irb.droitscommunication.dto.LigneFichierClient;
import com.bnpparibas.irb.droitscommunication.repository.ClientDemandeRepository;
import com.bnpparibas.irb.droitscommunication.repository.DroitCommunicationRepository;
import com.bnpparibas.irb.droitscommunication.repository.LigneDemandeStagingRepository;
import com.bnpparibas.irb.droitscommunication.sab.SabClient;
import com.bnpparibas.irb.droitscommunication.sab.SabResultat;
import com.bnpparibas.irb.droitscommunication.service.FichierClientParser;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Job Spring Batch "traitementSabJob" :
 * <ol>
 *   <li><b>parsingStep</b> (tasklet) : télécharge le .xlsx (MS Document), le parse,
 *       remplit la table tampon et fige le nombre de clients de la demande ;</li>
 *   <li><b>sabStep</b> (chunk) : lit la table tampon par lots et envoie chaque lot à SAB,
 *       consolide les résultats en {@link ClientDemande} et marque les lignes TRAITE.</li>
 * </ol>
 * À la fin, la demande passe en {@code RESULTATS_SAB}.
 */
@Configuration
@Slf4j
public class TraitementSabBatchConfig {

    public static final String JOB_NAME = "traitementSabJob";

    // ----------------------------------------------------------------------
    // Job + steps
    // ----------------------------------------------------------------------

    @Bean
    public Job traitementSabJob(JobRepository jobRepository,
                                Step parsingStep,
                                Step sabStep,
                                JobExecutionListener traitementSabJobListener) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .listener(traitementSabJobListener)
                .start(parsingStep)
                .next(sabStep)
                .build();
    }

    @Bean
    public Step parsingStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            Tasklet parsingTasklet) {
        return new StepBuilder("parsingStep", jobRepository)
                .tasklet(parsingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step sabStep(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager,
                        JpaPagingItemReader<LigneDemandeStaging> sabReader,
                        ItemWriter<LigneDemandeStaging> sabWriter,
                        @Value("${app.sab.batch.chunk-size:100}") int chunkSize) {
        return new StepBuilder("sabStep", jobRepository)
                .<LigneDemandeStaging, LigneDemandeStaging>chunk(chunkSize, transactionManager)
                .reader(sabReader)
                .writer(sabWriter)
                .build();
    }

    // ----------------------------------------------------------------------
    // Step 1 : parsing du .xlsx -> table tampon
    // ----------------------------------------------------------------------

    @Bean
    @StepScope
    public Tasklet parsingTasklet(@Value("#{jobParameters['demandeId']}") Long demandeId,
                                  DocumentContentPort documentContentPort,
                                  FichierClientParser parser,
                                  DroitCommunicationRepository demandeRepository,
                                  LigneDemandeStagingRepository stagingRepository,
                                  ClientDemandeRepository clientRepository) {
        return (contribution, chunkContext) -> {
            DroitCommunicationEntity demande = demandeRepository.findById(demandeId)
                    .orElseThrow(() -> new IllegalStateException("Demande introuvable : " + demandeId));

            // Idempotence : on repart d'un état propre si le job est relancé.
            clientRepository.deleteByDemandeId(demandeId);
            stagingRepository.deleteByDemandeId(demandeId);

            byte[] contenu = documentContentPort.telecharger(demande.getDocumentId());
            List<LigneFichierClient> lignes = parser.parser(contenu);

            List<LigneDemandeStaging> staging = lignes.stream()
                    .map(l -> LigneDemandeStaging.builder()
                            .demandeId(demandeId)
                            .numeroLigne(l.numeroLigne())
                            .nomRaisonSociale(l.nomRaisonSociale())
                            .cin(l.cin())
                            .numeroRc(l.numeroRc())
                            .statutLigne(StatutLigne.EN_ATTENTE)
                            .build())
                    .toList();
            stagingRepository.saveAll(staging);

            demande.setNombreClients(staging.size());
            demandeRepository.save(demande);

            log.info("[BATCH] demande {} : {} ligne(s) parsée(s) en table tampon", demandeId, staging.size());
            return RepeatStatus.FINISHED;
        };
    }

    // ----------------------------------------------------------------------
    // Step 2 : table tampon -> SAB (par chunks) -> ClientDemande
    // ----------------------------------------------------------------------

    @Bean
    @StepScope
    public JpaPagingItemReader<LigneDemandeStaging> sabReader(EntityManagerFactory emf,
                                                             @Value("#{jobParameters['demandeId']}") Long demandeId,
                                                             @Value("${app.sab.batch.chunk-size:100}") int chunkSize) {
        // NB : on filtre uniquement sur demandeId (pas sur le statut de ligne). Le writer
        // passe les lignes à TRAITE, et filtrer dessus décalerait l'offset de pagination
        // (lignes sautées). Le parsing étant idempotent, lire toutes les lignes est sûr.
        return new JpaPagingItemReaderBuilder<LigneDemandeStaging>()
                .name("sabReader")
                .entityManagerFactory(emf)
                .pageSize(chunkSize)
                .queryString("select l from LigneDemandeStaging l where l.demandeId = :demandeId order by l.id")
                .parameterValues(Map.of("demandeId", demandeId))
                .build();
    }

    @Bean
    public ItemWriter<LigneDemandeStaging> sabWriter(SabClient sabClient,
                                                     ClientDemandeRepository clientRepository,
                                                     LigneDemandeStagingRepository stagingRepository) {
        return chunk -> {
            List<LigneDemandeStaging> lignes = new ArrayList<>(chunk.getItems());
            List<SabResultat> resultats = sabClient.consulter(lignes);

            List<ClientDemande> clients = new ArrayList<>(lignes.size());
            for (int i = 0; i < lignes.size(); i++) {
                LigneDemandeStaging ligne = lignes.get(i);
                SabResultat r = resultats.get(i);
                clients.add(ClientDemande.builder()
                        .demandeId(ligne.getDemandeId())
                        .nomFourni(ligne.getNomRaisonSociale())
                        .cin(ligne.getCin())
                        .numeroRc(ligne.getNumeroRc())
                        .idSab(r.idSab())
                        .nomSab(r.nomSab())
                        .coherence(r.coherence())
                        .typeIncoherence(r.typeIncoherence())
                        .numeroCompte(r.numeroCompte())
                        .solde(r.solde())
                        .position(r.position())
                        .adresse(r.adresse())
                        .telephone(r.telephone())
                        // Décision par défaut : non conforme si incohérent, conforme sinon.
                        .decision(r.coherence() == Coherence.INCOHERENT
                                ? DecisionClient.NON_CONFORME : DecisionClient.CONFORME)
                        .build());
                ligne.setStatutLigne(StatutLigne.TRAITE);
            }
            clientRepository.saveAll(clients);
            stagingRepository.saveAll(lignes);
        };
    }

    // ----------------------------------------------------------------------
    // Fin de job -> statut RESULTATS_SAB
    // ----------------------------------------------------------------------

    @Bean
    public JobExecutionListener traitementSabJobListener(DroitCommunicationRepository demandeRepository) {
        return new JobExecutionListener() {
            @Override
            public void afterJob(org.springframework.batch.core.JobExecution jobExecution) {
                if (jobExecution.getStatus() != BatchStatus.COMPLETED) {
                    return;
                }
                Long demandeId = jobExecution.getJobParameters().getLong("demandeId");
                demandeRepository.findById(demandeId).ifPresent(d -> {
                    d.setStatut(StatutDemande.RESULTATS_SAB);
                    demandeRepository.save(d);
                    log.info("[BATCH] demande {} -> RESULTATS_SAB", demandeId);
                    // Phase 3 : corréler le message "sabTermine" au process Camunda (businessKey).
                });
            }
        };
    }
}
