package com.bnpparibas.irb.droitscommunication.referentiel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReferentielEntitiesTest {

    @Test
    void organisme_constructeurs_et_accesseurs() {
        Organisme o = new Organisme(1L, "DGFIP", "Finances Publiques", true);
        assertThat(o.getId()).isEqualTo(1L);
        assertThat(o.getCode()).isEqualTo("DGFIP");
        assertThat(o.getLibelle()).isEqualTo("Finances Publiques");
        assertThat(o.isActif()).isTrue();

        Organisme vide = new Organisme();
        vide.setCode("X");
        vide.setLibelle("Y");
        vide.setActif(false);
        assertThat(vide.getCode()).isEqualTo("X");
        assertThat(vide.isActif()).isFalse();
    }

    @Test
    void complementAdresse_constructeurs_et_accesseurs() {
        ComplementAdresse c = new ComplementAdresse(1L, "BAT", "Bâtiment", true);
        assertThat(c.getId()).isEqualTo(1L);
        assertThat(c.getCode()).isEqualTo("BAT");
        assertThat(c.getLibelle()).isEqualTo("Bâtiment");
        assertThat(c.isActif()).isTrue();

        ComplementAdresse vide = new ComplementAdresse();
        vide.setLibelle("Étage");
        assertThat(vide.getLibelle()).isEqualTo("Étage");
    }
}
