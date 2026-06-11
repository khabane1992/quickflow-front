package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster ce package (service ou service.derogation.impl) selon l'emplacement réel
// de DerogationRequestServiceImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationStatus;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.exceptions.ResourceNotFoundException;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.DerogationRequestRepository;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.WfTaskRepository;
// TODO : ajuster les imports selon les packages réels (bpm, mapper outbox, OutboxPublisher,
// StartDerggTarifRetWfInstanceCommand, DerogTarifRetWfInstanceBuilder, WfInstanceManager)
import com.bnpparibas.irb.qlickflow.bpm.outbox.OutboxPublisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DerogationRequestServiceImplTest {

    @Mock
    private WfInstanceManager wfInstanceManager;
    @Mock
    private UserService userService;
    @Mock
    private DerogTarifRetWfInstanceBuilder derogTarifRetWfInstanceBuilder;
    @Mock
    private DerogationRequestRepository derogationRequestRepository;
    @Mock
    private WfTaskService wfTaskService;
    @Mock
    private WfTaskRepository wfTaskRepository;
    @Mock
    private StartProcessByConseillerOutboxEventMapper startProcessByConseillerOutboxEventMapper;
    @Mock
    private StartProcessByDAOutboxEventMapper startProcessByDAOutboxEventMapper;
    @Mock
    private OutboxPublisher outboxPublisher;

    @InjectMocks
    private DerogationRequestServiceImpl service;

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private UserEntity user(String uid, ProfileEnum profile) {
        return UserEntity.builder()
                .uid(uid)
                .email(uid + "@bnpparibas.com")
                .firstName("First-" + uid)
                .lastName("Last-" + uid)
                .profile(profile)
                .build();
    }

    private DerogationRequest derogWithLatestTask(Long taskId) {
        WfTask latestTask = WfTask.builder()
                .id(taskId)
                .status(WfTask.WfTaskStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build();
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .status(DerogationStatus.VALIDATION_AGENCE.name())
                .build();
        derog.setLatestTask(latestTask);
        return derog;
    }

    private void stubSaveEcho() {
        when(derogationRequestRepository.save(any(DerogationRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    // ------------------------------------------------------------------
    // getById / getByBusinessKey
    // ------------------------------------------------------------------

    @Test
    void getById_found_returnsEntity() {
        UUID id = UUID.randomUUID();
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestRepository.findById(id)).thenReturn(Optional.of(derog));

        assertThat(service.getById(id)).isEqualTo(derog);
    }

    @Test
    void getById_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(derogationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void getByBusinessKey_found_returnsEntity() {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestRepository.findByBusinessKey("BK-001")).thenReturn(Optional.of(derog));

        assertThat(service.getByBusinessKey("BK-001")).isEqualTo(derog);
    }

    @Test
    void getByBusinessKey_notFound_throwsResourceNotFound() {
        when(derogationRequestRepository.findByBusinessKey("BK-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByBusinessKey("BK-404"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("BK-404");
    }

    // ------------------------------------------------------------------
    // saveDerogationRequest (délégation)
    // ------------------------------------------------------------------

    @Test
    void saveDerogationRequest_delegatesToRepository() {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestRepository.save(derog)).thenReturn(derog);

        assertThat(service.saveDerogationRequest(derog)).isEqualTo(derog);
    }

    // ------------------------------------------------------------------
    // submit / reject / retourACharge / retourPourComplementInfos
    // (chaque méthode : completeCurrentTask avec la bonne décision + statut SUBMITTED + save)
    // ------------------------------------------------------------------

    @Test
    void submit_completesTaskWithOkAndSubmits() {
        DerogationRequest derog = derogWithLatestTask(5L);
        stubSaveEcho();

        service.submit(derog);

        verify(wfTaskService).completeTask(5L, "OK");
        assertThat(derog.getStatus()).isEqualTo(DerogationStatus.SUBMITTED.name());
        verify(derogationRequestRepository).save(derog);
    }

    @Test
    void reject_completesTaskWithKoAndSubmits() {
        DerogationRequest derog = derogWithLatestTask(6L);
        stubSaveEcho();

        service.reject(derog);

        verify(wfTaskService).completeTask(6L, "KO");
        assertThat(derog.getStatus()).isEqualTo(DerogationStatus.SUBMITTED.name());
    }

    @Test
    void retourACharge_completesTaskWithRetourCharge() {
        DerogationRequest derog = derogWithLatestTask(7L);
        stubSaveEcho();

        service.retourACharge(derog);

        verify(wfTaskService).completeTask(7L, "RETOUR_CHARGE");
        assertThat(derog.getStatus()).isEqualTo(DerogationStatus.SUBMITTED.name());
    }

    @Test
    void retourPourComplementInfos_completesTaskWithBesoinInfo() {
        DerogationRequest derog = derogWithLatestTask(8L);
        stubSaveEcho();

        service.retourPourComplementInfos(derog);

        verify(wfTaskService).completeTask(8L, "BESOIN_INFO_COMPLEMENTAIRES");
        assertThat(derog.getStatus()).isEqualTo(DerogationStatus.SUBMITTED.name());
    }

    @Test
    void completeCurrentTask_delegatesToWfTaskService() {
        DerogationRequest derog = derogWithLatestTask(9L);

        service.completeCurrentTask(derog, "OK");

        verify(wfTaskService).completeTask(9L, "OK");
    }

    // ------------------------------------------------------------------
    // closeDerogationDraft
    // ------------------------------------------------------------------

    @Test
    void closeDerogationDraft_closesTasksAndRequest() {
        UUID id = UUID.randomUUID();
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .status(DerogationStatus.DRAFT.name())
                .build();
        when(derogationRequestRepository.findById(id)).thenReturn(Optional.of(derog));
        WfTask t1 = WfTask.builder().status(WfTask.WfTaskStatus.CREATED).createdAt(OffsetDateTime.now()).build();
        WfTask t2 = WfTask.builder().status(WfTask.WfTaskStatus.COMPLETE_SUBMITTED).createdAt(OffsetDateTime.now()).build();
        when(wfTaskRepository.findAllByWfInstanceBusinessKey("BK-001")).thenReturn(List.of(t1, t2));
        stubSaveEcho();

        service.closeDerogationDraft(id);

        assertThat(t1.getStatus()).isEqualTo(WfTask.WfTaskStatus.COMPLETED);
        assertThat(t2.getStatus()).isEqualTo(WfTask.WfTaskStatus.COMPLETED);
        verify(wfTaskRepository).saveAll(List.of(t1, t2));
        assertThat(derog.getStatus()).isEqualTo(DerogationStatus.CLOSED.name());
        assertThat(derog.getClosedDate()).isEqualTo(LocalDate.now());
        verify(derogationRequestRepository).save(derog);
    }

    @Test
    void closeDerogationDraft_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(derogationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.closeDerogationDraft(id))
                .isInstanceOf(ResourceNotFoundException.class);
        verifyNoInteractions(wfTaskRepository);
    }

    // ------------------------------------------------------------------
    // updateStatus(businessKey, status)
    // ------------------------------------------------------------------

    @Test
    void updateStatus_found_updatesAndSaves() {
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .status(DerogationStatus.SUBMITTED.name())
                .build();
        when(derogationRequestRepository.findByBusinessKey("BK-001")).thenReturn(Optional.of(derog));
        stubSaveEcho();

        service.updateStatus("BK-001", DerogationStatus.VALIDATION_DIE);

        assertThat(derog.getStatus()).isEqualTo(DerogationStatus.VALIDATION_DIE.name());
        assertThat(derog.getClosedDate()).isNull(); // statut non final
        verify(derogationRequestRepository).save(derog);
    }

    @Test
    void updateStatus_finalStatus_alsoSetsClosedDate() {
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .status(DerogationStatus.VALIDATION_LMR.name())
                .build();
        when(derogationRequestRepository.findByBusinessKey("BK-001")).thenReturn(Optional.of(derog));
        stubSaveEcho();

        service.updateStatus("BK-001", DerogationStatus.APPROVED);

        assertThat(derog.getStatus()).isEqualTo(DerogationStatus.APPROVED.name());
        assertThat(derog.getClosedDate()).isEqualTo(LocalDate.now()); // branche statut final de l'entité
    }

    @Test
    void updateStatus_unknownBusinessKey_throwsResourceNotFound() {
        when(derogationRequestRepository.findByBusinessKey("BK-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus("BK-404", DerogationStatus.CLOSED))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ------------------------------------------------------------------
    // assignTask
    // ------------------------------------------------------------------

    @Test
    void assignTask_delegatesToWfTaskServiceWithLatestTask() {
        DerogationRequest derog = derogWithLatestTask(11L);
        UserEntity assignee = user("u1", ProfileEnum.CONSEILLER);
        UserEntity assignedBy = user("u2", ProfileEnum.DA);

        service.assignTask(derog, assignee, assignedBy);

        verify(wfTaskService).assignTask(derog.getLatestTask(), assignee, assignedBy);
    }

    // ------------------------------------------------------------------
    // startDerogTarifRetWf (3 branches : CONSEILLER / DA / autre → throw)
    // ------------------------------------------------------------------

    @Test
    void startDerogTarifRetWf_initiateurConseiller_publishesConseillerEvent() {
        StartDerggTarifRetWfInstanceCommand command = StartDerggTarifRetWfInstanceCommand.builder().build();
        DerogationRequest entity = DerogationRequest.builder()
                .businessKey("BK-001")
                .initiateur(user("c1", ProfileEnum.CONSEILLER))
                .build();
        when(derogTarifRetWfInstanceBuilder.build(command)).thenReturn(entity);

        DerogationRequest result = service.startDerogTarifRetWf(command);

        assertThat(result).isNotNull();
        verify(startProcessByConseillerOutboxEventMapper).map(any());
        verify(outboxPublisher).publish(any());
        verifyNoInteractions(startProcessByDAOutboxEventMapper);
    }

    @Test
    void startDerogTarifRetWf_initiateurDa_publishesDaEvent() {
        StartDerggTarifRetWfInstanceCommand command = StartDerggTarifRetWfInstanceCommand.builder().build();
        DerogationRequest entity = DerogationRequest.builder()
                .businessKey("BK-001")
                .initiateur(user("da1", ProfileEnum.DA))
                .build();
        when(derogTarifRetWfInstanceBuilder.build(command)).thenReturn(entity);

        service.startDerogTarifRetWf(command);

        verify(startProcessByDAOutboxEventMapper).map(any());
        verify(outboxPublisher).publish(any());
        verifyNoInteractions(startProcessByConseillerOutboxEventMapper);
    }

    @Test
    void startDerogTarifRetWf_otherProfile_throwsIllegalArgument() {
        StartDerggTarifRetWfInstanceCommand command = StartDerggTarifRetWfInstanceCommand.builder().build();
        DerogationRequest entity = DerogationRequest.builder()
                .businessKey("BK-001")
                .initiateur(user("lmr1", ProfileEnum.LMR))
                .build();
        when(derogTarifRetWfInstanceBuilder.build(command)).thenReturn(entity);

        assertThatThrownBy(() -> service.startDerogTarifRetWf(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Conseiller ou DA");
        verifyNoInteractions(outboxPublisher);
    }

    // ------------------------------------------------------------------
    // Recherches (délégation repository.findAll(Specification))
    // ------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    void getDerogationsToProcess_delegatesToRepositoryWithSpec() {
        UserEntity currentUser = user("u1", ProfileEnum.CONSEILLER);
        List<DerogationRequest> expected = List.of(DerogationRequest.builder().businessKey("BK-001").build());
        when(derogationRequestRepository.findAll(any(Specification.class))).thenReturn(expected);

        List<DerogationRequest> result =
                service.getDerogationsToProcess(Set.of(currentUser), currentUser, "filtre");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getFollowUpDerogations_delegatesToRepositoryWithSpec() {
        when(derogationRequestRepository.findAll(any(Specification.class))).thenReturn(List.of());

        assertThat(service.getFollowUpDerogations(user("u1", ProfileEnum.DA), null)).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getFinalizedDerogations_delegatesToRepositoryWithSpec() {
        when(derogationRequestRepository.findAll(any(Specification.class))).thenReturn(List.of());

        assertThat(service.getFinalizedDerogations(user("u1", ProfileEnum.DA), "abc")).isEmpty();
    }
}
