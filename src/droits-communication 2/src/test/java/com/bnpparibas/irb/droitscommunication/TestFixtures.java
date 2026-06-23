package com.bnpparibas.irb.droitscommunication;

import com.bnpparibas.irb.droitscommunication.dto.DroitCommunicationRequest;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Données de test réutilisables.
 */
public final class TestFixtures {

    private TestFixtures() {
    }

    public static DroitCommunicationRequest request() {
        return new DroitCommunicationRequest(
                "DGFIP",
                "12 rue de la Paix, 75002 Paris",
                "BAT",
                "Communication des relevés de compte sur 12 mois",
                CanalDemande.COURRIER,
                "REF-2026-00123",
                LocalDate.of(2026, 6, 20),
                Set.of(TypeDemande.SOLDE, TypeDemande.RIB),
                "doc-uuid-123",
                "liste_droits.pdf",
                "application/pdf"
        );
    }

    public static DroitCommunicationEntity entity() {
        DroitCommunicationEntity e = new DroitCommunicationEntity();
        e.setId(1L);
        e.setOrganisme("DGFIP");
        e.setAdressePostale("12 rue de la Paix, 75002 Paris");
        e.setComplementAdresse("BAT");
        e.setObjetDemande("Communication des relevés de compte sur 12 mois");
        e.setCanalDemande(CanalDemande.COURRIER);
        e.setReferenceDemande("REF-2026-00123");
        e.setDateReception(LocalDate.of(2026, 6, 20));
        e.setTypesDemande(Set.of(TypeDemande.SOLDE, TypeDemande.RIB));
        e.setDocumentReference("doc-uuid-123");
        e.setDocumentNom("liste_droits.pdf");
        e.setDocumentContentType("application/pdf");
        e.setStatut(StatutDemande.SOUMISE);
        e.setDateCreation(LocalDateTime.of(2026, 6, 20, 10, 0));
        e.setDateModification(LocalDateTime.of(2026, 6, 20, 10, 0));
        return e;
    }
}
