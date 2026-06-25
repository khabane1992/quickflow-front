package com.bnpparibas.irb.droitscommunication.document;

import java.util.List;
import java.util.UUID;

/**
 * Port d'attachement vers le microservice Document, en miroir du pattern plateforme
 * ({@code commitAttachments} de DTRP).
 *
 * <p>Le front a uploadé le fichier (draft) au MS Document et nous a passé son UUID ;
 * à la création de la demande on <b>commit</b> ce document : il devient définitivement
 * rattaché à notre objet métier (ownerRef = businessKey, module = {@link #MODULE_DC}).
 *
 * <p>Impl. courante : {@link DocumentAttachmentStub} (temporaire). L'impl. réelle
 * s'appuiera sur le client généré depuis l'OpenAPI du MS Document
 * (ApiClient OkHttp + InternalDocumentAttachmentControllerApi).
 */
public interface DocumentAttachmentPort {

    /** Code module de ce microservice côté MS Document. */
    String MODULE_DC = "DC";

    /**
     * Commit (rattache) les documents à l'objet métier.
     *
     * @param businessKey clé métier servant d'ownerRef côté MS Document
     * @param documentIds identifiants des documents uploadés (draft) à rattacher
     * @param userUid     utilisateur à l'origine de l'action
     */
    void commit(UUID businessKey, List<UUID> documentIds, String userUid);
}
