package com.bnpparibas.irb.droitscommunication.document;

import java.util.UUID;

/**
 * Port de récupération du binaire d'un document auprès du microservice Document.
 * Utilisé par le batch (Phase 2) pour télécharger le .xlsx à parser.
 *
 * <p>Impl. courante : {@link DocumentContentStub}. L'impl. réelle s'appuiera sur le
 * client généré (InternalDocumentQueryControllerApi) pour récupérer le contenu par id.
 */
public interface DocumentContentPort {

    /**
     * Télécharge le contenu binaire d'un document.
     *
     * @param documentId identifiant du document côté MS Document
     * @return le contenu du fichier (octets)
     */
    byte[] telecharger(UUID documentId);
}
