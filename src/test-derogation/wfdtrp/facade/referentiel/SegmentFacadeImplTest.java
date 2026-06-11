package com.bnpparibas.irb.qlickflow.wfdtrp.facade.referentiel;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de SegmentFacadeImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.SegmentDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.Segment;
import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.SegmentMapper;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.SegmentService;

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
class SegmentFacadeImplTest {

    @Mock
    private SegmentMapper segmentMapper;
    @Mock
    private SegmentService segmentService;

    @InjectMocks
    private SegmentFacadeImpl facade;

    @Test
    void findAllActifs_mapsEachEntity() {
        Segment mad = new Segment("GRP", "Grand public");
        Segment eur = new Segment("PRE", "Premium");
        when(segmentService.findAllActifs()).thenReturn(List.of(mad, eur));
        when(segmentMapper.toDto(mad)).thenReturn(SegmentDTO.builder().code("GRP").libelle("Grand public").build());
        when(segmentMapper.toDto(eur)).thenReturn(SegmentDTO.builder().code("PRE").libelle("Euro").build());

        List<SegmentDTO> result = facade.findAllActifs();

        assertThat(result).extracting(SegmentDTO::getCode).containsExactly("GRP", "PRE");
    }

    @Test
    void findByCode_found_mapsToDto() {
        Segment mad = new Segment("GRP", "Grand public");
        when(segmentService.findByCode("GRP")).thenReturn(Optional.of(mad));
        when(segmentMapper.toDto(mad)).thenReturn(SegmentDTO.builder().code("GRP").build());

        Optional<SegmentDTO> result = facade.findByCode("GRP");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("GRP");
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(segmentService.findByCode("XXX")).thenReturn(Optional.empty());

        assertThat(facade.findByCode("XXX")).isEmpty();
    }
}
