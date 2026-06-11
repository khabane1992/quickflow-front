package com.bnpparibas.irb.qlickflow.wfdtrp.facade.referentiel;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de DeviseFacadeImpl.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.DeviseDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.Devise;
import com.bnpparibas.irb.qlickflow.wfdtrp.mapper.DeviseMapper;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.DeviseService;

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
class DeviseFacadeImplTest {

    @Mock
    private DeviseMapper deviseMapper;
    @Mock
    private DeviseService deviseService;

    @InjectMocks
    private DeviseFacadeImpl facade;

    @Test
    void findAllActives_mapsEachEntity() {
        Devise mad = new Devise("MAD", "Dirham marocain");
        Devise eur = new Devise("EUR", "Euro");
        when(deviseService.findAllActives()).thenReturn(List.of(mad, eur));
        when(deviseMapper.toDto(mad)).thenReturn(DeviseDTO.builder().code("MAD").libelle("Dirham marocain").build());
        when(deviseMapper.toDto(eur)).thenReturn(DeviseDTO.builder().code("EUR").libelle("Euro").build());

        List<DeviseDTO> result = facade.findAllActives();

        assertThat(result).extracting(DeviseDTO::getCode).containsExactly("MAD", "EUR");
    }

    @Test
    void findByCode_found_mapsToDto() {
        Devise mad = new Devise("MAD", "Dirham marocain");
        when(deviseService.findByCode("MAD")).thenReturn(Optional.of(mad));
        when(deviseMapper.toDto(mad)).thenReturn(DeviseDTO.builder().code("MAD").build());

        Optional<DeviseDTO> result = facade.findByCode("MAD");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("MAD");
    }

    @Test
    void findByCode_notFound_returnsEmpty() {
        when(deviseService.findByCode("XXX")).thenReturn(Optional.empty());

        assertThat(facade.findByCode("XXX")).isEmpty();
    }
}
