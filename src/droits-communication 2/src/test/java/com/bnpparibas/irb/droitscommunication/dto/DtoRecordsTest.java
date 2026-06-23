package com.bnpparibas.irb.droitscommunication.dto;

import com.bnpparibas.irb.droitscommunication.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoRecordsTest {

    @Test
    void referentielItem_accesseurs() {
        ReferentielItem item = new ReferentielItem("CODE", "Libellé");
        assertThat(item.code()).isEqualTo("CODE");
        assertThat(item.libelle()).isEqualTo("Libellé");
        assertThat(item).isEqualTo(new ReferentielItem("CODE", "Libellé"));
    }

    @Test
    void request_accesseurs() {
        DroitCommunicationRequest request = TestFixtures.request();
        assertThat(request.organisme()).isEqualTo("DGFIP");
        assertThat(request.typesDemande()).isNotEmpty();
        assertThat(request.documentNom()).isEqualTo("liste_droits.pdf");
    }

    @Test
    void response_accesseurs() {
        var e = TestFixtures.entity();
        DroitCommunicationResponse response = new DroitCommunicationResponse(
                e.getId(), e.getOrganisme(), e.getAdressePostale(), e.getComplementAdresse(),
                e.getObjetDemande(), e.getCanalDemande(), e.getReferenceDemande(),
                e.getDateReception(), e.getTypesDemande(), e.getDocumentReference(),
                e.getDocumentNom(), e.getDocumentContentType(), e.getStatut(),
                e.getDateCreation(), e.getDateModification());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.canalDemande()).isEqualTo(e.getCanalDemande());
    }
}
