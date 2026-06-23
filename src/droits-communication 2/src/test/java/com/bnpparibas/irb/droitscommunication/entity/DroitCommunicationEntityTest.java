package com.bnpparibas.irb.droitscommunication.entity;

import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DroitCommunicationEntityTest {

    @Test
    void builder_devrait_construire_une_entite_complete() {
        DroitCommunicationEntity e = DroitCommunicationEntity.builder()
                .organisme("DGFIP")
                .adressePostale("12 rue de la Paix")
                .objetDemande("Objet")
                .canalDemande(CanalDemande.EMAIL)
                .dateReception(LocalDate.of(2026, 1, 1))
                .typesDemande(Set.of(TypeDemande.SOLDE))
                .statut(StatutDemande.EN_COURS)
                .build();

        assertThat(e.getOrganisme()).isEqualTo("DGFIP");
        assertThat(e.getCanalDemande()).isEqualTo(CanalDemande.EMAIL);
        assertThat(e.getTypesDemande()).containsExactly(TypeDemande.SOLDE);
        assertThat(e.getStatut()).isEqualTo(StatutDemande.EN_COURS);
    }

    @Test
    void typesDemande_par_defaut_devrait_etre_un_set_vide() {
        DroitCommunicationEntity e = DroitCommunicationEntity.builder()
                .organisme("X")
                .adressePostale("Y")
                .objetDemande("Z")
                .canalDemande(CanalDemande.GUICHET)
                .dateReception(LocalDate.now())
                .build();

        assertThat(e.getTypesDemande()).isEmpty();
        assertThat(e.getStatut()).isEqualTo(StatutDemande.SOUMISE);
    }

    @Test
    void no_args_et_setters() {
        DroitCommunicationEntity e = new DroitCommunicationEntity();
        e.setReferenceDemande("REF-1");
        e.setDocumentNom("f.pdf");
        e.setDocumentContentType("application/pdf");
        e.setComplementAdresse("BAT");

        assertThat(e.getReferenceDemande()).isEqualTo("REF-1");
        assertThat(e.getDocumentNom()).isEqualTo("f.pdf");
        assertThat(e.getComplementAdresse()).isEqualTo("BAT");
    }
}
