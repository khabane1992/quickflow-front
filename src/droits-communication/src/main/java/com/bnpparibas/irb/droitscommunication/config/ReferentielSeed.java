package com.bnpparibas.irb.droitscommunication.config;

import com.bnpparibas.irb.droitscommunication.referentiel.Organisme;
import com.bnpparibas.irb.droitscommunication.referentiel.OrganismeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Alimente le référentiel des organismes au démarrage s'il est vide.
 * En production, ces données viendraient d'un script de migration (Flyway/Liquibase)
 * ou d'une administration dédiée.
 */
@Configuration
public class ReferentielSeed {

    @Bean
    CommandLineRunner seedReferentiels(OrganismeRepository organismeRepository) {
        return args -> {
            if (organismeRepository.count() == 0) {
                organismeRepository.save(new Organisme(null, "BAM", "Bank Al-Maghrib", true));
                organismeRepository.save(new Organisme(null, "TGR", "Trésorerie Générale du Royaume", true));
                organismeRepository.save(new Organisme(null, "CNSS", "Caisse Nationale de Sécurité Sociale", true));
                organismeRepository.save(new Organisme(null, "DOUANE", "Administration des Douanes (ADII)", true));
                organismeRepository.save(new Organisme(null, "TRIBUNAL", "Tribunal de Commerce", true));
                organismeRepository.save(new Organisme(null, "PERCEPTION", "Perception / Recette", true));
            }
        };
    }
}
