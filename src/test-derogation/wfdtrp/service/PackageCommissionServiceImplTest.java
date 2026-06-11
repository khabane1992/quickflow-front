package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de PackageCommissionServiceImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.PackageCommission;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.PackageCommissionRepository;

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
class PackageCommissionServiceImplTest {

    @Mock
    private PackageCommissionRepository packageCommissionRepository;

    @InjectMocks
    private PackageCommissionServiceImpl service;

    @Test
    void findAllActifs_delegatesToRepository() {
        List<PackageCommission> packages = List.of(
                new PackageCommission("PACK1", "Package 1", List.of("C1", "C2")));
        when(packageCommissionRepository.findByActifTrue()).thenReturn(packages);

        assertThat(service.findAllActifs()).isEqualTo(packages);
    }

    @Test
    void findByCode_delegatesToRepository() {
        PackageCommission ref = new PackageCommission("PACK1", "Package 1", List.of("C1"));
        when(packageCommissionRepository.findByCodeAndActifTrue("PACK1")).thenReturn(Optional.of(ref));

        assertThat(service.findByCode("PACK1")).contains(ref);
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(packageCommissionRepository.findByCodeAndActifTrue("XXX")).thenReturn(Optional.empty());

        assertThat(service.findByCode("XXX")).isEmpty();
    }
}
