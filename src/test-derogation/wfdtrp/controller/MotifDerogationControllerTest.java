package com.bnpparibas.irb.qlickflow.wfdtrp.controller;
// ⚠️ TODO : ajuster le package selon l'emplacement réel des controllers référentiel.
// ⚠️ TODO : les facades sont supposées être des interfaces (MotifDerogationFacade, MotifDerogationFacade,
// PackageCommissionFacade, SegmentFacade) — si seules les Impl existent, remplacer le type des @Mock.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.ApiResponse;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.MotifDerogationDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.referentiel.MotifDerogationFacade;

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
class MotifDerogationControllerTest {

    @Mock
    private MotifDerogationFacade motifDerogationFacade;

    @InjectMocks
    private MotifDerogationController controller;

    @Test
    void getAllMotifs_returnsSuccessWithList() {
        List<MotifDerogationDTO> devises = List.of(MotifDerogationDTO.builder().code("M1").libelle("Motif un").build());
        when(motifDerogationFacade.findAllActifs()).thenReturn(devises);

        var response = controller.getAllMotifs();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getData()).isEqualTo(devises);
    }

    @Test
    void getMotifByCode_found_returns200() {
        MotifDerogationDTO devise = MotifDerogationDTO.builder().code("M1").libelle("Motif un").build();
        when(motifDerogationFacade.findByCode("M1")).thenReturn(Optional.of(devise));

        var response = controller.getMotifByCode("M1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo(devise);
    }

    @Test
    void getMotifByCode_unknown_returns404WithError() {
        when(motifDerogationFacade.findByCode("XXX")).thenReturn(Optional.empty());

        var response = controller.getMotifByCode("XXX");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isFalse();
        assertThat(body.getError()).contains("Motif not found for code :").contains("XXX");
    }
}
