package com.bnpparibas.irb.qlickflow.wfdtrp.entities;
// ⚠️ TODO : ce fichier touche plusieurs packages (entities, entities.user, bpm, dto).
// Le placer dans le package qui minimise les imports, ou le scinder si l'IDE le préfère.

import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask;
import com.bnpparibas.irb.qlickflow.wfdtrp.dto.ApiResponse;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.Devise;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.MotifDerogation;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.PackageCommission;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.referentiel.Segment;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfAuthenticationToken;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntitiesAndEnumsTest {

    // ================= DerogationRequest =================

    @Test
    void derogationRequest_getSetDerogTarifStatus_roundTrip() {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        derog.setDerogTarifStatus(DerogationStatus.SUBMITTED);

        assertThat(derog.getStatus()).isEqualTo("SUBMITTED");
        assertThat(derog.getDerogTarifStatus()).isEqualTo(DerogationStatus.SUBMITTED);
        assertThat(derog.getClosedDate()).isNull(); // statut non final
    }

    @Test
    void derogationRequest_setFinalStatus_setsClosedDate() {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        derog.setDerogTarifStatus(DerogationStatus.APPROVED);

        assertThat(derog.getClosedDate()).isNotNull(); // branche finalStatuses
    }

    @Test
    void derogationRequest_addCommentaire_nullList_initializesAndLinks() {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        derog.setCommentaires(null);
        CommentaireEntity c = CommentaireEntity.builder().id(UUID.randomUUID()).text("x").build();

        derog.addCommentaire(c);

        assertThat(derog.getCommentaires()).containsExactly(c);
        assertThat(c.getDerogationRequest()).isEqualTo(derog);
    }

    @Test
    void derogationRequest_addCommentaire_existingList_appends() {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        CommentaireEntity c1 = CommentaireEntity.builder().id(UUID.randomUUID()).text("c1").build();
        CommentaireEntity c2 = CommentaireEntity.builder().id(UUID.randomUUID()).text("c2").build();
        derog.addCommentaire(c1);
        derog.addCommentaire(c2);

        assertThat(derog.getCommentaires()).containsExactly(c1, c2);
    }

    // ================= WfInstance (via DerogationRequest) =================

    @Test
    void wfInstance_addTask_setsLatestTaskAndBackRef() {
        DerogationRequest derog = DerogationRequest.builder().businessKey("BK-001").build();
        WfTask task = WfTask.builder().createdAt(OffsetDateTime.now()).build();

        derog.addTask(task);

        assertThat(derog.getTasks()).contains(task);
        assertThat(derog.getLatestTask()).isEqualTo(task);
        assertThat(task.getWfInstance()).isEqualTo(derog);
    }

    @Test
    void wfInstance_equals_basedOnId() {
        UUID id = UUID.randomUUID();
        DerogationRequest a = DerogationRequest.builder().id(id).businessKey("BK-A").build();
        DerogationRequest b = DerogationRequest.builder().id(id).businessKey("BK-B").build();
        DerogationRequest c = DerogationRequest.builder().id(UUID.randomUUID()).businessKey("BK-C").build();

        assertThat(a).isEqualTo(b);
        assertThat(a).isNotEqualTo(c);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    // ================= CommentaireEntity =================

    @Test
    void commentaireEntity_prePersist_setsCreatedAt() {
        CommentaireEntity c = CommentaireEntity.builder().id(UUID.randomUUID()).text("x").build();

        c.prePersist();

        assertThat(c.getCreatedAt()).isNotNull();
    }

    @Test
    void commentaireEntity_equals_basedOnId() {
        UUID id = UUID.randomUUID();
        CommentaireEntity a = CommentaireEntity.builder().id(id).text("a").build();
        CommentaireEntity b = CommentaireEntity.builder().id(id).text("b").build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    // ================= UserEntity =================

    @Test
    void userEntity_getFullName_concatenatesNames() {
        UserEntity u = UserEntity.builder()
                .uid("u1").email("u1@bnpparibas.com")
                .firstName("Ahmed").lastName("El Amrani")
                .build();

        assertThat(u.getFullName()).isEqualTo("Ahmed El Amrani");
    }

    @Test
    void userEntity_equals_onlyOnUid() {
        UserEntity a = UserEntity.builder().uid("u1").email("a@x.com").firstName("A").lastName("X").build();
        UserEntity b = UserEntity.builder().uid("u1").email("b@y.com").firstName("B").lastName("Y").build();

        assertThat(a).isEqualTo(b); // @EqualsAndHashCode(onlyExplicitlyIncluded=true) sur uid
    }

    // ================= DerogationStatus =================

    @Test
    void derogationStatus_finalStatuses_containsExpected() {
        assertThat(DerogationStatus.finalStatuses())
                .containsExactlyInAnyOrder(
                        DerogationStatus.CLOSED, DerogationStatus.APPROVED,
                        DerogationStatus.REJECTED, DerogationStatus.CANCELLED);
    }

    // ================= ProfileEnum =================

    @Test
    void profileEnum_exists_trueForKnownCode() {
        assertThat(ProfileEnum.exists("DA")).isTrue();
        assertThat(ProfileEnum.exists("CONSEILLER")).isTrue();
    }

    @Test
    void profileEnum_exists_falseForUnknownCode() {
        assertThat(ProfileEnum.exists("INCONNU")).isFalse();
    }

    @Test
    void profileEnum_gettersReturnCodeAndDesc() {
        assertThat(ProfileEnum.DA.getCode()).isEqualTo("DA");
        assertThat(ProfileEnum.DA.getDesc()).isEqualTo("DIRECTEUR D'AGENCE");
    }

    // ================= Référentiels (constructeurs → actif=true) =================

    @Test
    void devise_twoArgConstructor_setsActifTrue() {
        Devise d = new Devise("MAD", "Dirham marocain");
        assertThat(d.getCode()).isEqualTo("MAD");
        assertThat(d.getLibelle()).isEqualTo("Dirham marocain");
        assertThat(d.getActif()).isTrue();
    }

    @Test
    void motifDerogation_constructor_setsActifTrue() {
        assertThat(new MotifDerogation("M1", "Motif").getActif()).isTrue();
    }

    @Test
    void segment_constructor_setsActifTrue() {
        assertThat(new Segment("GRP", "Grand public").getActif()).isTrue();
    }

    @Test
    void packageCommission_constructor_setsActifTrueAndCodes() {
        PackageCommission p = new PackageCommission("PACK1", "Package 1", List.of("C1", "C2"));
        assertThat(p.getActif()).isTrue();
        assertThat(p.getCodesCommission()).containsExactly("C1", "C2");
    }

    // ================= ApiResponse (5 factories) =================

    @Test
    void apiResponse_factories() {
        ApiResponse<String> s1 = ApiResponse.success("data");
        assertThat(s1.isSuccess()).isTrue();
        assertThat(s1.getData()).isEqualTo("data");

        ApiResponse<Object> s2 = ApiResponse.success();
        assertThat(s2.isSuccess()).isTrue();

        ApiResponse<String> s3 = ApiResponse.success("data", "message ok");
        assertThat(s3.getMessage()).isEqualTo("message ok");
        assertThat(s3.getData()).isEqualTo("data");

        ApiResponse<Object> e1 = ApiResponse.error("erreur");
        assertThat(e1.isSuccess()).isFalse();
        assertThat(e1.getError()).isEqualTo("erreur");

        ApiResponse<Object> e2 = ApiResponse.error("err", "msg");
        assertThat(e2.getError()).isEqualTo("err");
        assertThat(e2.getMessage()).isEqualTo("msg");
    }

    // ================= QfAuthenticationToken =================

    @Test
    void qfAuthenticationToken_principalProvided_returnsPrincipal() {
        Object principal = new Object();
        QfAuthenticationToken token = new QfAuthenticationToken(
                "u1", principal, List.of(new SimpleGrantedAuthority("ROLE_DA")));

        assertThat(token.getUid()).isEqualTo("u1");
        assertThat(token.getPrincipal()).isEqualTo(principal);
        assertThat(token.getCredentials()).isNull();
        assertThat(token.isAuthenticated()).isTrue();
    }

    @Test
    void qfAuthenticationToken_nullPrincipal_returnsUid() {
        QfAuthenticationToken token = new QfAuthenticationToken("u1", null, List.of());

        assertThat(token.getPrincipal()).isEqualTo("u1"); // branche principal != null ? principal : uid
    }
}
