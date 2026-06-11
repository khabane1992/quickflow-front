package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de UserDetailsServiceImpl (service ou facade).

import com.bnpparibas.irb.qlickflow.wfdtrp.integration.InternalUserDTO;
// TODO : ajuster les imports (InternalUserControllerApi, InternalUserDTO = classes générées)

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private InternalUserControllerApi internalUserControllerApi;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Test
    void fetchUser_delegatesToApi() {
        InternalUserDTO dto = mock(InternalUserDTO.class);
        when(internalUserControllerApi.getUserInfo("u1")).thenReturn(dto);

        assertThat(service.fetchUser("u1")).isEqualTo(dto);
    }

    @Test
    void fetchActiveUsersByOrgaUnit_delegatesToApi() {
        List<InternalUserDTO> users = List.of(mock(InternalUserDTO.class));
        when(internalUserControllerApi.getEnabledUsersByOrgaUnit(10L)).thenReturn(users);

        assertThat(service.fetchActiveUsersByOrgaUnit(10L)).isEqualTo(users);
    }

    @Test
    void fetchActiveUsersByParentOrgaUnit_delegatesToApi() {
        List<InternalUserDTO> users = List.of(mock(InternalUserDTO.class));
        when(internalUserControllerApi.getEnabledUsersByParentOrgaUnit(20L)).thenReturn(users);

        assertThat(service.fetchActiveUsersByParentOrgaUnit(20L)).isEqualTo(users);
    }
}
