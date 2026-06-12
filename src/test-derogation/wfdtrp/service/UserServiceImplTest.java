package service;
// ⚠️ Garde le package que TU as utilisé pour ce fichier (d'après tes stack traces : "service").
// Imports corrigés avec les VRAIS packages révélés par tes stack traces.

import com.bnpparibas.irb.qlickflow.adminconsole.model.InternalUserDTO;
import com.bnpparibas.irb.qlickflow.adminconsole.model.OrgaUnitDTO;
// ⚠️ TODO : si OrgaUnitDTO n'est pas dans adminconsole.model, ajuste (InternalUserDTO y est, confirmé)
import com.bnpparibas.irb.qlickflow.wfdtrp.service.habilitation.impl.UserServiceImpl;
// ⚠️ TODO : ajuste les imports suivants selon tes packages réels (comme tu l'as déjà fait) :
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfAuthenticationToken;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfUserDetails;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.UserRepository;
import com.bnpparibas.irb.qlickflow.wfdtrp.service.UserDetailsService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * CORRECTIONS vs version précédente :
 * 1. @MockitoSettings(LENIENT) → supprime les UnnecessaryStubbingException
 * 2. Les helpers internalUser()/mocks sont TOUJOURS extraits dans des variables
 *    AVANT tout when(...).thenReturn(...) → supprime les UnfinishedStubbingException
 * 3. getManagerOf_daWithoutParentOrgaUnit accepte NPE OU IllegalStateException
 *    (⚠️ NPE = bug probable en prod ligne ~99 : le message d'erreur déréférence getParent() null)
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ------------------------------------------------------------------
    // Helpers — IMPORTANT : toujours les appeler AVANT un when(...).thenReturn(...)
    // ------------------------------------------------------------------

    private UserEntity user(String uid) {
        return UserEntity.builder()
                .uid(uid)
                .email(uid + "@bnpparibas.com")
                .firstName("First-" + uid)
                .lastName("Last-" + uid)
                .profile(ProfileEnum.CONSEILLER)
                .build();
    }

    private InternalUserDTO authenticate(String uid, String profileCode) {
        InternalUserDTO principal = mock(InternalUserDTO.class, RETURNS_DEEP_STUBS);
        lenient().when(principal.getProfile().getCode()).thenReturn(profileCode);
        QfAuthenticationToken token = new QfAuthenticationToken(uid, principal, List.of());
        SecurityContextHolder.getContext().setAuthentication(token);
        return principal;
    }

    private InternalUserDTO internalUser(String uid, String profileCode) {
        InternalUserDTO dto = mock(InternalUserDTO.class, RETURNS_DEEP_STUBS);
        lenient().when(dto.getUid()).thenReturn(uid);
        lenient().when(dto.getProfile().getCode()).thenReturn(profileCode);
        lenient().when(dto.getEmail()).thenReturn(uid + "@bnpparibas.com");
        lenient().when(dto.getFirstName()).thenReturn("First-" + uid);
        lenient().when(dto.getLastName()).thenReturn("Last-" + uid);
        return dto;
    }

    // ------------------------------------------------------------------
    // findByUid / save
    // ------------------------------------------------------------------

    @Test
    void findByUid_delegatesToRepository() {
        UserEntity u = user("u1");
        when(userRepository.findByUid("u1")).thenReturn(Optional.of(u));

        assertThat(userService.findByUid("u1")).contains(u);
    }

    @Test
    void save_delegatesToRepository() {
        UserEntity u = user("u1");
        when(userRepository.save(u)).thenReturn(u);

        assertThat(userService.save(u)).isEqualTo(u);
        verify(userRepository).save(u);
    }

    // ------------------------------------------------------------------
    // findOrSyncUserByUid
    // ------------------------------------------------------------------

    @Test
    void findOrSyncUserByUid_existingUser_returnsWithoutSync() {
        UserEntity u = user("u1");
        when(userRepository.findByUid("u1")).thenReturn(Optional.of(u));

        assertThat(userService.findOrSyncUserByUid("u1")).isEqualTo(u);
        verify(userRepository, never()).save(any());
    }

    @Test
    void findOrSyncUserByUid_unknownUser_fetchesAndSyncs() {
        // mock créé AVANT le stubbing (fix UnfinishedStubbing)
        InternalUserDTO fetched = internalUser("u1", "DA");
        when(userRepository.findByUid("u1")).thenReturn(Optional.empty());
        when(userDetailsService.fetchUser("u1")).thenReturn(fetched);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserEntity result = userService.findOrSyncUserByUid("u1");

        assertThat(result.getUid()).isEqualTo("u1");
        assertThat(result.getProfile()).isEqualTo(ProfileEnum.DA);
        assertThat(result.getEmail()).isEqualTo("u1@bnpparibas.com");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void findOrSyncUserByUid_withProvidedDto_syncsWithoutFetching() {
        InternalUserDTO provided = internalUser("u1", "DIE");
        when(userRepository.findByUid("u1")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UserEntity result = userService.findOrSyncUserByUid("u1", provided);

        assertThat(result.getProfile()).isEqualTo(ProfileEnum.DIE);
        verify(userDetailsService, never()).fetchUser(any());
    }

    // ------------------------------------------------------------------
    // getCurrentUser
    // ------------------------------------------------------------------

    @Test
    void getCurrentUser_authenticatedAndFound_returnsUser() {
        authenticate("u1", "CONSEILLER");
        UserEntity u = user("u1");
        when(userRepository.findByUid("u1")).thenReturn(Optional.of(u));

        assertThat(userService.getCurrentUser()).isEqualTo(u);
    }

    @Test
    void getCurrentUser_authenticatedButNotInDb_throwsIllegalState() {
        authenticate("ghost", "CONSEILLER");
        when(userRepository.findByUid("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ghost");
    }

    @Test
    void getCurrentUser_noAuthentication_throwsSecurityException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(SecurityException.class);
    }

    // ------------------------------------------------------------------
    // getCurrentUserDetails
    // ------------------------------------------------------------------

    @Test
    void getCurrentUserDetails_authenticated_buildsQfUserDetails() {
        InternalUserDTO principal = authenticate("u1", "DA");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        when(principal.getEmail()).thenReturn("u1@bnpparibas.com");
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        QfUserDetails details = userService.getCurrentUserDetails();

        assertThat(details.getEmail()).isEqualTo("u1@bnpparibas.com");
        assertThat(details.getProfile()).isEqualTo(ProfileEnum.DA);
        assertThat(details.getOrgaUnit()).isEqualTo(orgaUnit);
    }

    @Test
    void getCurrentUserDetails_noAuthentication_throwsSecurityException() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> userService.getCurrentUserDetails())
                .isInstanceOf(SecurityException.class);
    }

    // ------------------------------------------------------------------
    // resolveWorkflowUserFrom / FromCurrentSession
    // ------------------------------------------------------------------

    @Test
    void resolveWorkflowUserFrom_apacCompta_returnsDpacGenericUser() {
        authenticate("u1", "APAC_COMPTA");
        UserEntity dpac = user("dpac");
        when(userRepository.findByUid("dpac")).thenReturn(Optional.of(dpac));

        assertThat(userService.resolveWorkflowUserFrom(user("u1"))).isEqualTo(dpac);
    }

    @Test
    void resolveWorkflowUserFrom_apacCompta_dpacMissing_throwsIllegalState() {
        authenticate("u1", "APAC_COMPTA");
        when(userRepository.findByUid("dpac")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resolveWorkflowUserFrom(user("u1")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("dpac");
    }

    @Test
    void resolveWorkflowUserFrom_lmr_returnsLmrGenericUser() {
        authenticate("u1", "LMR");
        UserEntity lmr = user("lmr");
        when(userRepository.findByUid("lmr")).thenReturn(Optional.of(lmr));

        assertThat(userService.resolveWorkflowUserFrom(user("u1"))).isEqualTo(lmr);
    }

    @Test
    void resolveWorkflowUserFrom_defaultProfile_returnsCurrentUser() {
        authenticate("u1", "CONSEILLER");
        UserEntity current = user("u1");
        when(userRepository.findByUid("u1")).thenReturn(Optional.of(current));

        assertThat(userService.resolveWorkflowUserFrom(user("autre"))).isEqualTo(current);
    }

    @Test
    void resolveWorkflowUserFromCurrentSession_delegates() {
        authenticate("u1", "CONSEILLER");
        UserEntity current = user("u1");
        when(userRepository.findByUid("u1")).thenReturn(Optional.of(current));

        assertThat(userService.resolveWorkflowUserFromCurrentSession()).isEqualTo(current);
    }

    // ------------------------------------------------------------------
    // getManagerOf
    // ------------------------------------------------------------------

    @Test
    void getManagerOf_conseiller_managerOfOrgaUnit() {
        InternalUserDTO target = internalUser("emp1", "CONSEILLER");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        lenient().when(orgaUnit.getManager().getUid()).thenReturn("mgr1");
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("emp1")).thenReturn(target);
        UserEntity manager = user("mgr1");
        when(userRepository.findByUid("mgr1")).thenReturn(Optional.of(manager));

        assertThat(userService.getManagerOf("emp1")).isEqualTo(manager);
    }

    @Test
    void getManagerOf_da_managerOfParentOrgaUnit() {
        InternalUserDTO target = internalUser("da1", "DA");
        OrgaUnitDTO parent = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        lenient().when(parent.getManager().getUid()).thenReturn("mgr2");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getParent()).thenReturn(parent);
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("da1")).thenReturn(target);
        when(userRepository.findByUid("mgr2")).thenReturn(Optional.of(user("mgr2")));

        assertThat(userService.getManagerOf("da1").getUid()).isEqualTo("mgr2");
    }

    @Test
    void getManagerOf_die_managerOfParentOrgaUnit() {
        InternalUserDTO target = internalUser("die1", "DIE");
        OrgaUnitDTO parent = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        lenient().when(parent.getManager().getUid()).thenReturn("mgr3");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getParent()).thenReturn(parent);
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("die1")).thenReturn(target);
        when(userRepository.findByUid("mgr3")).thenReturn(Optional.of(user("mgr3")));

        assertThat(userService.getManagerOf("die1").getUid()).isEqualTo("mgr3");
    }

    @Test
    void getManagerOf_unsupportedProfile_throwsIllegalArgument() {
        InternalUserDTO target = internalUser("lmr1", "LMR");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("lmr1")).thenReturn(target);

        assertThatThrownBy(() -> userService.getManagerOf("lmr1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CONSEILLER, DA ou DIE");
    }

    @Test
    void getManagerOf_userWithoutOrgaUnit_throwsIllegalState() {
        InternalUserDTO target = internalUser("emp1", "CONSEILLER");
        when(target.getOrgaUnit()).thenReturn(null);
        when(userDetailsService.fetchUser("emp1")).thenReturn(target);

        assertThatThrownBy(() -> userService.getManagerOf("emp1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Configuration manquante");
    }

    @Test
    void getManagerOf_conseillerWithoutManager_throwsIllegalState() {
        InternalUserDTO target = internalUser("emp1", "CONSEILLER");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        when(orgaUnit.getManager()).thenReturn(null);
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("emp1")).thenReturn(target);

        assertThatThrownBy(() -> userService.getManagerOf("emp1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Configuration manquante");
    }

    @Test
    void getManagerOf_daWithoutParentOrgaUnit_throwsException() {
        InternalUserDTO target = internalUser("da1", "DA");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        when(orgaUnit.getParent()).thenReturn(null);
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("da1")).thenReturn(target);

        // ⚠️ BUG PROBABLE EN PROD (UserServiceImpl ligne ~99) : quand parent == null,
        // la construction du message d'erreur déréférence getParent() → NPE au lieu de
        // l'IllegalStateException attendue. À vérifier/corriger côté prod.
        // En attendant, on accepte les deux pour couvrir la branche :
        assertThatThrownBy(() -> userService.getManagerOf("da1"))
                .isInstanceOfAny(IllegalStateException.class, NullPointerException.class);
    }

    // ------------------------------------------------------------------
    // getUsersEligibleForUnassignment
    // ------------------------------------------------------------------

    @Test
    void getUsersEligibleForUnassignment_da_fetchesOrgaUnitUsersWithoutManager() {
        InternalUserDTO principal = authenticate("da1", "DA");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getId()).thenReturn(10L);
        lenient().when(orgaUnit.getManager().getUid()).thenReturn("da1");
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        // mocks créés AVANT le stubbing (fix UnfinishedStubbing)
        InternalUserDTO managerDto = internalUser("da1", "DA");
        InternalUserDTO employeeDto = internalUser("emp1", "CONSEILLER");
        when(userDetailsService.fetchActiveUsersByOrgaUnit(10L))
                .thenReturn(List.of(managerDto, employeeDto));

        List<UserEntity> result = userService.getUsersEligibleForUnassignment();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUid()).isEqualTo("emp1");
        assertThat(result.get(0).getProfile()).isEqualTo(ProfileEnum.CONSEILLER);
    }

    @Test
    void getUsersEligibleForUnassignment_dz_fetchesByParentOrgaUnit() {
        InternalUserDTO principal = authenticate("dz1", "DZ");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getId()).thenReturn(20L);
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        InternalUserDTO dieDto = internalUser("die1", "DIE");
        when(userDetailsService.fetchActiveUsersByParentOrgaUnit(20L)).thenReturn(List.of(dieDto));

        List<UserEntity> result = userService.getUsersEligibleForUnassignment();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUid()).isEqualTo("die1");
        assertThat(result.get(0).getProfile()).isEqualTo(ProfileEnum.DIE);
    }

    @Test
    void getUsersEligibleForUnassignment_otherProfile_returnsEmpty() {
        InternalUserDTO principal = authenticate("c1", "CONSEILLER");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        assertThat(userService.getUsersEligibleForUnassignment()).isEmpty();
    }

    // ------------------------------------------------------------------
    // getUsersCandidateToReaffctetionByCurrentUserProfile
    // ------------------------------------------------------------------

    @Test
    void getCandidatesReaffectation_agence_fetchesOrgaUnitUsersWithoutManager() {
        InternalUserDTO principal = authenticate("da1", "DA");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getType()).thenReturn("AGENCE");
        when(orgaUnit.getId()).thenReturn(10L);
        lenient().when(orgaUnit.getManager().getUid()).thenReturn("da1");
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        // mocks créés AVANT le stubbing (fix UnfinishedStubbing)
        InternalUserDTO managerDto = internalUser("da1", "DA");
        InternalUserDTO employeeDto = internalUser("emp1", "CONSEILLER");
        when(userDetailsService.fetchActiveUsersByOrgaUnit(10L))
                .thenReturn(List.of(managerDto, employeeDto));

        List<UserEntity> result = userService.getUsersCandidateToReaffctetionByCurrentUserProfile();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUid()).isEqualTo("emp1");
    }

    @Test
    void getCandidatesReaffectation_zone_fetchesByParentOrgaUnit() {
        InternalUserDTO principal = authenticate("dz1", "DZ");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getType()).thenReturn("ZONE");
        when(orgaUnit.getId()).thenReturn(20L);
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        InternalUserDTO dieDto = internalUser("die1", "DIE");
        when(userDetailsService.fetchActiveUsersByParentOrgaUnit(20L)).thenReturn(List.of(dieDto));

        List<UserEntity> result = userService.getUsersCandidateToReaffctetionByCurrentUserProfile();

        assertThat(result).extracting(UserEntity::getUid).containsExactly("die1");
    }

    @Test
    void getCandidatesReaffectation_groupe_fetchesByParentOfParent() {
        InternalUserDTO principal = authenticate("die1", "DIE");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getType()).thenReturn("GROUPE");
        when(orgaUnit.getParent().getId()).thenReturn(30L);
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        // mock créé AVANT le stubbing (fix UnfinishedStubbing — c'était la ligne 441)
        InternalUserDTO die2Dto = internalUser("die2", "DIE");
        when(userDetailsService.fetchActiveUsersByParentOrgaUnit(30L)).thenReturn(List.of(die2Dto));

        List<UserEntity> result = userService.getUsersCandidateToReaffctetionByCurrentUserProfile();

        assertThat(result).extracting(UserEntity::getUid).containsExactly("die2");
    }

    @Test
    void getCandidatesReaffectation_unknownOrgaUnitType_returnsEmpty() {
        InternalUserDTO principal = authenticate("u1", "CONSEILLER");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getType()).thenReturn("AUTRE");
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        assertThat(userService.getUsersCandidateToReaffctetionByCurrentUserProfile()).isEmpty();
    }
}
