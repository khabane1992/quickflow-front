package com.bnpparibas.irb.qlickflow.wfdtrp.bpm;
// ⚠️ TODO : ajuster ce package selon l'emplacement réel de DerogTarifRetWfInstanceBuilder
// (bpm ou facade probable).

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.CommentaireEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationStatus;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfUserDetails;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.integration.OrgaUnitDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.DerogationRequestRepository;
// TODO : ajuster les imports (OrgaUnitDTO généré, CommentairesMapper, SequentialBusinessKeyGenerator,
// StartDerggTarifRetWfInstanceCommand, WfType, WfTask, WfTaskAssignment)

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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DerogTarifRetWfInstanceBuilderTest {

    @Mock
    private SequentialBusinessKeyGenerator sequentialBusinessKeyGenerator;
    @Mock
    private UserService userService; // TODO : import com.bnpparibas.irb.qlickflow.wfdtrp.service.UserService
    @Mock
    private DerogationRequestRepository derogationRequestRepository;
    @Mock
    private CommentairesMapper commentairesMapper;

    @InjectMocks
    private DerogTarifRetWfInstanceBuilder builder;

    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        currentUser = UserEntity.builder()
                .uid("c1").email("c1@bnpparibas.com")
                .firstName("First").lastName("Last")
                .profile(ProfileEnum.CONSEILLER)
                .build();
        lenient().when(userService.getCurrentUser()).thenReturn(currentUser);

        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        lenient().when(orgaUnit.getNom()).thenReturn("Agence Casablanca Centre");
        QfUserDetails details = QfUserDetails.builder()
                .uid("c1").email("c1@bnpparibas.com")
                .profile(ProfileEnum.CONSEILLER)
                .orgaUnit(orgaUnit)
                .build();
        lenient().when(userService.getCurrentUserDetails()).thenReturn(details);
    }

    private StartDerggTarifRetWfInstanceCommand.StartDerggTarifRetWfInstanceCommandBuilder baseCommand() {
        return StartDerggTarifRetWfInstanceCommand.builder()
                .clientSubId("0406102")
                .accountNumber("1000000023456789")
                .commissionCode("COM1")
                .commentaire("Justification du dossier")
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .businessLine("RET")
                .segment("GRP")
                .motifDerogation("M1")
                .firstName("Ahmed")
                .lastName("El Amrani")
                .packageField("PACK1")
                .tarificationStandard("STD")
                .devise("MAD")
                .tauxMontant(new BigDecimal("10.5"))
                .tauxMontantLabel("10,5 %")
                .convention("non")
                .employeur(null)
                .salaire(new BigDecimal("15000"))
                .pnb(new BigDecimal("1200"))
                .documentIds(Set.of(UUID.randomUUID()));
    }

    // ------------------------------------------------------------------
    // Branche 2 : id == null → création complète
    // ------------------------------------------------------------------

    @Test
    void build_withoutId_createsFullWfInstance() {
        when(sequentialBusinessKeyGenerator.generate(eq(WfType.DER_TAR_RET_PACK), anyString()))
                .thenReturn("DTRP-0001");
        StartDerggTarifRetWfInstanceCommand command = baseCommand().id(null).build();

        DerogationRequest result = builder.build(command);

        // identité workflow
        assertThat(result.getId()).isNotNull();
        assertThat(result.getBusinessKey()).isEqualTo("DTRP-0001");
        assertThat(result.getWfType()).isEqualTo(WfType.DER_TAR_RET_PACK);
        assertThat(result.getStatus()).isEqualTo(DerogationStatus.SUBMITTED.name());

        // champs communs mappés
        assertThat(result.getClientSubId()).isEqualTo("0406102");
        assertThat(result.getAccountNumber()).isEqualTo("1000000023456789");
        assertThat(result.getCommissionCode()).isEqualTo("COM1");
        assertThat(result.getInitiateur()).isEqualTo(currentUser);
        assertThat(result.getAgency()).isEqualTo("Agence Casablanca Centre");
        assertThat(result.getEffectiveDate()).isEqualTo(LocalDate.now().plusDays(1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.now().plusDays(30));
        assertThat(result.getBusinessLine()).isEqualTo("RET");
        assertThat(result.getSegment()).isEqualTo("GRP");
        assertThat(result.getMotifDerogation()).isEqualTo("M1");
        assertThat(result.getFirstName()).isEqualTo("Ahmed");
        assertThat(result.getLastName()).isEqualTo("El Amrani");
        assertThat(result.getPackageField()).isEqualTo("PACK1");
        assertThat(result.getTarificationStandard()).isEqualTo("STD");
        assertThat(result.getDevise()).isEqualTo("MAD");
        assertThat(result.getTauxMontant()).isEqualByComparingTo("10.5");
        assertThat(result.getTauxMontantLabel()).isEqualTo("10,5 %");
        assertThat(result.getSalaire()).isEqualByComparingTo("15000");
        assertThat(result.getPnb()).isEqualByComparingTo("1200");
        assertThat(result.getDocumentIds()).isEqualTo(command.getDocumentIds());

        // tâche d'initiation
        WfTask latestTask = result.getLatestTask();
        assertThat(latestTask).isNotNull();
        assertThat(latestTask.getName()).isEqualTo("Initiation");
        assertThat(latestTask.getStatus()).isEqualTo(WfTask.WfTaskStatus.COMPLETED);
        assertThat(latestTask.getWfInstance()).isEqualTo(result);
        assertThat(result.getTasks()).contains(latestTask);

        WfTaskAssignment assignment = latestTask.getCurrentAssignment();
        assertThat(assignment).isNotNull();
        assertThat(assignment.getAssignee()).isEqualTo(currentUser);
        assertThat(assignment.getAssignedBy()).isEqualTo(currentUser);
        assertThat(assignment.getSyncStatus()).isEqualTo(WfTaskAssignment.SyncStatus.CONFIRMED);
        assertThat(assignment.getTask()).isEqualTo(latestTask);

        // commentaire initial créé avec le texte de la commande
        assertThat(result.getCommentaires())
                .anySatisfy(c -> assertThat(c.getText()).isEqualTo("Justification du dossier"));

        verify(commentairesMapper).mapToEntities(command, result, currentUser);
    }

    // ------------------------------------------------------------------
    // Branche 1 : id != null → reprise d'un brouillon existant
    // ------------------------------------------------------------------

    @Test
    void build_withExistingDraftId_resumesDraft() {
        UUID draftId = UUID.randomUUID();

        CommentaireEntity draftComment = CommentaireEntity.builder()
                .id(UUID.randomUUID())
                .text("Ancien texte du brouillon")
                .user(currentUser)
                .build();
        WfTask draftTask = WfTask.builder()
                .status(WfTask.WfTaskStatus.CREATED)
                .name("Draft")
                .createdAt(OffsetDateTime.now())
                .build();
        DerogationRequest draft = DerogationRequest.builder()
                .businessKey("BK-DRAFT-01")
                .status(DerogationStatus.DRAFT.name())
                .commentaires(new ArrayList<>(List.of(draftComment)))
                .build();
        draft.setLatestTask(draftTask);

        when(derogationRequestRepository.findById(draftId)).thenReturn(Optional.of(draft));

        StartDerggTarifRetWfInstanceCommand command = baseCommand().id(draftId).build();

        DerogationRequest result = builder.build(command);

        // l'entité existante est réutilisée, la businessKey est conservée
        assertThat(result).isSameAs(draft);
        assertThat(result.getBusinessKey()).isEqualTo("BK-DRAFT-01");

        // le commentaire du brouillon est mis à jour avec le texte de la commande
        assertThat(draftComment.getText()).isEqualTo("Justification du dossier");

        // la tâche de brouillon est complétée
        assertThat(draftTask.getStatus()).isEqualTo(WfTask.WfTaskStatus.COMPLETED);
        assertThat(draftTask.getUpdatedAt()).isNotNull();

        // champs communs + statut SUBMITTED appliqués
        assertThat(result.getStatus()).isEqualTo(DerogationStatus.SUBMITTED.name());
        assertThat(result.getInitiateur()).isEqualTo(currentUser);
        assertThat(result.getAgency()).isEqualTo("Agence Casablanca Centre");
        assertThat(result.getClientSubId()).isEqualTo("0406102");

        verify(commentairesMapper).mapToEntities(command, result, currentUser);
    }

    @Test
    void build_withUnknownId_throwsIllegalArgument() {
        UUID unknownId = UUID.randomUUID();
        when(derogationRequestRepository.findById(unknownId)).thenReturn(Optional.empty());

        StartDerggTarifRetWfInstanceCommand command = baseCommand().id(unknownId).build();

        assertThatThrownBy(() -> builder.build(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    // ------------------------------------------------------------------
    // Branche documentIds : remplacement quand l'entité en possède déjà
    // ------------------------------------------------------------------

    @Test
    void build_existingDraftWithDocuments_replacesDocumentIds() {
        UUID draftId = UUID.randomUUID();
        UUID oldDoc = UUID.randomUUID();

        DerogationRequest draft = DerogationRequest.builder()
                .businessKey("BK-DRAFT-02")
                .status(DerogationStatus.DRAFT.name())
                .commentaires(new ArrayList<>(List.of(
                        CommentaireEntity.builder().id(UUID.randomUUID()).text("x").user(currentUser).build())))
                .build();
        draft.setLatestTask(WfTask.builder()
                .status(WfTask.WfTaskStatus.CREATED)
                .createdAt(OffsetDateTime.now())
                .build());
        draft.setDocumentIds(new java.util.LinkedHashSet<>(Set.of(oldDoc)));

        when(derogationRequestRepository.findById(draftId)).thenReturn(Optional.of(draft));

        UUID newDoc = UUID.randomUUID();
        StartDerggTarifRetWfInstanceCommand command = baseCommand()
                .id(draftId)
                .documentIds(Set.of(newDoc))
                .build();

        DerogationRequest result = builder.build(command);

        assertThat(result.getDocumentIds())
                .containsExactly(newDoc)
                .doesNotContain(oldDoc);
    }
}
