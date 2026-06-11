package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de MotifDerogationServiceImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.MotifDerogation;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.MotifDerogationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MotifDerogationServiceImplTest {

    @Mock
    private MotifDerogationRepository motifDerogationRepository;

    @InjectMocks
    private MotifDerogationServiceImpl service;

    @Test
    void findAllActifs_delegatesToRepository() {
        List<MotifDerogation> motifs = List.of(new MotifDerogation("M1", "Motif un"));
        when(motifDerogationRepository.findByActifTrue()).thenReturn(motifs);

        assertThat(service.findAllActifs()).isEqualTo(motifs);
    }

    @Test
    void findByCode_delegatesToRepository() {
        MotifDerogation ref = new MotifDerogation("M1", "Motif un");
        when(motifDerogationRepository.findByCodeAndActifTrue("M1")).thenReturn(Optional.of(ref));

        assertThat(service.findByCode("M1")).contains(ref);
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(motifDerogationRepository.findByCodeAndActifTrue("XXX")).thenReturn(Optional.empty());

        assertThat(service.findByCode("XXX")).isEmpty();
    }
}
