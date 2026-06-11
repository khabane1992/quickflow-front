package com.bnpparibas.irb.qlickflow.wfdtrp.controller;
// ⚠️ TODO : ajuster le package selon l'emplacement réel du controller.
// ⚠️ Rappel : l'URL réelle contient la typo "packages-commisions" et la méthode liste
// s'appelle réellement getAllMotifs() (copié-collé dans le code de prod).

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.ApiResponse;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.PackageCommissionDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.referentiel.PackageCommissionFacade;

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
class PackageCommissionControllerTest {

    @Mock
    private PackageCommissionFacade packageCommissionFacade;

    @InjectMocks
    private PackageCommissionController controller;

    @Test
    void getAllMotifs_returnsSuccessWithList() { // nom réel de la méthode (copié-collé en prod)
        List<PackageCommissionDTO> packages = List.of(
                PackageCommissionDTO.builder().code("PACK1").libelle("Package 1")
                        .codesCommission(List.of("C1", "C2")).build());
        when(packageCommissionFacade.findAllActifs()).thenReturn(packages);

        var response = controller.getAllMotifs();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getData()).isEqualTo(packages);
    }

    @Test
    void getPackageByCode_found_returns200() {
        PackageCommissionDTO pack = PackageCommissionDTO.builder().code("PACK1").libelle("Package 1").build();
        when(packageCommissionFacade.findByCode("PACK1")).thenReturn(Optional.of(pack));

        var response = controller.getPackageByCode("PACK1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()).getData()).isEqualTo(pack);
    }

    @Test
    void getPackageByCode_unknown_returns404WithError() {
        when(packageCommissionFacade.findByCode("XXX")).thenReturn(Optional.empty());

        var response = controller.getPackageByCode("XXX");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isFalse();
        assertThat(body.getError()).contains("Package Commission not found for code :").contains("XXX");
    }

    @Test
    void getCodesCommission_returnsSuccessWithCodes() {
        when(packageCommissionFacade.getCodesCommissionByPackageCode("PACK1"))
                .thenReturn(List.of("C1", "C2"));

        var response = controller.getCodesCommission("PACK1");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getData()).isEqualTo(List.of("C1", "C2"));
    }
}
