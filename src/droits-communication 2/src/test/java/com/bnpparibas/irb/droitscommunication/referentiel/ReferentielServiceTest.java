package com.bnpparibas.irb.droitscommunication.referentiel;

import com.bnpparibas.irb.droitscommunication.dto.ReferentielItem;
import com.bnpparibas.irb.droitscommunication.enums.CanalDemande;
import com.bnpparibas.irb.droitscommunication.enums.TypeDemande;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferentielServiceTest {

    @Mock
    private OrganismeRepository organismeRepository;
    @Mock
    private ComplementAdresseRepository complementAdresseRepository;

    @InjectMocks
    private ReferentielService service;

    @Test
    void getOrganismes_devrait_mapper_code_et_libelle() {
        when(organismeRepository.findByActifTrueOrderByLibelleAsc())
                .thenReturn(List.of(new Organisme(1L, "DGFIP", "Direction Générale des Finances Publiques", true)));

        List<ReferentielItem> result = service.getOrganismes();

        assertThat(result).containsExactly(
                new ReferentielItem("DGFIP", "Direction Générale des Finances Publiques"));
    }

    @Test
    void getComplementsAdresse_devrait_mapper_code_et_libelle() {
        when(complementAdresseRepository.findByActifTrueOrderByLibelleAsc())
                .thenReturn(List.of(new ComplementAdresse(1L, "BAT", "Bâtiment", true)));

        List<ReferentielItem> result = service.getComplementsAdresse();

        assertThat(result).containsExactly(new ReferentielItem("BAT", "Bâtiment"));
    }

    @Test
    void getCanauxDemande_devrait_exposer_tous_les_enums() {
        List<ReferentielItem> result = service.getCanauxDemande();

        assertThat(result).hasSize(CanalDemande.values().length);
        assertThat(result).contains(
                new ReferentielItem("COURRIER", CanalDemande.COURRIER.getLibelle()));
    }

    @Test
    void getTypesDemande_devrait_exposer_tous_les_enums() {
        List<ReferentielItem> result = service.getTypesDemande();

        assertThat(result).hasSize(TypeDemande.values().length);
        assertThat(result).contains(
                new ReferentielItem("SOLDE", TypeDemande.SOLDE.getLibelle()));
    }
}
