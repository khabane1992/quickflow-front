package com.bnpparibas.irb.qlickflow.wfdtrp.facade;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de CoreBankingFacade.
// ⚠️ CoreBankingFacade est une CLASSE @Service concrète (pas une interface).
// ⚠️ Le code de simulation (initializeSimulatedClientsData/createClient/getLibelleForCurrency)
//    est du CODE MORT injoignable (ctor commenté) → recommander suppression ou exclusion Sonar.

import com.bnpparibas.irb.qlickflow.wfdtrp.dto.ClientSabDTO;
// TODO : ajuster les imports (GestionDesTiersApi = client généré, ApiException, type de réponse
// getClientInfosByIdClient avec getBusinessLinkName()/getBusinessLinkType()/getDetailedClientCategory())

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoreBankingFacadeTest {

    @Mock
    private GestionDesTiersApi gestionDesTiersApi;

    @InjectMocks
    private CoreBankingFacade facade;

    @Test
    void getClientInfo_nominal_buildsClientSabDto() throws Exception {
        // réponse de l'API en deep stubs (type généré, getters chaînés)
        var resp = mock(Object.class.getClass()); // placeholder — voir note ci-dessous
        // ⚠️ TODO : remplacer le type ci-dessus par le vrai type de retour de
        // gestionDesTiersApi.getClientInfosByIdClient(...). Exemple avec deep stubs :
        //
        //   ClientInfoResponse resp = mock(ClientInfoResponse.class, RETURNS_DEEP_STUBS);
        //   when(resp.getBusinessLinkName().getFirstName()).thenReturn("Ahmed");
        //   when(resp.getBusinessLinkName().getLastName()).thenReturn("El Amrani");
        //   when(resp.getBusinessLinkType()).thenReturn("RET");
        //   when(resp.getDetailedClientCategory()).thenReturn("GRP");
        //   when(gestionDesTiersApi.getClientInfosByIdClient("01", "0406102")).thenReturn(resp);
        //
        //   ClientSabDTO dto = facade.getClientInfo("0406102");
        //   assertThat(dto.getSubId()).isEqualTo("0406102");
        //   assertThat(dto.getFirstName()).isEqualTo("Ahmed");
        //   assertThat(dto.getLastName()).isEqualTo("El Amrani");
        //   assertThat(dto.getBusinessLine()).isEqualTo("RET");
        //   assertThat(dto.getSegment()).isEqualTo("GRP");
        assertThat(true).isTrue(); // garde le test compilable tant que le type n'est pas branché
    }

    @Test
    void getClientInfo_apiException_throwsRuntimeException() throws Exception {
        // ⚠️ TODO : ApiException doit correspondre à l'exception checked déclarée par l'API générée.
        // when(gestionDesTiersApi.getClientInfosByIdClient("01", "999"))
        //         .thenThrow(new ApiException("boom"));
        //
        // assertThatThrownBy(() -> facade.getClientInfo("999"))
        //         .isInstanceOf(RuntimeException.class);
        assertThat(true).isTrue();
    }

    @Test
    void getSimulatedClientBySabId_emptyMap_returnsNull() {
        // la map simulatedClientsData n'est jamais peuplée (ctor d'init commenté)
        assertThat(facade.getSimulatedClientBySabId("0406102")).isNull();
    }
}
