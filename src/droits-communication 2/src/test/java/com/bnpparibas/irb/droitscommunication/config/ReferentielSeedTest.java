package com.bnpparibas.irb.droitscommunication.config;

import com.bnpparibas.irb.droitscommunication.referentiel.ComplementAdresseRepository;
import com.bnpparibas.irb.droitscommunication.referentiel.OrganismeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferentielSeedTest {

    @Mock
    private OrganismeRepository organismeRepository;
    @Mock
    private ComplementAdresseRepository complementAdresseRepository;

    private final ReferentielSeed seed = new ReferentielSeed();

    @Test
    void seed_devrait_inserer_quand_les_tables_sont_vides() throws Exception {
        when(organismeRepository.count()).thenReturn(0L);
        when(complementAdresseRepository.count()).thenReturn(0L);

        CommandLineRunner runner =
                seed.seedReferentiels(organismeRepository, complementAdresseRepository);
        runner.run();

        verify(organismeRepository, atLeastOnce()).save(org.mockito.ArgumentMatchers.any());
        verify(complementAdresseRepository, atLeastOnce()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void seed_ne_devrait_rien_inserer_quand_les_tables_sont_remplies() throws Exception {
        when(organismeRepository.count()).thenReturn(5L);
        when(complementAdresseRepository.count()).thenReturn(4L);

        CommandLineRunner runner =
                seed.seedReferentiels(organismeRepository, complementAdresseRepository);
        runner.run();

        verify(organismeRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(complementAdresseRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
