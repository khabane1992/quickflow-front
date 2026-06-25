package com.bnpparibas.irb.droitscommunication.document;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Implémentation temporaire du {@link DocumentAttachmentPort} : se contente de tracer
 * l'appel, le temps de générer/brancher le vrai client OpenAPI du MS Document.
 *
 * <p>À remplacer par une impl. annotée {@code @Primary} appelant
 * {@code InternalDocumentAttachmentControllerApi.commit(new CommitDocumentsRequest()
 * .documentIds(...).module(MODULE_DC).ownerRef(businessKey).userUid(...))}.
 */
@Component
@Slf4j
public class DocumentAttachmentStub implements DocumentAttachmentPort {

    @Override
    public void commit(UUID businessKey, List<UUID> documentIds, String userUid) {
        log.info("[STUB MS Document] commit module={} ownerRef={} documentIds={} userUid={}",
                MODULE_DC, businessKey, documentIds, userUid);
    }
}
