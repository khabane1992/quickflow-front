package com.bnpparibas.irb.qlickflow.wfdtrp.controller;
// ⚠️ TODO : ajuster le package selon l'emplacement réel des controllers référentiel.
// ⚠️ TODO : les facades sont supposées être des interfaces (DeviseFacade, MotifDerogationFacade,
// PackageCommissionFacade, SegmentFacade) — si seules les Impl existent, remplacer le type des @Mock.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.ApiResponse;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.DeviseDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.referentiel.DeviseFacade;

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
class DeviseControllerTest {

    @Mock
    private DeviseFacade deviseFacade;

    @InjectMocks
    private DeviseController controller;

    @Test
    void getAllDevises_returnsSuccessWithList() {
        List<DeviseDTO> devises = List.of(DeviseDTO.builder().code("MAD").libelle("Dirham marocain").build());
        when(deviseFacade.findAllActives()).thenReturn(devises);

        var response = controller.getAllDevises();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getData()).isEqualTo(devises);
    }

    @Test
    void getDeviseByCode_found_returns200() {
        DeviseDTO devise = DeviseDTO.builder().code("MAD").libelle("Dirham marocain").build();
        when(deviseFacade.findByCode("MAD")).thenReturn(Optional.of(devise));

        var response = controller.getDeviseByCode("MAD");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo(devise);
    }

    @Test
    void getDeviseByCode_unknown_returns404WithError() {
        when(deviseFacade.findByCode("XXX")).thenReturn(Optional.empty());

        var response = controller.getDeviseByCode("XXX");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isFalse();
        assertThat(body.getError()).contains("Devise not found for code :").contains("XXX");
    }
}
