package com.bnpparibas.irb.qlickflow.wfdtrp.mapper;
// ⚠️ TODO : DerogationRequestMapper est dans le package mapper (confirmé) — ajuster si besoin.

import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask;
import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTaskAssignment;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.CreateDerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.DerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.SendDemandeComplementsDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.UpdateDerogationRequestDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.RetourAchargeValidationDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.CommentaireEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationStatus;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.MotifDerogation;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfUserDetails;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.integration.OrgaUnitDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.MotifDerogationService;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.UserService;
// TODO : ajuster les imports selon les packages réels (WfTask/WfTaskAssignment/WfType,
// OrgaUnitDTO généré, SequentialBusinessKeyGenerator, UserDetailsService)

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DerogationRequestMapperTest {

    @Mock
    private MotifDerogationService motifDerogationService;
    @Mock
    private UserService userService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private SequentialBusinessKeyGenerator sequentialBusinessKeyGenerator;

    @InjectMocks
    private DerogationRequestMapper mapper;

    private UserEntity initiateur;
    private UserEntity responsable;

    @BeforeEach
    void setUp() {
        initiateur = user("init1", ProfileEnum.CONSEILLER);
        responsable = user("resp1", ProfileEnum.DA);
        lenient().when(motifDerogationService.findByCode("M1"))
                .thenReturn(Optional.of(new MotifDerogation("M1", "Libellé motif M1")));
        lenient().when(motifDerogationService.findByCode("INCONNU"))
                .thenReturn(Optional.empty());
    }

    private UserEntity user(String uid, ProfileEnum profile) {
        return UserEntity.builder()
                .uid(uid).email(uid + "@bnpparibas.com")
                .firstName("First-" + uid).lastName("Last-" + uid)
                .profile(profile)
                .build();
    }

    /** Entité complète : initiateur, commentaire, latestTask (id 5) assignée à `responsable`. */
    private DerogationRequest fullEntity() {
        CommentaireEntity commentaire = CommentaireEntity.builder()
                .id(UUID.randomUUID())
                .text("Premier commentaire")
                .user(initiateur)
                .build();

        WfTaskAssignment assignment = WfTaskAssignment.builder()
                .assignee(responsable)
                .assignedBy(initiateur)
                .build();
        WfTask task = WfTask.builder()
                .id(5L)
                .status(WfTask.WfTaskStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .currentAssignment(assignment)
                .build();

        DerogationRequest entity = DerogationRequest.builder()
                .businessKey("BK-001")
                .status(DerogationStatus.VALIDATION_AGENCE.name())
                .clientSubId("0406102")
                .accountNumber("1000000023456789")
                .commissionCode("COM1")
                .agency("Agence Casa")
                .firstName("Ahmed")
                .lastName("El Amrani")
                .motifDerogation("M1")
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .initiateur(initiateur)
                .commentaires(new ArrayList<>(List.of(commentaire)))
                .build();
        entity.addTask(task); // pose tasks + latestTask + back-ref
        return entity;
    }

    // ------------------------------------------------------------------
    // toDTO(entity, currentUser)
    // ------------------------------------------------------------------

    @Test
    void toDTO_nullEntity_returnsNull() {
        assertThat(mapper.toDTO(null, user("u1", ProfileEnum.CONSEILLER))).isNull();
    }

    @Test
    void toDTO_fullEntity_mapsAllFields() {
        DerogationRequest entity = fullEntity();

        DerogationRequestDTO dto = mapper.toDTO(entity, responsable);

        assertThat(dto.getBusinessKey()).isEqualTo("BK-001");
        assertThat(dto.getClientSubId()).isEqualTo("0406102");
        assertThat(dto.getClientName()).isEqualTo("Ahmed El Amrani");
        assertThat(dto.getOwner()).isEqualTo("First-init1 Last-init1");
        assertThat(dto.getResponsible()).isEqualTo("First-resp1 Last-resp1");
        assertThat(dto.getAgency()).isEqualTo("Agence Casa");
        assertThat(dto.getMotifDerogation()).isEqualTo("Libellé motif M1");
        assertThat(dto.getStatus()).isEqualTo(DerogationStatus.VALIDATION_AGENCE);
        assertThat(dto.getReceptionDate()).isEqualTo(LocalDate.now()); // createdAt de latestTask
        assertThat(dto.getTaskId()).isEqualTo(5L);
        assertThat(dto.getCommentaires()).hasSize(1);
        assertThat(dto.getCommentaires().get(0).getText()).isEqualTo("Premier commentaire");
        assertThat(dto.getCommentaires().get(0).getAuthor()).isEqualTo("First-init1 Last-init1");
        // profileAuthor = profile.name() dans CE mapper (≠ getDesc() de CommentaireMapper)
        assertThat(dto.getCommentaires().get(0).getProfileAuthor()).isEqualTo("CONSEILLER");
    }

    @Test
    void toDTO_entityFirstNameNull_clientNameHandlesNull() {
        DerogationRequest entity = fullEntity();
        entity.setFirstName(null);

        DerogationRequestDTO dto = mapper.toDTO(entity, responsable);

        assertThat(dto.getClientName()).isEqualTo(" El Amrani"); // ternaire null-safe
    }

    @Test
    void toDTO_unknownMotifCode_labelInconnu() {
        DerogationRequest entity = fullEntity();
        entity.setMotifDerogation("INCONNU");

        DerogationRequestDTO dto = mapper.toDTO(entity, responsable);

        assertThat(dto.getMotifDerogation()).isEqualTo("Inconnu"); // branche orElse
    }

    // ------------------------------------------------------------------
    // processEnabled — isDerogationATraiter (4 branches)
    // ------------------------------------------------------------------
    // ⚠️ TODO : si processEnabled/assignEnabled sont des Boolean (et non boolean),
    // remplacer isProcessEnabled()/isAssignEnabled() par getProcessEnabled()/getAssignEnabled().

    @Test
    void toDTO_currentUserDz_processDisabled_assignEnabled() {
        DerogationRequestDTO dto = mapper.toDTO(fullEntity(), user("dz1", ProfileEnum.DZ));

        assertThat(dto.isProcessEnabled()).isFalse(); // DZ → jamais à traiter
        assertThat(dto.isAssignEnabled()).isTrue();   // DZ → toujours réaffectable
    }

    @Test
    void toDTO_currentUserLmr_processEnabled() {
        DerogationRequestDTO dto = mapper.toDTO(fullEntity(), user("lmr1", ProfileEnum.LMR));

        assertThat(dto.isProcessEnabled()).isTrue();
        assertThat(dto.isAssignEnabled()).isFalse(); // default du switch réaffectation
    }

    @Test
    void toDTO_currentUserApacCompta_processEnabled() {
        DerogationRequestDTO dto = mapper.toDTO(fullEntity(), user("apac1", ProfileEnum.APAC_COMPTA));

        assertThat(dto.isProcessEnabled()).isTrue();
    }

    @Test
    void toDTO_currentUserIsAssignee_processEnabled() {
        // responsable (resp1, DA) est l'assignee de la latestTask
        DerogationRequestDTO dto = mapper.toDTO(fullEntity(), responsable);

        assertThat(dto.isProcessEnabled()).isTrue();   // uid == assignee
        assertThat(dto.isAssignEnabled()).isFalse();   // DA assigné → PAS réaffectable par lui
    }

    @Test
    void toDTO_currentUserDaNotAssignee_assignEnabled() {
        DerogationRequestDTO dto = mapper.toDTO(fullEntity(), user("autreDa", ProfileEnum.DA));

        assertThat(dto.isProcessEnabled()).isFalse();  // pas l'assignee
        assertThat(dto.isAssignEnabled()).isTrue();    // DA non assigné → réaffectable
    }

    @Test
    void toDTO_currentUserDieAssignee_assignEnabled() {
        // remplace l'assignee par un DIE et interroge avec le même uid
        DerogationRequest entity = fullEntity();
        UserEntity die = user("die1", ProfileEnum.DIE);
        entity.getLatestTask().getCurrentAssignment().setAssignee(die);

        DerogationRequestDTO dto = mapper.toDTO(entity, die);

        assertThat(dto.isAssignEnabled()).isTrue();    // DIE assigné → réaffectable
    }

    @Test
    void toDTO_currentUserDieNotAssignee_assignDisabled() {
        DerogationRequestDTO dto = mapper.toDTO(fullEntity(), user("die2", ProfileEnum.DIE));

        assertThat(dto.isAssignEnabled()).isFalse();
    }

    // ------------------------------------------------------------------
    // toDTO(entity) / toDTO(List)
    // ------------------------------------------------------------------

    @Test
    void toDTO_singleArg_usesCurrentUser() {
        when(userService.getCurrentUser()).thenReturn(responsable);

        DerogationRequestDTO dto = mapper.toDTO(fullEntity());

        assertThat(dto.getBusinessKey()).isEqualTo("BK-001");
    }

    @Test
    void toDTO_list_mapsEachEntity() {
        when(userService.getCurrentUser()).thenReturn(responsable);

        List<DerogationRequestDTO> dtos = mapper.toDTO(List.of(fullEntity(), fullEntity()));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getBusinessKey()).isEqualTo("BK-001");
    }

    // ------------------------------------------------------------------
    // toDraftEntity
    // ------------------------------------------------------------------

    @Test
    void toDraftEntity_null_returnsNull() {
        assertThat(mapper.toDraftEntity(null)).isNull();
    }

    @Test
    void toDraftEntity_buildsDraftWithTaskAndJustification() {
        when(userService.getCurrentUser()).thenReturn(initiateur);
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        when(orgaUnit.getNom()).thenReturn("Agence Casa");
        when(userService.getCurrentUserDetails()).thenReturn(QfUserDetails.builder()
                .uid("init1").profile(ProfileEnum.CONSEILLER).orgaUnit(orgaUnit).build());
        when(sequentialBusinessKeyGenerator.generate(eq(WfType.DER_TAR_RET_PACK), anyString()))
                .thenReturn("DTRP-0002");

        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder()
                .clientSubId("0406102")
                .accountNumber("1000000023456789")
                .justification("Ma justification")
                .receptionDate(LocalDate.now())
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .firstName("Ahmed")
                .lastName("El Amrani")
                .documentIds(Set.of(UUID.randomUUID()))
                .build();

        DerogationRequest draft = mapper.toDraftEntity(dto);

        assertThat(draft.getStatus()).isEqualTo(DerogationStatus.DRAFT.name());
        assertThat(draft.getBusinessKey()).isEqualTo("DTRP-0002");
        assertThat(draft.getInitiateur()).isEqualTo(initiateur);
        assertThat(draft.getAgency()).isEqualTo("Agence Casa");
        assertThat(draft.getReceptionDate()).isEqualTo(LocalDate.now()); // ici = dto.receptionDate (pas endDate)

        WfTask draftTask = draft.getLatestTask();
        assertThat(draftTask).isNotNull();
        assertThat(draftTask.getName()).isEqualTo("Draft");
        assertThat(draftTask.getStatus()).isEqualTo(WfTask.WfTaskStatus.CREATED);
        assertThat(draftTask.getCurrentAssignment().getAssignee()).isEqualTo(initiateur);
        assertThat(draftTask.getCurrentAssignment().getSyncStatus())
                .isEqualTo(WfTaskAssignment.SyncStatus.CONFIRMED);

        assertThat(draft.getCommentaires())
                .anySatisfy(c -> assertThat(c.getText()).isEqualTo("Ma justification"));
    }

    // ------------------------------------------------------------------
    // updateEntityFromDTO(CreateDerogationRequestDTO, entity)
    // ------------------------------------------------------------------

    @Test
    void updateFromCreateDto_null_doesNothing() {
        DerogationRequest entity = fullEntity();
        String before = entity.getClientSubId();

        mapper.updateEntityFromDTO((CreateDerogationRequestDTO) null, entity);

        assertThat(entity.getClientSubId()).isEqualTo(before);
    }

    @Test
    void updateFromCreateDto_setsInitiateurAndCopiesEndDateIntoReceptionDate() {
        UserEntity newCurrentUser = user("nouveau", ProfileEnum.DA);
        when(userService.getCurrentUser()).thenReturn(newCurrentUser);
        DerogationRequest entity = fullEntity();

        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder()
                .clientSubId("NEW-SUB")
                .endDate(LocalDate.now().plusDays(99))
                .justification("Nouvelle justification")
                .documentIds(Set.of(UUID.randomUUID()))
                .build();

        mapper.updateEntityFromDTO(dto, entity);

        assertThat(entity.getInitiateur()).isEqualTo(newCurrentUser); // ⚠️ Create remplace l'initiateur
        assertThat(entity.getClientSubId()).isEqualTo("NEW-SUB");
        // ⚠️ comportement actuel (bug connu) : receptionDate = dto.endDate
        assertThat(entity.getReceptionDate()).isEqualTo(LocalDate.now().plusDays(99));
        assertThat(entity.getDocumentIds()).isEqualTo(dto.getDocumentIds());
        // justification non blanche → l'ancien commentaire est remplacé par le nouveau
        assertThat(entity.getCommentaires()).hasSize(1);
        assertThat(entity.getCommentaires().get(0).getText()).isEqualTo("Nouvelle justification");
    }

    @Test
    void updateFromCreateDto_blankJustification_keepsExistingCommentaires() {
        when(userService.getCurrentUser()).thenReturn(initiateur);
        DerogationRequest entity = fullEntity();

        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder()
                .justification("   ")
                .build();

        mapper.updateEntityFromDTO(dto, entity);

        assertThat(entity.getCommentaires()).hasSize(1);
        assertThat(entity.getCommentaires().get(0).getText()).isEqualTo("Premier commentaire");
    }

    // ------------------------------------------------------------------
    // updateEntityFromDTO(UpdateDerogationRequestDTO, entity) — PAS de setInitiateur
    // ------------------------------------------------------------------

    @Test
    void updateFromUpdateDto_doesNotChangeInitiateur() {
        when(userService.getCurrentUser()).thenReturn(user("autre", ProfileEnum.DA));
        DerogationRequest entity = fullEntity();

        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder()
                .clientSubId("UPD-SUB")
                .endDate(LocalDate.now().plusDays(45))
                .documentIds(Set.of(UUID.randomUUID()))
                .build();

        mapper.updateEntityFromDTO(dto, entity);

        assertThat(entity.getInitiateur()).isEqualTo(initiateur); // inchangé
        assertThat(entity.getClientSubId()).isEqualTo("UPD-SUB");
        assertThat(entity.getReceptionDate()).isEqualTo(LocalDate.now().plusDays(45)); // = endDate (bug connu)
    }

    // ------------------------------------------------------------------
    // updateEntityFromDTO(SendDemandeComplementsDTO, entity) — documents uniquement
    // ------------------------------------------------------------------

    @Test
    void updateFromComplementsDto_onlyReplacesDocuments() {
        DerogationRequest entity = fullEntity();
        entity.setDocumentIds(new LinkedHashSet<>(Set.of(UUID.randomUUID())));
        String subIdBefore = entity.getClientSubId();

        UUID newDoc = UUID.randomUUID();
        SendDemandeComplementsDTO dto = SendDemandeComplementsDTO.builder()
                .id(UUID.randomUUID())
                .documentIds(Set.of(newDoc))
                .build();

        mapper.updateEntityFromDTO(dto, entity);

        assertThat(entity.getDocumentIds()).containsExactly(newDoc);
        assertThat(entity.getClientSubId()).isEqualTo(subIdBefore);
    }

    @Test
    void updateFromComplementsDto_entityWithNullDocuments_createsSet() {
        DerogationRequest entity = fullEntity();
        entity.setDocumentIds(null); // branche "sinon setDocumentIds(new LinkedHashSet)"

        UUID newDoc = UUID.randomUUID();
        SendDemandeComplementsDTO dto = SendDemandeComplementsDTO.builder()
                .id(UUID.randomUUID())
                .documentIds(Set.of(newDoc))
                .build();

        mapper.updateEntityFromDTO(dto, entity);

        assertThat(entity.getDocumentIds()).containsExactly(newDoc);
    }

    // ------------------------------------------------------------------
    // updateEntityFromRetourAchargeValidationDTO (20 sets, pas d'initiateur)
    // ------------------------------------------------------------------

    @Test
    void updateFromRetourAcharge_null_doesNothing() {
        DerogationRequest entity = fullEntity();
        String before = entity.getClientSubId();

        mapper.updateEntityFromRetourAchargeValidationDTO(null, entity);

        assertThat(entity.getClientSubId()).isEqualTo(before);
    }

    @Test
    void updateFromRetourAcharge_copiesAllFields() {
        DerogationRequest entity = fullEntity();

        RetourAchargeValidationDTO dto = RetourAchargeValidationDTO.builder()
                .clientSubId("RAC-SUB")
                .accountNumber("999")
                .commissionCode("COM-RAC")
                .effectiveDate(LocalDate.now().plusDays(2))
                .endDate(LocalDate.now().plusDays(20))
                .businessLine("RET")
                .segment("PRE")
                .motifDerogation("M2")
                .firstName("Fatima")
                .lastName("Zahra")
                .packageField("PACK2")
                .tarificationStandard("STD2")
                .devise("EUR")
                .tauxMontant(new BigDecimal("7.5"))
                .tauxMontantLabel("7,5 %")
                .convention("oui")
                .employeur("BNP")
                .salaire(new BigDecimal("20000"))
                .pnb(new BigDecimal("800"))
                .documentIds(Set.of(UUID.randomUUID()))
                .build();

        mapper.updateEntityFromRetourAchargeValidationDTO(dto, entity);

        assertThat(entity.getClientSubId()).isEqualTo("RAC-SUB");
        assertThat(entity.getAccountNumber()).isEqualTo("999");
        assertThat(entity.getCommissionCode()).isEqualTo("COM-RAC");
        assertThat(entity.getReceptionDate()).isEqualTo(LocalDate.now().plusDays(20)); // = endDate (bug connu)
        assertThat(entity.getSegment()).isEqualTo("PRE");
        assertThat(entity.getFirstName()).isEqualTo("Fatima");
        assertThat(entity.getEmployeur()).isEqualTo("BNP");
        assertThat(entity.getSalaire()).isEqualByComparingTo("20000");
        assertThat(entity.getDocumentIds()).isEqualTo(dto.getDocumentIds());
        assertThat(entity.getInitiateur()).isEqualTo(initiateur); // inchangé
    }

    // ------------------------------------------------------------------
    // updateEntityFromLmrValidationDTO (5 sets seulement)
    // ------------------------------------------------------------------

    @Test
    void updateFromLmrValidation_null_doesNothing() {
        DerogationRequest entity = fullEntity();
        mapper.updateEntityFromLmrValidationDTO(null, entity);
        assertThat(entity.getClientSubId()).isEqualTo("0406102");
    }

    @Test
    void updateFromLmrValidation_copiesOnlyFiveFields() {
        DerogationRequest entity = fullEntity();
        entity.setTauxMontant(new BigDecimal("10"));

        UpdateDerogationRequestDTO dto = UpdateDerogationRequestDTO.builder()
                .clientSubId("DOIT-PAS-CHANGER")
                .effectiveDate(LocalDate.now().plusDays(3))
                .endDate(LocalDate.now().plusDays(33))
                .tauxMontant(new BigDecimal("12.5"))
                .tauxMontantLabel("12,5 %")
                .build();

        mapper.updateEntityFromLmrValidationDTO(dto, entity);

        assertThat(entity.getEffectiveDate()).isEqualTo(LocalDate.now().plusDays(3));
        assertThat(entity.getEndDate()).isEqualTo(LocalDate.now().plusDays(33));
        assertThat(entity.getReceptionDate()).isEqualTo(LocalDate.now().plusDays(33)); // = endDate
        assertThat(entity.getTauxMontant()).isEqualByComparingTo("12.5");
        assertThat(entity.getTauxMontantLabel()).isEqualTo("12,5 %");
        // tout le reste inchangé
        assertThat(entity.getClientSubId()).isEqualTo("0406102");
    }
}
