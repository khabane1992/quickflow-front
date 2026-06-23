package com.bnpparibas.irb.droitscommunication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Active le remplissage automatique de dateCreation / dateModification.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
