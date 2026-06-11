package com.bnpparibas.irb.qlickflow.wfdtrp.controller;
// ⚠️ TODO : ajuster ce package selon l'emplacement réel de DerogationRequestController.

import com.bnpparibas.irb.qlickflow.wfdtrp.controller.exception.NotFoundException;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.ApiResponse;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.ClientSabDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CreateDerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.DerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.FilterRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.RetourAchargeValidationDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.SendDemandeComplementsDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.SubmitReaffetationDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.UpdateDerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.UpdateStatusRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.UserReaffectationDto;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationStatus;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.CoreBankingFacade;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.DerogationAssignmentFacade;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.DerogationEditionFacade;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.DerogationQueryFacade;
import com.bnpparibas.irb.qlickflow.wfdtrp.facade.DerogationWorkflowFacade;
// TODO : ajuster les imports facade/dto selon les packages réels

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DerogationRequestControllerTest {

    @Mock
    private CoreBankingFacade coreBankingFacade;
    @Mock
    private DerogationWorkflowFacade derogationWorkflowFacade;
    @Mock
    private DerogationQueryFacade derogationQueryFacade;
    @Mock
    private DerogationAssignmentFacade derogationAssignmentFacade;
    @Mock
    private DerogationEditionFacade derogationEditionFacade;

    @InjectMocks
    private DerogationRequestController controller;

    // ------------------------------------------------------------------
    // GET /sabId (2 branches : client trouvé / null → NotFoundException)
    // ------------------------------------------------------------------

    @Test
    void getClientBySabId_found_returnsClient() {
        ClientSabDTO client = ClientSabDTO.builder().subId("0406102").firstName("Ahmed").build();
        when(coreBankingFacade.getClientInfo("0406102")).thenReturn(client);

        assertThat(controller.getClientBySabId("0406102")).isEqualTo(client);
    }

    @Test
    void getClientBySabId_unknown_throwsNotFound() {
        when(coreBankingFacade.getClientInfo("999")).thenReturn(null);

        assertThatThrownBy(() -> controller.getClientBySabId("999"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Aucun client");
    }

    // ------------------------------------------------------------------
    // POST /create
    // ------------------------------------------------------------------

    @Test
    void createDerogationRequest_delegatesToWorkflowFacade() throws Exception {
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder().clientSubId("123").build();
        DerogationRequestDTO expected = DerogationRequestDTO.builder().businessKey("BK-001").build();
        when(derogationWorkflowFacade.submit(dto)).thenReturn(expected);

        assertThat(controller.createDerogationRequest(dto)).isEqualTo(expected);
    }

    // ------------------------------------------------------------------
    // POST /draft (201 + message)
    // ------------------------------------------------------------------

    @Test
    void saveAsDraft_returns201WithMessage() throws Exception {
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder().clientSubId("123").build();

        var response = controller.saveAsDraftDerogationRequest(dto);

        verify(derogationEditionFacade).saveAsDraft(dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).hasToString("{message=Brouillon enregistré avec succès !}");
    }

    // ------------------------------------------------------------------
    // PUT /update
    // ------------------------------------------------------------------

    @Test
    void updateDerogationRequest_returns200WithMessage() throws Exception {
        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder().clientSubId("123").build();

        var response = controller.updateDerogationRequest(dto);

        verify(derogationEditionFacade).save(dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).contains("Demande mise à jour avec succès!");
    }

    // ------------------------------------------------------------------
    // POST /demands-* (3 listes)
    // ------------------------------------------------------------------

    @Test
    void getDemandsToProcess_wrapsInApiResponse() {
        List<DerogationRequestDTO> demands = List.of(DerogationRequestDTO.builder().businessKey("BK-001").build());
        when(derogationQueryFacade.getDerogationsToProcess("abc")).thenReturn(demands);
        FilterRequest filter = new FilterRequest();
        filter.setFilterValue("abc");

        var response = controller.getDemandsToProcess(filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getData()).isEqualTo(demands);
    }

    @Test
    void getDemandsPendingValidation_wrapsInApiResponse() {
        when(derogationQueryFacade.getFollowUpDerogations("x")).thenReturn(List.of());
        FilterRequest filter = new FilterRequest();
        filter.setFilterValue("x");

        var response = controller.getDemandsPendingValidation(filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()).isSuccess()).isTrue();
    }

    @Test
    void getDemandsProcessed_wrapsInApiResponse() {
        when(derogationQueryFacade.getFinalizedDerogations(null)).thenReturn(List.of());
        FilterRequest filter = new FilterRequest();

        var response = controller.getDemandsProcessed(filter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((ApiResponse<?>) response.getBody()).isSuccess()).isTrue();
    }

    // ------------------------------------------------------------------
    // GET /{id}
    // ------------------------------------------------------------------

    @Test
    void getDerogationRequestById_returnsDtoDirectly() {
        UUID id = UUID.randomUUID();
        DerogationRequestDTO dto = DerogationRequestDTO.builder().businessKey("BK-001").build();
        when(derogationQueryFacade.getById(id)).thenReturn(dto);

        var response = controller.getDerogationRequestById(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto); // pas enveloppé dans ApiResponse
    }

    // ------------------------------------------------------------------
    // Endpoints workflow (délégation + message)
    // ------------------------------------------------------------------

    @Test
    void submitValidationDerog_delegatesToApprove() throws Exception {
        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder().build();

        var response = controller.submitValidationDerog(dto);

        verify(derogationWorkflowFacade).approve(dto);
        assertThat(response.getBody().toString()).contains("Demande soumise pour validation avec succès!");
    }

    @Test
    void closeDerogationRejection_delegatesToReject() throws Exception {
        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder().build();

        var response = controller.closeDerogationRejection(dto);

        verify(derogationWorkflowFacade).reject(dto);
        assertThat(response.getBody().toString()).contains("Demande clôturée avec rejet avec succès!");
    }

    @Test
    void updateDerogationStatus_delegatesToEditionFacade() throws Exception {
        UpdateStatusRequestDTO dto = UpdateStatusRequestDTO.builder()
                .id(UUID.randomUUID())
                .status(DerogationStatus.CLOSED)
                .build();

        var response = controller.updateDerogationStatus(dto);

        verify(derogationEditionFacade).updateStatus(dto);
        assertThat(response.getBody().toString()).contains("Statut de la demande mis à jour avec succès!");
    }

    @Test
    void submitDemandeInfo_delegatesToRequestComplements() {
        SendDemandeComplementsDTO dto = SendDemandeComplementsDTO.builder()
                .id(UUID.randomUUID())
                .build();

        var response = controller.submitDemandeInfo(dto);

        verify(derogationWorkflowFacade).requestComplements(dto);
        assertThat(response.getBody().toString()).contains("Demande soumise pour info complémentaires!");
    }

    @Test
    void returnChargeDerogation_delegatesToRetourACharge() throws Exception {
        RetourAchargeValidationDTO dto = RetourAchargeValidationDTO.builder().build();

        var response = controller.returnChargeDerogation(dto);

        verify(derogationWorkflowFacade).retourACharge(dto);
        // message réel avec double espace : "retour charge  avec succès!"
        assertThat(response.getBody().toString()).contains("retour charge");
    }

    // ------------------------------------------------------------------
    // Réaffectation
    // ------------------------------------------------------------------

    @Test
    void reaffecter_delegatesAndReturnsApiSuccess() {
        UUID derogationId = UUID.randomUUID();
        SubmitReaffetationDTO dto = SubmitReaffetationDTO.builder()
                .assignee("u2")
                .derogationId(derogationId)
                .build();

        ApiResponse<?> response = controller.reaffecter(dto);

        verify(derogationAssignmentFacade).reassign(derogationId, "u2");
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void getUsersCandidateToReaffctetion_wrapsCandidates() {
        UUID id = UUID.randomUUID();
        List<UserReaffectationDto> candidates = List.of(
                UserReaffectationDto.builder().uid("u2").firstName("F").lastName("L").build());
        when(derogationAssignmentFacade.getReassignmentCandidates(id)).thenReturn(candidates);

        var response = controller.getUsersCandidateToReaffctetion(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertThat(body.isSuccess()).isTrue();
        assertThat(body.getData()).isEqualTo(candidates);
        assertThat(body.getMessage()).isEqualTo("Reaffectation candidates retrieved successfully");
    }
}
