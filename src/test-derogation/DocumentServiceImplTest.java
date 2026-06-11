package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster ce package selon l'emplacement réel de DocumentServiceImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.exceptions.UnauthorizedOperationException;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.DerogationAuthorizationService;
// TODO : ajuster les imports selon les packages réels :
// - APIs générées (InternalDocumentAttachmentControllerApi, InternalDocumentQueryControllerApi, DocumentDto)
// - AttachDocumentsOutboxEventMapper, OutboxPublisher
import com.bnpparibas.irb.qlickflow.bpm.outbox.OutboxPublisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private InternalDocumentAttachmentControllerApi documentAttachmentControllerApi;
    @Mock
    private InternalDocumentQueryControllerApi internalDocumentQueryControllerApi;
    @Mock
    private DerogationRequestService derogationRequestService;
    @Mock
    private DerogationAuthorizationService derogationAuthorizationService;
    @Mock
    private AttachDocumentsOutboxEventMapper attachDocumentsOutboxEventMapper;
    @Mock
    private OutboxPublisher outboxPublisher;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private UserEntity currentUser() {
        return UserEntity.builder()
                .uid("u1").email("u1@bnpparibas.com")
                .firstName("First").lastName("Last")
                .profile(ProfileEnum.CONSEILLER)
                .build();
    }

    // ------------------------------------------------------------------
    // sendDocumentsAttachmentEvent (3 branches : null / vide / non vide)
    // ------------------------------------------------------------------

    @Test
    void sendDocumentsAttachmentEvent_nullDocumentIds_doesNothing() {
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .build();
        derog.setDocumentIds(null);

        documentService.sendDocumentsAttachmentEvent(derog);

        verifyNoInteractions(attachDocumentsOutboxEventMapper, outboxPublisher);
    }

    @Test
    void sendDocumentsAttachmentEvent_emptyDocumentIds_doesNothing() {
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .build();
        derog.setDocumentIds(new LinkedHashSet<>());

        documentService.sendDocumentsAttachmentEvent(derog);

        verifyNoInteractions(attachDocumentsOutboxEventMapper, outboxPublisher);
    }

    @Test
    void sendDocumentsAttachmentEvent_withDocuments_mapsAndPublishes() {
        DerogationRequest derog = DerogationRequest.builder()
                .businessKey("BK-001")
                .build();
        derog.setDocumentIds(new LinkedHashSet<>(Set.of(UUID.randomUUID())));

        documentService.sendDocumentsAttachmentEvent(derog);

        verify(attachDocumentsOutboxEventMapper).map(derog);
        verify(outboxPublisher).publish(any());
    }

    // ------------------------------------------------------------------
    // commitAttachments
    // ------------------------------------------------------------------

    @Test
    void commitAttachments_validatesThenCallsApi() throws Exception {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestService.getByBusinessKey("BK-001")).thenReturn(derog);
        when(userService.getCurrentUser()).thenReturn(currentUser());
        List<UUID> docIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        documentService.commitAttachments("BK-001", docIds);

        verify(derogationAuthorizationService).validateCurrentUserCanProcess(derog);
        verify(documentAttachmentControllerApi).commit(any());
    }

    // ------------------------------------------------------------------
    // detach (2 branches : document présent / absent → throw)
    // ------------------------------------------------------------------

    @Test
    void detach_documentBelongsToDerog_callsApi() throws Exception {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestService.getByBusinessKey("BK-001")).thenReturn(derog);
        when(userService.getCurrentUser()).thenReturn(currentUser());

        UUID documentId = UUID.randomUUID();
        DocumentDto doc = mock(DocumentDto.class); // classe générée — mockée
        when(doc.getId()).thenReturn(documentId);
        when(internalDocumentQueryControllerApi.getDocuments1("BK-001")).thenReturn(List.of(doc));

        documentService.detach("BK-001", documentId);

        verify(derogationAuthorizationService).validateCurrentUserCanProcess(derog);
        verify(documentAttachmentControllerApi).detach(any());
    }

    @Test
    void detach_documentNotInDerog_throwsUnauthorized() throws Exception {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        when(derogationRequestService.getByBusinessKey("BK-001")).thenReturn(derog);
        when(userService.getCurrentUser()).thenReturn(currentUser());

        DocumentDto otherDoc = mock(DocumentDto.class);
        when(otherDoc.getId()).thenReturn(UUID.randomUUID());
        when(internalDocumentQueryControllerApi.getDocuments1("BK-001")).thenReturn(List.of(otherDoc));

        UUID missingDocId = UUID.randomUUID();

        assertThatThrownBy(() -> documentService.detach("BK-001", missingDocId))
                .isInstanceOf(UnauthorizedOperationException.class);

        verify(documentAttachmentControllerApi, never()).detach(any());
    }
}
