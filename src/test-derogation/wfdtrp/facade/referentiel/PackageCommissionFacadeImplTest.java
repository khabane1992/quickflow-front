package com.bnpparibas.irb.qlickflow.wfdtrp.facade.referentiel;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de PackageCommissionFacadeImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.PackageCommissionDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.PackageCommission;
import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.PackageCommissionMapper;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.PackageCommissionService;

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
class PackageCommissionFacadeImplTest {

    @Mock
    private PackageCommissionMapper packageCommissionMapper;
    @Mock
    private PackageCommissionService packageCommissionService;

    @InjectMocks
    private PackageCommissionFacadeImpl facade;

    @Test
    void findAllActifs_mapsEachEntity() {
        PackageCommission p1 = new PackageCommission("PACK1", "Package 1", List.of("C1", "C2"));
        when(packageCommissionService.findAllActifs()).thenReturn(List.of(p1));
        when(packageCommissionMapper.toDto(p1))
                .thenReturn(PackageCommissionDTO.builder().code("PACK1").libelle("Package 1").build());

        List<PackageCommissionDTO> result = facade.findAllActifs();

        assertThat(result).extracting(PackageCommissionDTO::getCode).containsExactly("PACK1");
    }

    @Test
    void findByCode_found_mapsToDto() {
        PackageCommission p1 = new PackageCommission("PACK1", "Package 1", List.of("C1"));
        when(packageCommissionService.findByCode("PACK1")).thenReturn(Optional.of(p1));
        when(packageCommissionMapper.toDto(p1)).thenReturn(PackageCommissionDTO.builder().code("PACK1").build());

        assertThat(facade.findByCode("PACK1")).isPresent();
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(packageCommissionService.findByCode("XXX")).thenReturn(Optional.empty());

        assertThat(facade.findByCode("XXX")).isEmpty();
    }

    @Test
    void getCodesCommissionByPackageCode_found_returnsCodes() {
        PackageCommission p1 = new PackageCommission("PACK1", "Package 1", List.of("C1", "C2"));
        when(packageCommissionService.findByCode("PACK1")).thenReturn(Optional.of(p1));

        assertThat(facade.getCodesCommissionByPackageCode("PACK1")).containsExactly("C1", "C2");
    }

    @Test
    void getCodesCommissionByPackageCode_notFound_returnsEmptyList() {
        when(packageCommissionService.findByCode("XXX")).thenReturn(Optional.empty());

        // branche orElse(List.of())
        assertThat(facade.getCodesCommissionByPackageCode("XXX")).isEmpty();
    }
}
