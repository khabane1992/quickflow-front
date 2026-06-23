package com.bnpparibas.irb.droitscommunication.service;

import com.bnpparibas.irb.droitscommunication.TestFixtures;
import com.bnpparibas.irb.droitscommunication.entity.DroitCommunicationEntity;
import com.bnpparibas.irb.droitscommunication.enums.StatutDemande;
import com.bnpparibas.irb.droitscommunication.exception.ReferentielInvalideException;
import com.bnpparibas.irb.droitscommunication.exception.RessourceIntrouvableException;
import com.bnpparibas.irb.droitscommunication.referentiel.ComplementAdresseRepository;
import com.bnpparibas.irb.droitscommunication.referentiel.OrganismeRepository;
import com.bnpparibas.irb.droitscommunication.repository.DroitCommunicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DroitCommunicationServiceImplTest {

    @Mock
    private DroitCommunicationRepository repository;
    @Mock
    private OrganismeRepository organismeRepository;
    @Mock
    private ComplementAdresseRepository complementAdresseRepository;

    @InjectMocks
    private DroitCommunicationServiceImpl service;

    private DroitCommunicationEntity entity;

    @BeforeEach
    void setUp() {
        entity = TestFixtures.entity();
    }

    @Test
    void create_devrait_sauvegarder_quand_referentiels_valides() {
        when(organismeRepository.existsByCode("DGFIP")).thenReturn(true);
        when(complementAdresseRepository.existsByCode("BAT")).thenReturn(true);
        when(repository.save(any())).thenReturn(entity);

        DroitCommunicationEntity result = service.createDroitCommunication(entity);

        assertThat(result).isEqualTo(entity);
        verify(repository).save(entity);
    }

    @Test
    void create_devrait_forcer_le_statut_SOUMISE_si_absent() {
        entity.setStatut(null);
        when(organismeRepository.existsByCode("DGFIP")).thenReturn(true);
        when(complementAdresseRepository.existsByCode("BAT")).thenReturn(true);
        when(repository.save(any())).thenReturn(entity);

        service.createDroitCommunication(entity);

        ArgumentCaptor<DroitCommunicationEntity> captor =
                ArgumentCaptor.forClass(DroitCommunicationEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getStatut()).isEqualTo(StatutDemande.SOUMISE);
    }

    @Test
    void create_devrait_accepter_complement_adresse_vide() {
        entity.setComplementAdresse("  ");
        when(organismeRepository.existsByCode("DGFIP")).thenReturn(true);
        when(repository.save(any())).thenReturn(entity);

        service.createDroitCommunication(entity);

        verify(complementAdresseRepository, never()).existsByCode(any());
        verify(repository).save(entity);
    }

    @Test
    void create_devrait_echouer_si_organisme_inconnu() {
        when(organismeRepository.existsByCode("DGFIP")).thenReturn(false);

        assertThatThrownBy(() -> service.createDroitCommunication(entity))
                .isInstanceOf(ReferentielInvalideException.class)
                .hasMessageContaining("Organisme inconnu");

        verify(repository, never()).save(any());
    }

    @Test
    void create_devrait_echouer_si_complement_adresse_inconnu() {
        when(organismeRepository.existsByCode("DGFIP")).thenReturn(true);
        when(complementAdresseRepository.existsByCode("BAT")).thenReturn(false);

        assertThatThrownBy(() -> service.createDroitCommunication(entity))
                .isInstanceOf(ReferentielInvalideException.class)
                .hasMessageContaining("Complément d'adresse inconnu");

        verify(repository, never()).save(any());
    }

    @Test
    void getById_devrait_retourner_la_demande() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(service.getById(1L)).isEqualTo(entity);
    }

    @Test
    void getById_devrait_echouer_si_introuvable() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(RessourceIntrouvableException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAll_devrait_retourner_la_liste() {
        when(repository.findAll()).thenReturn(List.of(entity));

        assertThat(service.getAll()).containsExactly(entity);
    }
}
