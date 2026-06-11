package com.bnpparibas.irb.qlickflow.wfdtrp.facade;
// ⚠️ TODO : DerogationAuthorizationService est dans le package facade (confirmé) — ajuster si besoin.

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationStatus;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfUserDetails;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.exceptions.UnauthorizedOperationException;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.UserService;
// TODO : ajuster les imports ci-dessus selon les packages réels
// (WfTask / WfTaskAssignment : package bpm ou entities — voir imports plus bas)

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DerogationAuthorizationServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private DerogationAuthorizationService authorizationService;

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private UserEntity user(String uid) {
        return UserEntity.builder()
                .uid(uid)
                .email(uid + "@bnpparibas.com")
                .firstName("First-" + uid)
                .lastName("Last-" + uid)
                .build();
    }

    private QfUserDetails details(String uid, ProfileEnum profile) {
        return QfUserDetails.builder()
                .uid(uid)
                .email(uid + "@bnpparibas.com")
                .profile(profile)
                .build();
    }

    /** Construit une DerogationRequest dont latestTask.currentAssignment.assignee = assignee. */
    private DerogationRequest derogAssignedTo(UserEntity assignee) {
        // TODO : ajuster les imports WfTask / WfTaskAssignment selon le package réel (bpm ou entities)
        com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTaskAssignment assignment =
                com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTaskAssignment.builder()
                        .assignee(assignee)
                        .assignedBy(assignee)
                        .build();
        com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask task =
                com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask.builder()
                        .currentAssignment(assignment)
                        .build();
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .status(DerogationStatus.SUBMITTED.name())
                .build();
        derog.setLatestTask(task);
        return derog;
    }

    // ------------------------------------------------------------------
    // validateCurrentUserCanInitOrCreateDerog
    // ------------------------------------------------------------------

    @Test
    void validateCanInitOrCreate_conseiller_passes() {
        when(userService.getCurrentUserDetails()).thenReturn(details("u1", ProfileEnum.CONSEILLER));

        assertThatCode(() -> authorizationService.validateCurrentUserCanInitOrCreateDerog())
                .doesNotThrowAnyException();
    }

    @Test
    void validateCanInitOrCreate_da_passes() {
        when(userService.getCurrentUserDetails()).thenReturn(details("u1", ProfileEnum.DA));

        assertThatCode(() -> authorizationService.validateCurrentUserCanInitOrCreateDerog())
                .doesNotThrowAnyException();
    }

    @Test
    void validateCanInitOrCreate_lmr_throws() {
        when(userService.getCurrentUserDetails()).thenReturn(details("u1", ProfileEnum.LMR));

        assertThatThrownBy(() -> authorizationService.validateCurrentUserCanInitOrCreateDerog())
                .isInstanceOf(UnauthorizedOperationException.class);
    }

    @Test
    void validateCanInitOrCreate_dz_throws() {
        when(userService.getCurrentUserDetails()).thenReturn(details("u1", ProfileEnum.DZ));

        assertThatThrownBy(() -> authorizationService.validateCurrentUserCanInitOrCreateDerog())
                .isInstanceOf(UnauthorizedOperationException.class);
    }

    // ------------------------------------------------------------------
    // validateCurrentUserIsInitiateur
    // ------------------------------------------------------------------

    @Test
    void validateIsInitiateur_sameUser_passes() {
        UserEntity initiateur = user("u1");
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .initiateur(initiateur)
                .build();
        // equals de UserEntity ne porte que sur uid → une autre instance avec le même uid est égale
        when(userService.getCurrentUser()).thenReturn(user("u1"));

        assertThatCode(() -> authorizationService.validateCurrentUserIsInitiateur(derog))
                .doesNotThrowAnyException();
    }

    @Test
    void validateIsInitiateur_differentUser_throws() {
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .initiateur(user("u1"))
                .build();
        when(userService.getCurrentUser()).thenReturn(user("u2"));

        assertThatThrownBy(() -> authorizationService.validateCurrentUserIsInitiateur(derog))
                .isInstanceOf(UnauthorizedOperationException.class);
    }

    // ------------------------------------------------------------------
    // validateCurrentUserCanProcess
    // ------------------------------------------------------------------

    @Test
    void validateCanProcess_currentUserIsAssignee_passes() {
        UserEntity assignee = user("u1");
        DerogationRequest derog = derogAssignedTo(assignee);
        when(userService.resolveWorkflowUserFromCurrentSession()).thenReturn(user("u1"));
        when(userService.resolveWorkflowUserFrom(assignee)).thenReturn(user("u1"));

        assertThatCode(() -> authorizationService.validateCurrentUserCanProcess(derog))
                .doesNotThrowAnyException();
    }

    @Test
    void validateCanProcess_currentUserIsNotAssignee_throws() {
        UserEntity assignee = user("u1");
        DerogationRequest derog = derogAssignedTo(assignee);
        when(userService.resolveWorkflowUserFromCurrentSession()).thenReturn(user("u2"));
        when(userService.resolveWorkflowUserFrom(assignee)).thenReturn(user("u1"));

        assertThatThrownBy(() -> authorizationService.validateCurrentUserCanProcess(derog))
                .isInstanceOf(UnauthorizedOperationException.class);
    }

    // ------------------------------------------------------------------
    // validateDerogStatus
    // ------------------------------------------------------------------

    @Test
    void validateDerogStatus_expectedStatus_passes() {
        DerogationRequest derog = DerogationRequest.builder()
                .status(DerogationStatus.REVUE.name())
                .build();

        assertThatCode(() -> authorizationService.validateDerogStatus(derog, DerogationStatus.REVUE))
                .doesNotThrowAnyException();
    }

    @Test
    void validateDerogStatus_unexpectedStatus_throws() {
        DerogationRequest derog = DerogationRequest.builder()
                .status(DerogationStatus.SUBMITTED.name())
                .build();

        assertThatThrownBy(() -> authorizationService.validateDerogStatus(derog, DerogationStatus.REVUE))
                .isInstanceOf(UnauthorizedOperationException.class);
    }
}
