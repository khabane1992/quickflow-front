package com.bnpparibas.irb.qlickflow.documents.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires (Mockito) pour DocumentAttachmentService.
 * Objectif : couvrir CHAQUE branche (lignes + branches Jacoco).
 *
 * commitAttachments : skip (ATTACHED + même owner) / throw (status != STAGED) / succès STAGED
 * detachAttachments : count mismatch throw / skip (STAGED + orphan) / ATTACHED->DETACHED /
 *                     QUARANTINED|DELETED throw / fallthrough throw
 * stageDocument -> saveDocument : file vide / trop gros / mime non supporté /
 *                  filename null / chemin nominal
 *
 * ⚠️ ADAPTER : noms exacts de DocumentEntity (suppose @Data @Builder), DocumentStatus,
 * DocumentProperties.allowedMimeToExt(), DocumentExtensionResolver.resolve(...),
 * StoreAndHashResult / StoredObject, AttachmentResult.
 */
@ExtendWith(MockitoExtension.class)
class DocumentAttachmentServiceTest {

    @Mock DocumentRepository documentRepository;
    @Mock DocumentProperties documentProperties;
    @Mock DocumentStorageService documentStorageService;
    @Mock DocumentExtensionResolver documentExtensionResolver;

    @InjectMocks DocumentAttachmentService service;

    private DocumentEntity doc(UUID id, DocumentStatus status, String ownerRef) {
        return DocumentEntity.builder()
                .id(id)
                .status(status)
                .ownerRef(ownerRef)
                .createdBy("user-1")
                .build();
    }

    // =================== commitAttachments ===================

    @Test
    void commit_attachesStagedDocument() {
        UUID id = UUID.randomUUID();
        DocumentEntity staged = doc(id, DocumentStatus.STAGED, null);

        when(documentRepository.findAllByOwnerRef("OWNER-1")).thenReturn(List.of());
        when(documentRepository.findAllByIdForUpdate(List.of(id))).thenReturn(List.of(staged));

        List<UUID> result = service.commitAttachments("OWNER-1", List.of(id), "user-1", "corr-1");

        assertThat(result).containsExactly(id);
        assertThat(staged.getStatus()).isEqualTo(DocumentStatus.ATTACHED);
        assertThat(staged.getOwnerRef()).isEqualTo("OWNER-1");
    }

    @Test
    void commit_skipsAlreadyAttachedToSameOwner() {
        // existingAttachments contient déjà ce doc => filtré, donc findAllByIdForUpdate
        // reçoit une liste vide. On vérifie l'idempotence (aucun changement, retour vide).
        UUID id = UUID.randomUUID();
        DocumentEntity attached = doc(id, DocumentStatus.ATTACHED, "OWNER-1");

        when(documentRepository.findAllByOwnerRef("OWNER-1")).thenReturn(List.of(attached));
        when(documentRepository.findAllByIdForUpdate(List.of())).thenReturn(List.of());

        List<UUID> result = service.commitAttachments("OWNER-1", List.of(id), "user-1", "corr-1");

        assertThat(result).isEmpty();
    }

    @Test
    void commit_continueWhenDocAlreadyAttachedSameOwnerInFetched() {
        // Cas où findAllByIdForUpdate renvoie quand même un doc ATTACHED au même owner
        // => branche `continue` (status ATTACHED && ownerRef.equals)
        UUID id = UUID.randomUUID();
        DocumentEntity attached = doc(id, DocumentStatus.ATTACHED, "OWNER-1");

        when(documentRepository.findAllByOwnerRef("OWNER-1")).thenReturn(List.of());
        when(documentRepository.findAllByIdForUpdate(List.of(id))).thenReturn(List.of(attached));

        List<UUID> result = service.commitAttachments("OWNER-1", List.of(id), "user-1", "corr-1");

        // doc sauté mais toujours renvoyé dans la projection finale des ids
        assertThat(result).containsExactly(id);
        assertThat(attached.getStatus()).isEqualTo(DocumentStatus.ATTACHED);
    }

