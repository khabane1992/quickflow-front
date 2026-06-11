package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de DeviseServiceImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.Devise;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.DeviseRepository;

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
class DeviseServiceImplTest {

    @Mock
    private DeviseRepository deviseRepository;

    @InjectMocks
    private DeviseServiceImpl service;

    @Test
    void findAllActives_delegatesToRepository() {
        List<Devise> devises = List.of(new Devise("MAD", "Dirham marocain"));
        when(deviseRepository.findByActifTrue()).thenReturn(devises);

        assertThat(service.findAllActives()).isEqualTo(devises);
    }

    @Test
    void findByCode_delegatesToRepository() {
        Devise mad = new Devise("MAD", "Dirham marocain");
        when(deviseRepository.findByCodeAndActifTrue("MAD")).thenReturn(Optional.of(mad));

        assertThat(service.findByCode("MAD")).contains(mad);
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(deviseRepository.findByCodeAndActifTrue("XXX")).thenReturn(Optional.empty());

        assertThat(service.findByCode("XXX")).isEmpty();
    }
}
