package com.bnpparibas.irb.qlickflow.wfdtrp.controller;
// ⚠️ TODO : ajuster le package selon l'emplacement réel du controller.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CommitDocumentsRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.DocumentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentAttachmentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentAttachmentController controller;

    @Test
    void commitAttach_delegatesToDocumentService() throws Exception {
        List<UUID> docIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        // CommitDocumentsRequest = record DTO du projet (≠ classe générée du même nom côté API)
        CommitDocumentsRequest request = new CommitDocumentsRequest(docIds);

        controller.commitAttach("BK-001", request);

        verify(documentService).commitAttachments("BK-001", docIds);
    }

    @Test
    void detach_delegatesToDocumentService() throws Exception {
        UUID documentId = UUID.randomUUID();

        controller.detach("BK-001", documentId);

        verify(documentService).detach("BK-001", documentId);
    }
}
