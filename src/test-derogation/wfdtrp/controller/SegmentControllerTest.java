package com.bnpparibas.irb.qlickflow.wfdtrp.controller;
// ⚠️ TODO : ajuster le package selon l'emplacement réel des controllers référentiel.
// ⚠️ TODO : les facades sont supposées être des interfaces (SegmentFacade, MotifDerogationFacade,
// PackageCommissionFacade, SegmentFacade) — si seules les Impl existent, remplacer le type des @Mock.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.ApiResponse;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.SegmentDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.referentiel.SegmentFacade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SegmentControllerTest {

    @Mock
    private SegmentFacade segmentFacade;

    @InjectMocks
    private SegmentController controller;

    @Test
    void getAllSegments_returnsSuccessWithList() {
        List<SegmentDTO> devises = List.of(SegmentDTO.builder().code("GRP").libelle("Grand public").build());
        when(segmentFacade.findAllActifs()).thenReturn(devises);

        var response = controller.getAllSegments();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getData()).isEqualTo(devises);
    }

    @Test
    void getSegmentByCode_found_returns200() {
        SegmentDTO devise = SegmentDTO.builder().code("GRP").libelle("Grand public").build();
        when(segmentFacade.findByCode("GRP")).thenReturn(Optional.of(devise));

        var response = controller.getSegmentByCode("GRP");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo(devise);
    }

    @Test
    void getSegmentByCode_unknown_returns404WithError() {
        when(segmentFacade.findByCode("XXX")).thenReturn(Optional.empty());

        var response = controller.getSegmentByCode("XXX");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isFalse();
        assertThat(body.getError()).contains("segment not found for code :").contains("XXX");
    }
}
