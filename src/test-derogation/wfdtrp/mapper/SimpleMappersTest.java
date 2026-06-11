package com.bnpparibas.irb.qlickflow.wfdtrp.mapper;
// ⚠️ TODO : ajuster le package selon l'emplacement réel des mappers (mapper confirmé).

import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CommentaireDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CreateDerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.WfTaskDto;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.CommentaireEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
// TODO : ajuster les imports (WfTask/WfTaskDto/WfInstanceDto,
// StartDerggTarifRetWfInstanceCommand)

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleMappersTest {

    private UserEntity user() {
        return UserEntity.builder()
                .uid("u1").email("u1@bnpparibas.com")
                .firstName("Ahmed").lastName("El Amrani")
                .profile(ProfileEnum.CONSEILLER)
                .build();
    }

    // ================= CommentaireMapper =================

    @Test
    void commentaireMapper_null_returnsNull() {
        assertThat(new CommentaireMapper().toDTO(null)).isNull();
    }

    @Test
    void commentaireMapper_maps_authorIsFullName_profileIsDesc() {
        CommentaireMapper mapper = new CommentaireMapper();
        CommentaireEntity entity = CommentaireEntity.builder()
                .id(UUID.randomUUID())
                .text("Mon texte")
                .user(user())
                .createdAt(LocalDateTime.now())
                .build();

        CommentaireDTO dto = mapper.toDTO(entity);

        assertThat(dto.getText()).isEqualTo("Mon texte");
        assertThat(dto.getAuthor()).isEqualTo("Ahmed El Amrani");
        // CommentaireMapper utilise getDesc() (≠ name() de DerogationRequestMapper)
        assertThat(dto.getProfileAuthor()).isEqualTo(ProfileEnum.CONSEILLER.getDesc());
    }

    // ================= CommentairesMapper =================

    @Test
    void commentairesMapper_emptyList_createsNewCommentaire() {
        CommentairesMapper mapper = new CommentairesMapper();
        DerogationRequest request = DerogationRequest.builder()
                .businessKey("BK-001")
                .commentaires(new ArrayList<>())
                .build();
        StartDerggTarifRetWfInstanceCommand command = StartDerggTarifRetWfInstanceCommand.builder()
                .commentaire("Nouveau commentaire")
                .build();

        mapper.mapToEntities(command, request, user());

        assertThat(request.getCommentaires()).hasSize(1);
        assertThat(request.getCommentaires().get(0).getText()).isEqualTo("Nouveau commentaire");
        assertThat(request.getCommentaires().get(0).getUser()).isEqualTo(user());
    }

    @Test
    void commentairesMapper_existingList_updatesFirstCommentaireText() {
        CommentairesMapper mapper = new CommentairesMapper();
        CommentaireEntity existing = CommentaireEntity.builder()
                .id(UUID.randomUUID()).text("Ancien").user(user()).build();
        DerogationRequest request = DerogationRequest.builder()
                .businessKey("BK-001")
                .commentaires(new ArrayList<>(List.of(existing)))
                .build();
        StartDerggTarifRetWfInstanceCommand command = StartDerggTarifRetWfInstanceCommand.builder()
                .commentaire("Texte mis à jour")
                .build();

        mapper.mapToEntities(command, request, user());

        assertThat(existing.getText()).isEqualTo("Texte mis à jour");
    }

    // ================= WfTaskMapper =================

    @Test
    void wfTaskMapper_null_returnsNull() {
        assertThat(new WfTaskMapper().map((WfTask) null)).isNull();
    }

    @Test
    void wfTaskMapper_maps_taskIdAndBusinessKey() {
        WfTaskMapper mapper = new WfTaskMapper();
        DerogationRequest wfInstance = DerogationRequest.builder().businessKey("BK-001").build();
        WfTask task = WfTask.builder()
                .engineTaskId("engine-42")
                .createdAt(OffsetDateTime.now())
                .build();
        task.setWfInstance(wfInstance);

        WfTaskDto dto = mapper.map(task);

        assertThat(dto.getTaskId()).isEqualTo("engine-42");
        assertThat(dto.getWfInstance().getBusinessKey()).isEqualTo("BK-001");
    }

    @Test
    void wfTaskMapper_nullList_returnsNull() {
        assertThat(new WfTaskMapper().map((List<WfTask>) null)).isNull();
    }

    @Test
    void wfTaskMapper_list_mapsEach() {
        WfTaskMapper mapper = new WfTaskMapper();
        DerogationRequest wfInstance = DerogationRequest.builder().businessKey("BK-001").build();
        WfTask task = WfTask.builder().engineTaskId("e1").createdAt(OffsetDateTime.now()).build();
        task.setWfInstance(wfInstance);

        List<WfTaskDto> result = mapper.map(List.of(task));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTaskId()).isEqualTo("e1");
    }

    // ================= StartDerogTarifRetWfCommandMapper =================

    @Test
    void commandMapper_null_returnsNull() {
        assertThat(new StartDerogTarifRetWfCommandMapper().toCommand(null)).isNull();
    }

    @Test
    void commandMapper_mapsFields_commentaireIsJustification() {
        StartDerogTarifRetWfCommandMapper mapper = new StartDerogTarifRetWfCommandMapper();
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder()
                .clientSubId("0406102")
                .accountNumber("1000000023456789")
                .commissionCode("COM1")
                .justification("Ma justification")
                .firstName("Ahmed")
                .lastName("El Amrani")
                .build();

        StartDerggTarifRetWfInstanceCommand command = mapper.toCommand(dto);

        assertThat(command.getClientSubId()).isEqualTo("0406102");
        assertThat(command.getAccountNumber()).isEqualTo("1000000023456789");
        assertThat(command.getCommissionCode()).isEqualTo("COM1");
        // commentaire = justification (mapping spécifique)
        assertThat(command.getCommentaire()).isEqualTo("Ma justification");
        assertThat(command.getFirstName()).isEqualTo("Ahmed");
    }
}
