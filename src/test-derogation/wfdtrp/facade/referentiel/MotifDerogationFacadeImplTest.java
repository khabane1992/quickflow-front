package com.bnpparibas.irb.qlickflow.wfdtrp.facade.referentiel;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de MotifDerogationFacadeImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.MotifDerogationDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.MotifDerogation;
import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.MotifDerogationMapper;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.MotifDerogationService;

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
class MotifDerogationFacadeImplTest {

    @Mock
    private MotifDerogationMapper motifDerogationMapper;
    @Mock
    private MotifDerogationService motifDerogationService;

    @InjectMocks
    private MotifDerogationFacadeImpl facade;

    @Test
    void findAllActifs_mapsEachEntity() {
        MotifDerogation mad = new MotifDerogation("M1", "Motif un");
        MotifDerogation eur = new MotifDerogation("M2", "Motif deux");
        when(motifDerogationService.findAllActifs()).thenReturn(List.of(mad, eur));
        when(motifDerogationMapper.toDto(mad)).thenReturn(MotifDerogationDTO.builder().code("M1").libelle("Motif un").build());
        when(motifDerogationMapper.toDto(eur)).thenReturn(MotifDerogationDTO.builder().code("M2").libelle("Euro").build());

        List<MotifDerogationDTO> result = facade.findAllActifs();

        assertThat(result).extracting(MotifDerogationDTO::getCode).containsExactly("M1", "M2");
    }

    @Test
    void findByCode_found_mapsToDto() {
        MotifDerogation mad = new MotifDerogation("M1", "Motif un");
        when(motifDerogationService.findByCode("M1")).thenReturn(Optional.of(mad));
        when(motifDerogationMapper.toDto(mad)).thenReturn(MotifDerogationDTO.builder().code("M1").build());

        Optional<MotifDerogationDTO> result = facade.findByCode("M1");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("M1");
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(motifDerogationService.findByCode("XXX")).thenReturn(Optional.empty());

        assertThat(facade.findByCode("XXX")).isEmpty();
    }
}
