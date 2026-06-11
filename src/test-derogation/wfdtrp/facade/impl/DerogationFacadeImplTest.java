package com.bnpparibas.irb.qlickflow.wfdtrp.facade.impl;
// ⚠️ TODO : ajuster ce package selon l'emplacement réel de DerogationFacadeImpl (facade.impl confirmé).

import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask;
import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTaskAssignment;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CreateDerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.DerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.RetourAchargeValidationDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.SendDemandeComplementsDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.UpdateDerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.UpdateStatusRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.UserReaffectationDto;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationStatus;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfUserDetails;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.DerogationAuthorizationService;
import com.bnpparibas.irb.qlickflow.wfdtrp.integration.InternalUserDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.integration.OrgaUnitDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.DerogationRequestMapper;
import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.StartDerogTarifRetWfCommandMapper;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.DerogationRequestService;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.DocumentService;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.UserService;
// TODO : ajuster les imports selon les packages réels.

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DerogationFacadeImplTest {

    @Mock
    private DerogationRequestService derogationRequestService;
    @Mock
    private DerogationAuthorizationService derogationAuthorizationService;
    @Mock
    private DocumentService documentService;
    @Mock
    private DerogationRequestMapper derogationRequestMapper;
    @Mock
    private StartDerogTarifRetWfCommandMapper startDerogTarifRetWfCommandMapper;
    @Mock
    private UserService userService;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private DerogationFacadeImpl facade;

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private UserEntity user(String uid, ProfileEnum profile) {
        return UserEntity.builder()
                .uid(uid).email(uid + "@bnpparibas.com")
                .firstName("First-" + uid).lastName("Last-" + uid)
                .profile(profile)
                .build();
    }

    private DerogationRequest derogAssignedTo(UserEntity assignee, DerogationStatus status) {
        WfTaskAssignment assignment = WfTaskAssignment.builder()
                .assignee(assignee).assignedBy(assignee)
                .build();
        WfTask task = WfTask.builder()
                .status(WfTask.WfTaskStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .currentAssignment(assignment)
                .build();
        DerogationRequest derog = DerogationRequest.builder()
                .id(UUID.randomUUID())
                .businessKey("BK-001")
                .status(status.name())
                .build();
        derog.setLatestTask(task);
        return derog;
    }

    private QfUserDetails detailsWithOrgaUnit(String uid, ProfileEnum profile, OrgaUnitDTO orgaUnit) {
        return QfUserDetails.builder()
                .uid(uid).email(uid + "@bnpparibas.com")
                .profile(profile).orgaUnit(orgaUnit)
                .build();
    }

    private InternalUserDTO fetchedAssignee(String profileCode, OrgaUnitDTO orgaUnit) {
        InternalUserDTO dto = mock(InternalUserDTO.class, RETURNS_DEEP_STUBS);
        when(dto.getProfile().getCode()).thenReturn(profileCode);
        when(dto.getOrgaUnit()).thenReturn(orgaUnit);
        return dto;
    }

    // ------------------------------------------------------------------
    // getDerogationsToProcess / getFollowUp / getFinalized
    // ------------------------------------------------------------------

    @Test
    void getDerogationsToProcess_buildsEligibleSetAndMaps() {
        UserEntity currentUser = user("u1", ProfileEnum.DA);
        UserEntity workflowUser = user("u1", ProfileEnum.DA);
        UserEntity eligible = user("emp1", ProfileEnum.CONSEILLER);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(userService.resolveWorkflowUserFromCurrentSession()).thenReturn(workflowUser);
        when(userService.getUsersEligibleForUnassignment()).thenReturn(List.of(eligible));
        List<DerogationRequest> todoList = List.of(DerogationRequest.builder().businessKey("BK-001").build());
        when(derogationRequestService.getDerogationsToProcess(any(), eq(currentUser), eq("filtre")))
                .thenReturn(todoList);
        List<DerogationRequestDTO> expected = List.of(DerogationRequestDTO.builder().businessKey("BK-001").build());
        when(derogationRequestMapper.toDTO(todoList)).thenReturn(expected);

        List<DerogationRequestDTO> result = facade.getDerogationsToProcess("filtre");

        assertThat(result).isEqualTo(expected);
        verify(derogationRequestService).getDerogationsToProcess(
                argThat((Set<UserEntity> users) -> users.contains(currentUser) && users.contains(eligible)),
                eq(currentUser), eq("filtre"));
    }

    @Test
    void getFollowUpDerogations_resolvesWorkflowUserAndMaps() {
        UserEntity workflowUser = user("u1", ProfileEnum.LMR);
        when(userService.resolveWorkflowUserFromCurrentSession()).thenReturn(workflowUser);
        List<DerogationRequest> list = List.of();
        when(derogationRequestService.getFollowUpDerogations(workflowUser, "x")).thenReturn(list);
        when(derogationRequestMapper.toDTO(list)).thenReturn(List.of());

        assertThat(facade.getFollowUpDerogations("x")).isEmpty();
    }

    @Test
    void getFinalizedDerogations_resolvesWorkflowUserAndMaps() {
        UserEntity workflowUser = user("u1", ProfileEnum.DA);
        when(userService.resolveWorkflowUserFromCurrentSession()).thenReturn(workflowUser);
        List<DerogationRequest> list = List.of();
        when(derogationRequestService.getFinalizedDerogations(workflowUser, "y")).thenReturn(list);
        when(derogationRequestMapper.toDTO(list)).thenReturn(List.of());

        assertThat(facade.getFinalizedDerogations("y")).isEmpty();
    }

    // ------------------------------------------------------------------
    // getById
    // ------------------------------------------------------------------

    @Test
    void getById_mapsWithCurrentUser() {
        UUID id = UUID.randomUUID();
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        UserEntity currentUser = user("u1", ProfileEnum.CONSEILLER);
        when(derogationRequestService.getById(id)).thenReturn(derog);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        DerogationRequestDTO dto = DerogationRequestDTO.builder().businessKey("BK-001").build();
        when(derogationRequestMapper.toDTO(derog, currentUser)).thenReturn(dto);

        assertThat(facade.getById(id)).isEqualTo(dto);
    }

    // ------------------------------------------------------------------
    // submit (id null / non null)
    // ------------------------------------------------------------------

    @Test
    void submit_newRequest_validatesInitAndStartsWorkflow() throws Exception {
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder().id(null).build();
        StartDerggTarifRetWfInstanceCommand command = StartDerggTarifRetWfInstanceCommand.builder().build();
        when(startDerogTarifRetWfCommandMapper.toCommand(dto)).thenReturn(command);
        DerogationRequest entity = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestService.startDerogTarifRetWf(command)).thenReturn(entity);
        when(derogationRequestService.saveDerogationRequest(entity)).thenReturn(entity);
        DerogationRequestDTO expected = DerogationRequestDTO.builder().businessKey("BK-001").build();
        when(derogationRequestMapper.toDTO(entity)).thenReturn(expected);

        DerogationRequestDTO result = facade.submit(dto);

        assertThat(result).isEqualTo(expected);
        verify(derogationAuthorizationService).validateCurrentUserCanInitOrCreateDerog();
        verify(documentService).sendDocumentsAttachmentEvent(entity);
    }

    @Test
    void submit_fromExistingDraft_validatesInitiateur() throws Exception {
        UUID draftId = UUID.randomUUID();
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder().id(draftId).build();
        DerogationRequest draft = DerogationRequest.builder().businessKey("BK-DRAFT").build();
        when(derogationRequestService.getById(draftId)).thenReturn(draft);
        StartDerggTarifRetWfInstanceCommand command = StartDerggTarifRetWfInstanceCommand.builder().id(draftId).build();
        when(startDerogTarifRetWfCommandMapper.toCommand(dto)).thenReturn(command);
        DerogationRequest entity = DerogationRequest.builder().businessKey("BK-DRAFT").build();
        when(derogationRequestService.startDerogTarifRetWf(command)).thenReturn(entity);
        when(derogationRequestService.saveDerogationRequest(entity)).thenReturn(entity);
        when(derogationRequestMapper.toDTO(entity)).thenReturn(DerogationRequestDTO.builder().build());

        facade.submit(dto);

        verify(derogationAuthorizationService).validateCurrentUserIsInitiateur(draft);
        verify(derogationAuthorizationService, never()).validateCurrentUserCanInitOrCreateDerog();
    }

    // ------------------------------------------------------------------
    // saveAsDraft (3 branches)
    // ------------------------------------------------------------------

    @Test
    void saveAsDraft_newDraft_createsAndSendsEvent() throws Exception {
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder().id(null).build();
        DerogationRequest draft = DerogationRequest.builder().businessKey("BK-NEW").build();
        when(derogationRequestMapper.toDraftEntity(dto)).thenReturn(draft);
        when(derogationRequestService.saveDerogationRequest(draft)).thenReturn(draft);

        facade.saveAsDraft(dto);

        verify(derogationAuthorizationService).validateCurrentUserCanInitOrCreateDerog();
        verify(documentService).sendDocumentsAttachmentEvent(draft);
    }

    @Test
    void saveAsDraft_existingDraft_documentsChanged_sendsEvent() throws Exception {
        UUID draftId = UUID.randomUUID();
        UUID newDoc = UUID.randomUUID();
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder()
                .id(draftId)
                .documentIds(Set.of(newDoc))
                .build();
        DerogationRequest draft = DerogationRequest.builder().businessKey("BK-DRAFT").build();
        draft.setDocumentIds(new LinkedHashSet<>(Set.of(UUID.randomUUID()))); // docs différents
        when(derogationRequestService.getById(draftId)).thenReturn(draft);

        facade.saveAsDraft(dto);

        verify(derogationAuthorizationService).validateCurrentUserIsInitiateur(draft);
        verify(derogationRequestMapper).updateEntityFromDTO(dto, draft);
        verify(documentService).sendDocumentsAttachmentEvent(draft);
    }

    @Test
    void saveAsDraft_existingDraft_sameDocuments_noEvent() throws Exception {
        UUID draftId = UUID.randomUUID();
        UUID sameDoc = UUID.randomUUID();
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder()
                .id(draftId)
                .documentIds(Set.of(sameDoc))
                .build();
        DerogationRequest draft = DerogationRequest.builder().businessKey("BK-DRAFT").build();
        draft.setDocumentIds(new LinkedHashSet<>(Set.of(sameDoc))); // mêmes docs
        when(derogationRequestService.getById(draftId)).thenReturn(draft);

        facade.saveAsDraft(dto);

        verify(documentService, never()).sendDocumentsAttachmentEvent(any());
    }

    // ------------------------------------------------------------------
    // save (UpdateDerogationRequestDTO)
    // ------------------------------------------------------------------

    @Test
    void save_validatesProcessUpdatesAndSendsEventWhenDocsChanged() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder()
                .id(id)
                .documentIds(Set.of(UUID.randomUUID()))
                .build();
        DerogationRequest entity = DerogationRequest.builder().businessKey("BK-001").build();
        entity.setDocumentIds(new LinkedHashSet<>());
        when(derogationRequestService.getById(id)).thenReturn(entity);

        facade.save(dto);

        verify(derogationAuthorizationService).validateCurrentUserCanProcess(entity);
        verify(derogationRequestMapper).updateEntityFromDTO(dto, entity);
        verify(derogationRequestService).saveDerogationRequest(entity);
        verify(documentService).sendDocumentsAttachmentEvent(entity);
    }

    // ------------------------------------------------------------------
    // approve (3 chemins)
    // ------------------------------------------------------------------

    @Test
    void approve_lmrOnValidationLmrStatus_updatesEntityFromDto() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder().id(id).build();
        DerogationRequest derog = derogAssignedTo(user("resp", ProfileEnum.DA), DerogationStatus.VALIDATION_LMR);
        derog.setInitiateur(user("init1", ProfileEnum.CONSEILLER));
        when(derogationRequestService.getById(id)).thenReturn(derog);
        when(userService.getCurrentUser()).thenReturn(user("lmr1", ProfileEnum.LMR));

        facade.approve(dto);

        verify(derogationRequestMapper).updateEntityFromDTO(dto, derog);
        verify(derogationRequestMapper, never()).updateEntityFromLmrValidationDTO(any(), any());
        verify(derogationRequestService).submit(derog);
        verify(documentService).sendDocumentsAttachmentEvent(derog);
    }

    @Test
    void approve_initiateurOnNonRevueStatus_updatesFromLmrValidationDto() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder().id(id).build();
        UserEntity initiateur = user("init1", ProfileEnum.CONSEILLER);
        DerogationRequest derog = derogAssignedTo(initiateur, DerogationStatus.SUBMITTED);
        derog.setInitiateur(initiateur);
        when(derogationRequestService.getById(id)).thenReturn(derog);
        when(userService.getCurrentUser()).thenReturn(user("init1", ProfileEnum.CONSEILLER)); // equals sur uid

        facade.approve(dto);

        verify(derogationRequestMapper).updateEntityFromLmrValidationDTO(dto, derog);
        verify(derogationRequestService).submit(derog);
    }

    @Test
    void approve_otherCases_justSubmits() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder().id(id).build();
        DerogationRequest derog = derogAssignedTo(user("resp", ProfileEnum.DA), DerogationStatus.REVUE);
        derog.setInitiateur(user("init1", ProfileEnum.CONSEILLER));
        when(derogationRequestService.getById(id)).thenReturn(derog);
        when(userService.getCurrentUser()).thenReturn(user("autre", ProfileEnum.DA));

        facade.approve(dto);

        verify(derogationRequestMapper, never()).updateEntityFromDTO(eq(dto), any(DerogationRequest.class));
        verify(derogationRequestMapper, never()).updateEntityFromLmrValidationDTO(any(), any());
        verify(derogationRequestService).submit(derog);
    }

    // ------------------------------------------------------------------
    // reject / retourACharge / requestComplements
    // ------------------------------------------------------------------

    @Test
    void reject_validatesUpdatesAndRejects() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder().id(id).build();
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestService.getById(id)).thenReturn(derog);

        facade.reject(dto);

        verify(derogationAuthorizationService).validateCurrentUserCanProcess(derog);
        verify(derogationRequestMapper).updateEntityFromDTO(dto, derog);
        verify(documentService).sendDocumentsAttachmentEvent(derog);
        verify(derogationRequestService).reject(derog);
    }

    @Test
    void retourACharge_validatesRevueStatusAndDelegates() throws Exception {
        UUID id = UUID.randomUUID();
        RetourAchargeValidationDTO dto = RetourAchargeValidationDTO.builder().id(id).build();
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001")
                .status(DerogationStatus.REVUE.name()).build();
        when(derogationRequestService.getById(id)).thenReturn(derog);

        facade.retourACharge(dto);

        verify(derogationAuthorizationService).validateCurrentUserCanProcess(derog);
        verify(derogationAuthorizationService).validateDerogStatus(derog, DerogationStatus.REVUE);
        verify(derogationRequestMapper).updateEntityFromRetourAchargeValidationDTO(dto, derog);
        verify(derogationRequestService).retourACharge(derog);
        verify(documentService).sendDocumentsAttachmentEvent(derog);
    }

    @Test
    void requestComplements_nominal_delegates() {
        UUID id = UUID.randomUUID();
        SendDemandeComplementsDTO dto = SendDemandeComplementsDTO.builder().id(id).build();
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestService.getById(id)).thenReturn(derog);

        facade.requestComplements(dto);

        verify(derogationRequestMapper).updateEntityFromDTO(dto, derog);
        verify(derogationRequestService).retourPourComplementInfos(derog);
        verify(documentService).sendDocumentsAttachmentEvent(derog);
    }

    @Test
    void requestComplements_entityNull_throwsIllegalArgument() {
        UUID id = UUID.randomUUID();
        SendDemandeComplementsDTO dto = SendDemandeComplementsDTO.builder().id(id).build();
        when(derogationRequestService.getById(id)).thenReturn(null);

        assertThatThrownBy(() -> facade.requestComplements(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    // ------------------------------------------------------------------
    // updateStatus (3 branches)
    // ------------------------------------------------------------------

    @Test
    void updateStatus_statusNotClosed_throwsIllegalArgument() {
        UUID id = UUID.randomUUID();
        UpdateStatusRequestDTO dto = UpdateStatusRequestDTO.builder()
                .id(id).status(DerogationStatus.SUBMITTED).build();
        DerogationRequest derog = DerogationRequest.builder().id(id)
                .status(DerogationStatus.DRAFT.name()).build();
        when(derogationRequestService.getById(id)).thenReturn(derog);

        assertThatThrownBy(() -> facade.updateStatus(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CLOSED");
    }

    @Test
    void updateStatus_entityNotDraft_throwsIllegalState() {
        UUID id = UUID.randomUUID();
        UpdateStatusRequestDTO dto = UpdateStatusRequestDTO.builder()
                .id(id).status(DerogationStatus.CLOSED).build();
        DerogationRequest derog = DerogationRequest.builder().id(id)
                .status(DerogationStatus.SUBMITTED.name()).build();
        when(derogationRequestService.getById(id)).thenReturn(derog);

        assertThatThrownBy(() -> facade.updateStatus(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("DRAFT");
    }

    @Test
    void updateStatus_closedOnDraft_closesDerogation() {
        UUID id = UUID.randomUUID();
        UpdateStatusRequestDTO dto = UpdateStatusRequestDTO.builder()
                .id(id).status(DerogationStatus.CLOSED).build();
        DerogationRequest derog = DerogationRequest.builder().id(id)
                .status(DerogationStatus.DRAFT.name()).build();
        when(derogationRequestService.getById(id)).thenReturn(derog);

        facade.updateStatus(dto);

        verify(derogationAuthorizationService).validateCurrentUserIsInitiateur(derog);
        verify(derogationRequestService).closeDerogationDraft(id);
    }

    // ------------------------------------------------------------------
    // reassign — ~10 branches
    // ------------------------------------------------------------------

    /** Prépare le décor commun de reassign et retourne l'assigneeUser synchronisé. */
    private UserEntity setupReassign(String assigneeUid, String assigneeProfileCode,
                                     OrgaUnitDTO assigneeOrgaUnit,
                                     UserEntity currentUser, OrgaUnitDTO currentUserOrgaUnit,
                                     UUID derogationId) {
        InternalUserDTO assigneeDetails = fetchedAssignee(assigneeProfileCode, assigneeOrgaUnit);
        when(userDetailsService.fetchUser(assigneeUid)).thenReturn(assigneeDetails);
        UserEntity assigneeUser = user(assigneeUid, ProfileEnum.exists(assigneeProfileCode)
                ? ProfileEnum.valueOf(assigneeProfileCode) : ProfileEnum.CONSEILLER);
        when(userService.findOrSyncUserByUid(assigneeUid, assigneeDetails)).thenReturn(assigneeUser);
        when(userService.getCurrentUserDetails()).thenReturn(
                detailsWithOrgaUnit(currentUser.getUid(), currentUser.getProfile(), currentUserOrgaUnit));
        DerogationRequest derog = derogAssignedTo(user("resp", ProfileEnum.DA), DerogationStatus.SUBMITTED);
        when(derogationRequestService.getById(derogationId)).thenReturn(derog);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        return assigneeUser;
    }

    @Test
    void reassign_conseiller_forbidden() {
        UUID id = UUID.randomUUID();
        setupReassign("a1", "CONSEILLER", mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS),
                user("c1", ProfileEnum.CONSEILLER), mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), id);

        assertThatThrownBy(() -> facade.reassign(id, "a1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pas autorisé");
        verify(derogationRequestService, never()).assignTask(any(), any(), any());
    }

    @Test
    void reassign_apacCompta_forbidden() {
        UUID id = UUID.randomUUID();
        setupReassign("a1", "CONSEILLER", mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS),
                user("apac1", ProfileEnum.APAC_COMPTA), mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), id);

        assertThatThrownBy(() -> facade.reassign(id, "a1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reassign_lmr_forbidden() {
        UUID id = UUID.randomUUID();
        setupReassign("a1", "CONSEILLER", mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS),
                user("lmr1", ProfileEnum.LMR), mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), id);

        assertThatThrownBy(() -> facade.reassign(id, "a1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reassign_da_isManagerOfAssignee_assigns() {
        UUID id = UUID.randomUUID();
        UserEntity da = user("da1", ProfileEnum.DA);
        UserEntity assigneeUser = setupReassign("emp1", "CONSEILLER",
                mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), da, mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), id);
        when(userService.getManagerOf("emp1")).thenReturn(user("da1", ProfileEnum.DA)); // même uid

        facade.reassign(id, "emp1");

        verify(derogationRequestService).assignTask(any(DerogationRequest.class), eq(assigneeUser), eq(da));
    }

    @Test
    void reassign_da_notManagerOfAssignee_forbidden() {
        UUID id = UUID.randomUUID();
        UserEntity da = user("da1", ProfileEnum.DA);
        setupReassign("emp1", "CONSEILLER",
                mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), da, mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), id);
        when(userService.getManagerOf("emp1")).thenReturn(user("autreDa", ProfileEnum.DA));

        assertThatThrownBy(() -> facade.reassign(id, "emp1"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(derogationRequestService, never()).assignTask(any(), any(), any());
    }

    @Test
    void reassign_die_assigneeNotDie_forbidden() {
        UUID id = UUID.randomUUID();
        setupReassign("a1", "CONSEILLER", mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS),
                user("die1", ProfileEnum.DIE), mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), id);

        assertThatThrownBy(() -> facade.reassign(id, "a1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reassign_die_sameZone_assigns() {
        UUID id = UUID.randomUUID();
        // même instance de zone partagée par les deux parents → toute forme de comparaison passe
        OrgaUnitDTO sharedZone = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(sharedZone.getId()).thenReturn(100L);
        OrgaUnitDTO assigneeOrga = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(assigneeOrga.getParent()).thenReturn(sharedZone);
        OrgaUnitDTO currentOrga = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(currentOrga.getParent()).thenReturn(sharedZone);

        UserEntity die = user("die1", ProfileEnum.DIE);
        UserEntity assigneeUser = setupReassign("die2", "DIE", assigneeOrga, die, currentOrga, id);

        facade.reassign(id, "die2");

        verify(derogationRequestService).assignTask(any(DerogationRequest.class), eq(assigneeUser), eq(die));
    }

    @Test
    void reassign_die_differentZone_forbidden() {
        UUID id = UUID.randomUUID();
        OrgaUnitDTO zoneA = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(zoneA.getId()).thenReturn(100L);
        OrgaUnitDTO zoneB = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(zoneB.getId()).thenReturn(200L);
        OrgaUnitDTO assigneeOrga = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(assigneeOrga.getParent()).thenReturn(zoneA);
        OrgaUnitDTO currentOrga = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(currentOrga.getParent()).thenReturn(zoneB);

        setupReassign("die2", "DIE", assigneeOrga, user("die1", ProfileEnum.DIE), currentOrga, id);

        assertThatThrownBy(() -> facade.reassign(id, "die2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reassign_dz_assigneeNotDie_forbidden() {
        UUID id = UUID.randomUUID();
        setupReassign("a1", "CONSEILLER", mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS),
                user("dz1", ProfileEnum.DZ), mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS), id);

        assertThatThrownBy(() -> facade.reassign(id, "a1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DIE");
    }

    @Test
    void reassign_dz_assigneeDieOfHisZone_assigns() {
        UUID id = UUID.randomUUID();
        // asymétrie DZ : parent de l'assignee comparé à l'orgaUnit DIRECTE du DZ
        OrgaUnitDTO dzZone = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(dzZone.getId()).thenReturn(300L);
        OrgaUnitDTO assigneeOrga = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(assigneeOrga.getParent()).thenReturn(dzZone);

        UserEntity dz = user("dz1", ProfileEnum.DZ);
        UserEntity assigneeUser = setupReassign("die2", "DIE", assigneeOrga, dz, dzZone, id);

        facade.reassign(id, "die2");

        verify(derogationRequestService).assignTask(any(DerogationRequest.class), eq(assigneeUser), eq(dz));
    }

    @Test
    void reassign_dz_dieOfOtherZone_forbidden() {
        UUID id = UUID.randomUUID();
        OrgaUnitDTO dzZone = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(dzZone.getId()).thenReturn(300L);
        OrgaUnitDTO otherZone = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(otherZone.getId()).thenReturn(400L);
        OrgaUnitDTO assigneeOrga = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(assigneeOrga.getParent()).thenReturn(otherZone);

        setupReassign("die2", "DIE", assigneeOrga, user("dz1", ProfileEnum.DZ), dzZone, id);

        assertThatThrownBy(() -> facade.reassign(id, "die2"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ------------------------------------------------------------------
    // getReassignmentCandidates
    // ------------------------------------------------------------------

    @Test
    void getReassignmentCandidates_filtersOutCurrentAssignee() {
        UUID id = UUID.randomUUID();
        UserEntity currentAssignee = user("resp1", ProfileEnum.DA);
        DerogationRequest derog = derogAssignedTo(currentAssignee, DerogationStatus.SUBMITTED);
        when(derogationRequestService.getById(id)).thenReturn(derog);
        // ⚠️ TODO : vérifier le nom exact de la méthode côté UserService
        // (capture service : "Reaffctetion" / appel facade : "Reaffectation")
        when(userService.getUsersCandidateToReaffctetionByCurrentUserProfile())
                .thenReturn(List.of(currentAssignee, user("u2", ProfileEnum.CONSEILLER)));

        List<UserReaffectationDto> result = facade.getReassignmentCandidates(id);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUid()).isEqualTo("u2");
        assertThat(result.get(0).getFirstName()).isEqualTo("First-u2");
        assertThat(result.get(0).getEmail()).isEqualTo("u2@bnpparibas.com");
    }
}
