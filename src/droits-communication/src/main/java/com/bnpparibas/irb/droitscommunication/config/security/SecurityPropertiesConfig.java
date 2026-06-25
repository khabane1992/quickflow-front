package com.bnpparibas.irb.droitscommunication.config.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Active la liaison de {@link QfSecurityProperties} sur le bloc {@code qf.security} du YAML.
 */
@Configuration
@EnableConfigurationProperties(QfSecurityProperties.class)
public class SecurityPropertiesConfig {
}