    @Test
    void commit_throwsWhenStatusNotStaged() {
        UUID id = UUID.randomUUID();
        DocumentEntity detached = doc(id, DocumentStatus.DETACHED, "OTHER");

        when(documentRepository.findAllByOwnerRef("OWNER-1")).thenReturn(List.of());
        when(documentRepository.findAllByIdForUpdate(List.of(id))).thenReturn(List.of(detached));

        assertThatThrownBy(() ->
                service.commitAttachments("OWNER-1", List.of(id), "user-1", "corr-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be attached");
    }

    // =================== detachAttachments ===================

    @Test
    void detach_throwsWhenSomeDocumentsNotFound() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        // demande 2, repo n'en renvoie qu'1 => mismatch
        when(documentRepository.findAllById(List.of(id1, id2)))
                .thenReturn(List.of(doc(id1, DocumentStatus.ATTACHED, "OWNER-1")));

        assertThatThrownBy(() ->
                service.detachAttachments(List.of(id1, id2), "user-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void detach_skipsStagedOrphan() {
        UUID id = UUID.randomUUID();
        DocumentEntity stagedOrphan = doc(id, DocumentStatus.STAGED, null);
        when(documentRepository.findAllById(List.of(id))).thenReturn(List.of(stagedOrphan));

        List<UUID> result = service.detachAttachments(List.of(id), "user-1");

        // sauté (continue) mais inclus dans la projection finale
        assertThat(result).containsExactly(id);
        assertThat(stagedOrphan.getStatus()).isEqualTo(DocumentStatus.STAGED);
    }

    @Test
    void detach_detachesAttached() {
        UUID id = UUID.randomUUID();
        DocumentEntity attached = doc(id, DocumentStatus.ATTACHED, "OWNER-1");
        when(documentRepository.findAllById(List.of(id))).thenReturn(List.of(attached));

        List<UUID> result = service.detachAttachments(List.of(id), "user-1");

        assertThat(result).containsExactly(id);
        assertThat(attached.getStatus()).isEqualTo(DocumentStatus.DETACHED);
        assertThat(attached.getOwnerRef()).isNull();
    }

    @Test
    void detach_throwsWhenQuarantined() {
        UUID id = UUID.randomUUID();
        DocumentEntity q = doc(id, DocumentStatus.QUARANTINED, "OWNER-1");
        when(documentRepository.findAllById(List.of(id))).thenReturn(List.of(q));

        assertThatThrownBy(() -> service.detachAttachments(List.of(id), "user-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be detached");
    }

    @Test
    void detach_throwsWhenDeleted() {
        UUID id = UUID.randomUUID();
        DocumentEntity del = doc(id, DocumentStatus.DELETED, "OWNER-1");
        when(documentRepository.findAllById(List.of(id))).thenReturn(List.of(del));

        assertThatThrownBy(() -> service.detachAttachments(List.of(id), "user-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be detached");
    }

    @Test
    void detach_throwsOnFallthroughStatus() {
        // status qui ne matche aucun if explicite (ex: DETACHED) => throw final
        UUID id = UUID.randomUUID();
        DocumentEntity already = doc(id, DocumentStatus.DETACHED, null);
        when(documentRepository.findAllById(List.of(id))).thenReturn(List.of(already));

        assertThatThrownBy(() -> service.detachAttachments(List.of(id), "user-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be detached");
    }

    // =================== stageDocument -> saveDocument ===================

    private MultipartFile file(byte[] content, String name, String contentType) {
        return new MockMultipartFile("file", name, contentType, content);
    }

    @Test
    void stage_savesValidFile() {
        MultipartFile f = file("hello".getBytes(), "doc.pdf", "application/pdf");

        when(documentProperties.allowedMimeToExt())
                .thenReturn(Map.of("application/pdf", "pdf"));
        when(documentExtensionResolver.resolve(any())).thenReturn("pdf");
        when(documentStorageService.storeAndHash(any(), any(), any()))
                .thenReturn(new StoreAndHashResult(
                        new StoredObject("storage/key/1"), "abc123hash"));

        AttachmentResult result = service.stageDocument(f, "user-1");

        assertThat(result.getStatus()).isEqualTo(DocumentStatus.STAGED.name());
        assertThat(result.getOriginalFileName()).isEqualTo("doc.pdf");
        assertThat(result.getSha256()).isEqualTo("abc123hash");
        verify(documentRepository).save(any(DocumentEntity.class));
    }

    @Test
    void stage_throwsWhenFileEmpty() {
        MultipartFile empty = file(new byte[0], "doc.pdf", "application/pdf");

        assertThatThrownBy(() -> service.stageDocument(empty, "user-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void stage_throwsWhenFileTooLarge() {
        // MAX_SIZE_BYTES = 10MB ; on dépasse
        byte[] big = new byte[11 * 1024 * 1024];
        MultipartFile huge = file(big, "doc.pdf", "application/pdf");

        assertThatThrownBy(() -> service.stageDocument(huge, "user-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too large");
    }

    @Test
    void stage_throwsWhenContentTypeUnsupported() {
        MultipartFile f = file("data".getBytes(), "doc.exe", "application/x-msdownload");

        when(documentProperties.allowedMimeToExt())
                .thenReturn(Map.of("application/pdf", "pdf")); // pas le bon type

        assertThatThrownBy(() -> service.stageDocument(f, "user-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported content type");
    }

    @Test
    void stage_handlesNullOriginalFilename() {
        // filename null => branche ternaire "unknown"
        MultipartFile f = new MockMultipartFile(
                "file", null, "application/pdf", "data".getBytes());

        when(documentProperties.allowedMimeToExt())
                .thenReturn(Map.of("application/pdf", "pdf"));
        when(documentExtensionResolver.resolve(any())).thenReturn("pdf");
        when(documentStorageService.storeAndHash(any(), any(), any()))
                .thenReturn(new StoreAndHashResult(
                        new StoredObject("storage/key/2"), "hash2"));

        AttachmentResult result = service.stageDocument(f, "user-1");

        assertThat(result.getOriginalFileName()).isEqualTo("unknown");
    }
}

package com.bnpparibas.irb.qlickflow.documents.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires purs (Mockito) pour DocumentQueryService.
 * Couvre : getDocumentsByOwnerRef, getDocumentsByIds, getEntityOrThrow,
 * getDocumentsById, et la projection toDto (via les retours).
 *
 * ⚠️ ADAPTER : les imports de DocumentEntity / DocumentStatus / DocumentRepository
 * selon vos packages réels. Hypothèse : DocumentEntity est @Data @Builder.
 */
@ExtendWith(MockitoExtension.class)
class DocumentQueryServiceTest {

    @Mock
    DocumentRepository documentRepository;

    @InjectMocks
    DocumentQueryService service;

    // --- Helper pour fabriquer une entité complète ---
    private DocumentEntity entity(UUID id) {
        return DocumentEntity.builder()
                .id(id)
                .ownerRef("OWNER-1")
                .originalFileName("file.pdf")
                .contentType("application/pdf")
                .size(123L)
                .status(DocumentStatus.ATTACHED)
                .createdBy("user-1")
                .createdAt(OffsetDateTime.now())
                .attachedAt(OffsetDateTime.now())
                .storageKey("storage/key/1")
                .build();
    }

    // ============ getDocumentsByOwnerRef ============

    @Test
    void getDocumentsByOwnerRef_returnsMappedDtos() {
        UUID id = UUID.randomUUID();
        when(documentRepository.findAllByOwnerRef("OWNER-1"))
                .thenReturn(List.of(entity(id)));

        List<DocumentDto> result = service.getDocumentsByOwnerRef("OWNER-1");

        assertThat(result).hasSize(1);
        DocumentDto dto = result.get(0);
        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.ownerRef()).isEqualTo("OWNER-1");
        assertThat(dto.originalFileName()).isEqualTo("file.pdf");
        assertThat(dto.contentType()).isEqualTo("application/pdf");
        assertThat(dto.size()).isEqualTo(123L);
        assertThat(dto.status()).isEqualTo("ATTACHED");
        assertThat(dto.storageKey()).isEqualTo("storage/key/1");
    }

    @Test
    void getDocumentsByOwnerRef_emptyWhenNoneFound() {
        when(documentRepository.findAllByOwnerRef("OWNER-X")).thenReturn(List.of());

        assertThat(service.getDocumentsByOwnerRef("OWNER-X")).isEmpty();
    }

    // ============ getDocumentsByIds ============

    @Test
    void getDocumentsByIds_returnsMappedDtos() {
        UUID id = UUID.randomUUID();
        List<UUID> ids = List.of(id);
        when(documentRepository.findAllById(ids)).thenReturn(List.of(entity(id)));

        List<DocumentDto> result = service.getDocumentsByIds(ids);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(id);
    }

    @Test
    void getDocumentsByIds_emptyWhenNoneFound() {
        when(documentRepository.findAllById(List.of())).thenReturn(List.of());

        assertThat(service.getDocumentsByIds(List.of())).isEmpty();
    }

    // ============ getEntityOrThrow ============

    @Test
    void getEntityOrThrow_returnsDtoWhenPresent() {
        UUID id = UUID.randomUUID();
        when(documentRepository.findById(id)).thenReturn(Optional.of(entity(id)));

        DocumentDto dto = service.getEntityOrThrow(id.toString());

        assertThat(dto.id()).isEqualTo(id);
    }

    @Test
    void getEntityOrThrow_throwsWhenAbsent() {
        UUID id = UUID.randomUUID();
        when(documentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEntityOrThrow(id.toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Document not found");
    }

    // ============ getDocumentsById ============

    @Test
    void getDocumentsById_returnsDtoWhenPresent() {
        UUID id = UUID.randomUUID();
        when(documentRepository.findById(id)).thenReturn(Optional.of(entity(id)));

        DocumentDto dto = service.getDocumentsById(id.toString());

        assertThat(dto.id()).isEqualTo(id);
    }

    @Test
    void getDocumentsById_throwsWhenAbsent() {
        UUID id = UUID.randomUUID();
        when(documentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getDocumentsById(id.toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Document not found");
    }
}

package com.bnpparibas.irb.qlickflow.documents.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires (Mockito) pour DocumentStorageService.
 * Couvre storeAndHash (succès + catch -> RuntimeException) et
 * openForDownload (succès + catch -> StorageException).
 *
 * ⚠️ ADAPTER : LocalFileSystemStorage, StoredObject, StoreAndHashResult,
 * InputStreamResourceWithType, StorageException selon vos signatures réelles.
 */
@ExtendWith(MockitoExtension.class)
class DocumentStorageServiceTest {

    @Mock LocalFileSystemStorage localFileSystemStorage;

    @InjectMocks DocumentStorageService service;

    // =============== storeAndHash ===============

    @Test
    void storeAndHash_returnsResultOnSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "hello".getBytes());

        when(localFileSystemStorage.store(eq(id), any(), any(), eq("pdf")))
                .thenReturn(new StoredObject("storage/key/1"));

        StoreAndHashResult result = service.storeAndHash(id, file, "pdf");

        assertThat(result).isNotNull();
        assertThat(result.storedObject().storageKey()).isEqualTo("storage/key/1");
        // SHA-256 de "hello" attendu (déterministe)
        assertThat(result.sha256()).isNotBlank();
    }

    @Test
    void storeAndHash_wrapsExceptionInRuntimeException() throws Exception {
        UUID id = UUID.randomUUID();
        // file.getInputStream() lève une IOException => entre dans le catch
        MultipartFile broken = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", new byte[]{1}) {
            @Override
            public java.io.InputStream getInputStream() throws IOException {
                throw new IOException("boom");
            }
        };

        assertThatThrownBy(() -> service.storeAndHash(id, broken, "pdf"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unable to store document");
    }

    // =============== openForDownload ===============

    @Test
    void openForDownload_returnsResourceOnSuccess() throws Exception {
        when(localFileSystemStorage.open("storage/key/1"))
                .thenReturn(new ByteArrayInputStream("data".getBytes()));

        var result = service.openForDownload("storage/key/1");

        assertThat(result).isNotNull();
    }

    @Test
    void openForDownload_wrapsExceptionInStorageException() throws Exception {
        when(localFileSystemStorage.open("bad-key"))
                .thenThrow(new IOException("not found"));

        assertThatThrownBy(() -> service.openForDownload("bad-key"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Unable to open document");
    }
}

package com.bnpparibas.irb.qlickflow.documents.api.internal;

import com.bnpparibas.irb.qlickflow.documents.service.DocumentAttachmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest pour InternalDocumentAttachmentController.
 * Pas de Spring Security dans le projet => pas de config particulière.
 *
 * ⚠️ ADAPTER : si le package du controller diffère, ajuster l'import et @WebMvcTest.
 */
@WebMvcTest(InternalDocumentAttachmentController.class)
class InternalDocumentAttachmentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean DocumentAttachmentService documentAttachmentService;

    @Test
    void commit_returns200WithAttachedIds() throws Exception {
        UUID docId = UUID.randomUUID();
        when(documentAttachmentService.commitAttachments(
                anyString(), anyList(), anyString(), anyString()))
                .thenReturn(List.of(docId));

        var body = Map.of(
                "ownerRef", "OWNER-1",
                "documentIds", List.of(docId.toString()),
                "correlationId", "corr-1",
                "user", "user-1"
        );

        mockMvc.perform(post("/api/v1/internal/documents/attachments:commit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerRef").value("OWNER-1"))
                .andExpect(jsonPath("$.attachedDocumentIds[0]").value(docId.toString()));
    }

    @Test
    void commit_returns400WhenValidationFails() throws Exception {
        // ownerRef blank => @NotBlank doit déclencher 400
        var body = Map.of(
                "ownerRef", "",
                "documentIds", List.of(UUID.randomUUID().toString()),
                "correlationId", "corr-1",
                "user", "user-1"
        );

        mockMvc.perform(post("/api/v1/internal/documents/attachments:commit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void detach_returns200WithDetachedIds() throws Exception {
        UUID docId = UUID.randomUUID();
        when(documentAttachmentService.detachAttachments(anyList(), any()))
                .thenReturn(List.of(docId));

        var body = Map.of(
                "documentIds", List.of(docId.toString()),
                "userUid", "user-1"
        );

        mockMvc.perform(post("/api/v1/internal/documents/attachments:detach")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detachedDocumentIds[0]").value(docId.toString()));
    }

    @Test
    void detach_returns400WhenDocumentIdsEmpty() throws Exception {
        // @NotEmpty sur documentIds
        var body = Map.of(
                "documentIds", List.of(),
                "userUid", "user-1"
        );

        mockMvc.perform(post("/api/v1/internal/documents/attachments:detach")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}

package com.bnpparibas.irb.qlickflow.documents.api.internal;

import com.bnpparibas.irb.qlickflow.documents.service.DocumentDto;
import com.bnpparibas.irb.qlickflow.documents.service.DocumentQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest pour InternalDocumentQueryController.
 * ⚠️ ADAPTER les imports de package si besoin.
 */
@WebMvcTest(InternalDocumentQueryController.class)
class InternalDocumentQueryControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean DocumentQueryService documentQueryService;

    private DocumentDto dto(UUID id) {
        return new DocumentDto(id, "OWNER-1", "file.pdf", "application/pdf",
                123L, "ATTACHED", OffsetDateTime.now(), "user-1",
                OffsetDateTime.now(), "storage/key/1");
    }

    @Test
    void getDocuments_byOwner_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(documentQueryService.getDocumentsByOwnerRef("OWNER-1"))
                .thenReturn(List.of(dto(id)));

        mockMvc.perform(get("/api/v1/internal/documents/by-owner")
                        .param("ownerRef", "OWNER-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].ownerRef").value("OWNER-1"));
    }

    @Test
    void getDocumentsByIds_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(documentQueryService.getDocumentsByIds(anyList()))
                .thenReturn(List.of(dto(id)));

        mockMvc.perform(get("/api/v1/internal/documents/by-ids")
                        .param("ids", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()));
    }

    @Test
    void getDocument_byId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(documentQueryService.getDocumentsById(anyString()))
                .thenReturn(dto(id));

        mockMvc.perform(get("/api/v1/internal/documents/{documentId}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }
}

package com.bnpparibas.irb.qlickflow.documents.api.publicapi;

import com.bnpparibas.irb.qlickflow.documents.service.AttachmentResult;
import com.bnpparibas.irb.qlickflow.documents.service.DocumentAttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest pour PublicDocumentAttachmentController.
 * Couvre la branche principal == null -> "anonymous" (sans sécurité, principal est null
 * par défaut dans les requêtes MockMvc).
 *
 * ⚠️ ADAPTER : AttachmentResult (suppose @Builder + getters). Le endpoint attend un
 * @RequestParam("file") MultipartFile + Principal + Jwt. Sans sécurité, on poste juste le fichier.
 */
@WebMvcTest(PublicDocumentAttachmentController.class)
class PublicDocumentAttachmentControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean DocumentAttachmentService documentAttachmentService;

    @Test
    void stage_returns201_anonymousWhenNoPrincipal() throws Exception {
        UUID docId = UUID.randomUUID();
        AttachmentResult result = AttachmentResult.builder()
                .documentId(docId)
                .status("STAGED")
                .originalFileName("doc.pdf")
                .contentType("application/pdf")
                .size(123L)
                .sha256("hash")
                .stagedAt(OffsetDateTime.now())
                .createdBy("anonymous")
                .build();

        // principal null => le controller passe "anonymous"
        when(documentAttachmentService.stageDocument(any(), eq("anonymous")))
                .thenReturn(result);

        var file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "hello".getBytes());

        mockMvc.perform(multipart("/api/v1/public/documents/attachments:stage").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentId").value(docId.toString()))
                .andExpect(jsonPath("$.status").value("STAGED"))
                .andExpect(jsonPath("$.originalFileName").value("doc.pdf"))
                .andExpect(jsonPath("$.createdBy").value("anonymous"));
    }
}

package com.bnpparibas.irb.qlickflow.documents.api.publicapi;

import com.bnpparibas.irb.qlickflow.documents.service.DocumentDto;
import com.bnpparibas.irb.qlickflow.documents.service.DocumentQueryService;
import com.bnpparibas.irb.qlickflow.documents.service.DocumentStorageService;
import com.bnpparibas.irb.qlickflow.documents.service.InputStreamResourceWithType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest pour PublicDocumentQueryController.
 * Couvre by-owner, by-ids, by-id, et surtout download :
 *   - branche try OK (contentType parsable)
 *   - branche catch (contentType invalide -> APPLICATION_OCTET_STREAM)
 *   - sanitizeFilename (filename normal / null-blank -> "document")
 *
 * ⚠️ ADAPTER : DocumentDto possède getStorageKey()/storageKey() et contentType().
 * getEntityOrThrow renvoie un DocumentDto ici (vu dans les captures). openForDownload
 * renvoie un InputStreamResourceWithType exposant inputStream().
 */
@WebMvcTest(PublicDocumentQueryController.class)
class PublicDocumentQueryControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean DocumentQueryService documentQueryService;
    @MockBean DocumentStorageService documentStorageService;

    private DocumentDto dto(UUID id, String contentType, String fileName) {
        return new DocumentDto(id, "OWNER-1", fileName, contentType,
                123L, "ATTACHED", OffsetDateTime.now(), "user-1",
                OffsetDateTime.now(), "storage/key/1");
    }

    @Test
    void getDocuments_byOwner_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(documentQueryService.getDocumentsByOwnerRef("OWNER-1"))
                .thenReturn(List.of(dto(id, "application/pdf", "f.pdf")));

        mockMvc.perform(get("/api/v1/public/documents/by-owner")
                        .param("ownerRef", "OWNER-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()));
    }

    @Test
    void getDocumentsByIds_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(documentQueryService.getDocumentsByIds(anyList()))
                .thenReturn(List.of(dto(id, "application/pdf", "f.pdf")));

        mockMvc.perform(get("/api/v1/public/documents/by-ids")
                        .param("ids", id.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getDocument_byId_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(documentQueryService.getDocumentsById(anyString()))
                .thenReturn(dto(id, "application/pdf", "f.pdf"));

        mockMvc.perform(get("/api/v1/public/documents/{documentId}", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void download_returns200_withParsableContentType() throws Exception {
        UUID id = UUID.randomUUID();
        when(documentQueryService.getEntityOrThrow(anyString()))
                .thenReturn(dto(id, "application/pdf", "rapport.pdf"));
        when(documentStorageService.openForDownload(anyString()))
                .thenReturn(new InputStreamResourceWithType(
                        new ByteArrayInputStream("data".getBytes())));

        mockMvc.perform(get("/api/v1/public/documents/{documentId}/download", id.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("rapport.pdf")));
    }

    @Test
    void download_fallsBackToOctetStream_whenContentTypeInvalid() throws Exception {
        // contentType non parsable => catch => APPLICATION_OCTET_STREAM
        UUID id = UUID.randomUUID();
        when(documentQueryService.getEntityOrThrow(anyString()))
                .thenReturn(dto(id, "not a valid mime!!!", "rapport.pdf"));
        when(documentStorageService.openForDownload(anyString()))
                .thenReturn(new InputStreamResourceWithType(
                        new ByteArrayInputStream("data".getBytes())));

        mockMvc.perform(get("/api/v1/public/documents/{documentId}/download", id.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type",
                        "application/octet-stream"));
    }

    @Test
    void download_sanitizesNullFilename_toDocument() throws Exception {
        // originalFileName null => sanitizeFilename renvoie "document"
        UUID id = UUID.randomUUID();
        when(documentQueryService.getEntityOrThrow(anyString()))
                .thenReturn(dto(id, "application/pdf", null));
        when(documentStorageService.openForDownload(anyString()))
                .thenReturn(new InputStreamResourceWithType(
                        new ByteArrayInputStream("data".getBytes())));

        mockMvc.perform(get("/api/v1/public/documents/{documentId}/download", id.toString()))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("document")));
    }
}
