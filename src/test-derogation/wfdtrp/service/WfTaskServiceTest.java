package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster ce package pour qu'il corresponde EXACTEMENT au package de WfTaskService
// (probablement service ou bpm).

import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask;
import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTaskAssignment;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.WfTaskRepository;
// TODO : ajuster les imports selon les packages réels :
// - WfTask / WfTaskAssignment : bpm ou entities
// - mappers outbox + OutboxPublisher : bpm.outbox probablement
import com.bnpparibas.irb.qlickflow.bpm.outbox.OutboxPublisher;
// import des mappers — adapter le package :
// import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.CompleteTaskOutboxEventMapper;
// import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.AssignTaskOutboxEventMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Stratégie "interactions only" pour les mappers outbox (signatures non capturées) :
 * on ne stubbe JAMAIS le retour de map() — on vérifie uniquement que map() et publish()
 * sont appelés. any() de Mockito matche aussi null, donc publish(null) est vérifiable.
 */
@ExtendWith(MockitoExtension.class)
class WfTaskServiceTest {

    @Mock
    private WfTaskRepository wfTaskRepository;
    @Mock
    private CompleteTaskOutboxEventMapper completeTaskOutboxEventMapper;
    @Mock
    private AssignTaskOutboxEventMapper assignTaskOutboxEventMapper;
    @Mock
    private OutboxPublisher outboxPublisher;

    @InjectMocks
    private WfTaskService wfTaskService;

    private UserEntity user(String uid) {
        return UserEntity.builder()
                .uid(uid)
                .email(uid + "@bnpparibas.com")
                .firstName("First-" + uid)
                .lastName("Last-" + uid)
                .build();
    }

    private WfTask createdTask() {
        return WfTask.builder()
                .status(WfTask.WfTaskStatus.CREATED)
                .engineTaskId("engine-1")
                .name("Validation")
                .createdAt(OffsetDateTime.now())
                .assignments(new LinkedHashSet<>()) // @Builder ne conserve pas l'initialisation de champ
                .build();
    }

    // ------------------------------------------------------------------
    // completeTask
    // ------------------------------------------------------------------

    @Test
    void completeTask_existingTask_marksCompleteSubmittedAndPublishes() {
        WfTask task = createdTask();
        when(wfTaskRepository.findById(42L)).thenReturn(Optional.of(task));

        wfTaskService.completeTask(42L, "OK");

        assertThat(task.getStatus()).isEqualTo(WfTask.WfTaskStatus.COMPLETE_SUBMITTED);
        assertThat(task.getUpdatedAt()).isNotNull();
        verify(wfTaskRepository).save(task);
        verify(completeTaskOutboxEventMapper).map(any());
        verify(outboxPublisher).publish(any());
    }

    @Test
    void completeTask_unknownTask_throwsIllegalArgument() {
        when(wfTaskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wfTaskService.completeTask(99L, "OK"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(completeTaskOutboxEventMapper, outboxPublisher);
    }

    // ------------------------------------------------------------------
    // assignTask
    // ------------------------------------------------------------------

    @Test
    void assignTask_createdTask_createsAssignmentAndPublishes() {
        WfTask task = createdTask();
        UserEntity assignee = user("u-assignee");
        UserEntity assignedBy = user("u-manager");

        wfTaskService.assignTask(task, assignee, assignedBy);

        WfTaskAssignment current = task.getCurrentAssignment();
        assertThat(current).isNotNull();
        assertThat(current.getAssignee()).isEqualTo(assignee);
        assertThat(current.getAssignedBy()).isEqualTo(assignedBy);
        assertThat(current.getSyncStatus()).isEqualTo(WfTaskAssignment.SyncStatus.PENDING);
        assertThat(current.getAssignedAt()).isNotNull();
        assertThat(current.getTask()).isEqualTo(task);
        assertThat(task.getAssignments()).contains(current);

        verify(wfTaskRepository).save(task);
        verify(assignTaskOutboxEventMapper).map(any());
        verify(outboxPublisher).publish(any());
    }

    @Test
    void assignTask_taskNotInCreatedStatus_throwsIllegalArgument() {
        WfTask task = createdTask();
        task.setStatus(WfTask.WfTaskStatus.COMPLETED);

        assertThatThrownBy(() -> wfTaskService.assignTask(task, user("u1"), user("u2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot assign");

        verifyNoInteractions(wfTaskRepository, assignTaskOutboxEventMapper, outboxPublisher);
    }

    @Test
    void assignTask_completeSubmittedTask_throwsIllegalArgument() {
        WfTask task = createdTask();
        task.setStatus(WfTask.WfTaskStatus.COMPLETE_SUBMITTED);

        assertThatThrownBy(() -> wfTaskService.assignTask(task, user("u1"), user("u2")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
