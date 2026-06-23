package com.bnpparibas.irb.droitscommunication.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JpaAuditingConfigTest {

    @Test
    void instanciation() {
        // La classe ne porte que des annotations ; on couvre son instanciation.
        assertThat(new JpaAuditingConfig()).isNotNull();
    }
}
