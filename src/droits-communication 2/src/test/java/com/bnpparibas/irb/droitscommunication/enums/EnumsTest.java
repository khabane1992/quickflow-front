package com.bnpparibas.irb.droitscommunication.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnumsTest {

    @Test
    void canalDemande_libelles() {
        assertThat(CanalDemande.COURRIER.getLibelle()).isEqualTo("Courrier");
        assertThat(CanalDemande.valueOf("EMAIL")).isEqualTo(CanalDemande.EMAIL);
        assertThat(CanalDemande.values()).isNotEmpty();
    }

    @Test
    void typeDemande_libelles() {
        assertThat(TypeDemande.SOLDE.getLibelle()).isEqualTo("Solde");
        assertThat(TypeDemande.valueOf("RIB")).isEqualTo(TypeDemande.RIB);
        assertThat(TypeDemande.values()).hasSize(5);
    }

    @Test
    void statutDemande_libelles() {
        assertThat(StatutDemande.SOUMISE.getLibelle()).isEqualTo("Soumise");
        assertThat(StatutDemande.valueOf("TRAITEE")).isEqualTo(StatutDemande.TRAITEE);
        assertThat(StatutDemande.values()).isNotEmpty();
    }
}
