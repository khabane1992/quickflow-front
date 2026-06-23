package com.bnpparibas.irb.droitscommunication.config;

import com.bnpparibas.irb.droitscommunication.referentiel.ComplementAdresse;
import com.bnpparibas.irb.droitscommunication.referentiel.ComplementAdresseRepository;
import com.bnpparibas.irb.droitscommunication.referentiel.Organisme;
import com.bnpparibas.irb.droitscommunication.referentiel.OrganismeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Alimente les référentiels en base au démarrage s'ils sont vides.
 * En production, ces données viendraient d'un script de migration (Flyway/Liquibase)
 * ou d'une administration dédiée.
 */
@Configuration
public class ReferentielSeed {

    @Bean
    CommandLineRunner seedReferentiels(OrganismeRepository organismeRepository,
                                       ComplementAdresseRepository complementAdresseRepository) {
        return args -> {
            if (organismeRepository.count() == 0) {
                organismeRepository.save(new Organisme(null, "DGFIP", "Direction Générale des Finances Publiques", true));
                organismeRepository.save(new Organisme(null, "URSSAF", "URSSAF", true));
                organismeRepository.save(new Organisme(null, "TRIBUNAL", "Tribunal Judiciaire", true));
                organismeRepository.save(new Organisme(null, "DOUANE", "Direction Générale des Douanes", true));
                organismeRepository.save(new Organisme(null, "POLICE", "Services de Police / Gendarmerie", true));
            }
            if (complementAdresseRepository.count() == 0) {
                complementAdresseRepository.save(new ComplementAdresse(null, "BAT", "Bâtiment", true));
                complementAdresseRepository.save(new ComplementAdresse(null, "ETAGE", "Étage", true));
                complementAdresseRepository.save(new ComplementAdresse(null, "BP", "Boîte postale", true));
                complementAdresseRepository.save(new ComplementAdresse(null, "ZI", "Zone industrielle", true));
            }
        };
    }
}
