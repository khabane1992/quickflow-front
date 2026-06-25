package com.bnpparibas.irb.droitscommunication.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Propriétés de sécurité (préfixe {@code qf.security}).
 * Calquées sur le pattern du projet frère qlickflow-wfdtrp.
 *
 * @param authMode        mode d'authentification : {@code mock} (dev) ou {@code oauth} (prod)
 * @param publicEndpoints patterns Ant des endpoints accessibles sans authentification
 */
@ConfigurationProperties(prefix = "qf.security")
public record QfSecurityProperties(
        String authMode,
        List<String> publicEndpoints) {
}
