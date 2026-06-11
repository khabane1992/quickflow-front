package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de SegmentServiceImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.Segment;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.SegmentRepository;

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
class SegmentServiceImplTest {

    @Mock
    private SegmentRepository segmentRepository;

    @InjectMocks
    private SegmentServiceImpl service;

    @Test
    void findAllActifs_delegatesToRepository() {
        List<Segment> segments = List.of(new Segment("GRP", "Grand public"));
        when(segmentRepository.findByActifTrue()).thenReturn(segments);

        assertThat(service.findAllActifs()).isEqualTo(segments);
    }

    @Test
    void findByCode_delegatesToRepository() {
        Segment ref = new Segment("GRP", "Grand public");
        when(segmentRepository.findByCodeAndActifTrue("GRP")).thenReturn(Optional.of(ref));

        assertThat(service.findByCode("GRP")).contains(ref);
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(segmentRepository.findByCodeAndActifTrue("XXX")).thenReturn(Optional.empty());

        assertThat(service.findByCode("XXX")).isEmpty();
    }
}
