package com.bnpparibas.irb.qlickflow.wfdtrp.service;
// ⚠️ TODO : ajuster ce package pour qu'il corresponde EXACTEMENT au package de UserServiceImpl
// (service ou service.impl).

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.ProfileEnum;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfAuthenticationToken;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.QfUserDetails;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.UserEntity;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.UserRepository;
// TODO : ajuster les imports selon les packages réels :
// - QfAuthenticationToken : entities.user ou config/security
// - InternalUserDTO / OrgaUnitDTO : classes générées (package integration ou client généré)
import com.bnpparibas.irb.qlickflow.wfdtrp.integration.InternalUserDTO;
import com.bnpparibas.irb.qlickflow.wfdtrp.integration.OrgaUnitDTO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Les DTOs générés (InternalUserDTO, OrgaUnitDTO) sont mockés en RETURNS_DEEP_STUBS
 * pour ne pas dépendre des types intermédiaires (ex: getProfile().getCode()).
 * QfAuthenticationToken est instancié RÉELLEMENT et posé dans le SecurityContextHolder.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailsService userDetailsService; // TODO : import selon package (facade ou service)

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ------------------------------------------------------------------
    // Helpers
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

    /** Pose un QfAuthenticationToken réel (uid + principal InternalUserDTO mocké) dans le contexte. */
    private InternalUserDTO authenticate(String uid, String profileCode) {
        InternalUserDTO principal = mock(InternalUserDTO.class, RETURNS_DEEP_STUBS);
        when(principal.getProfile().getCode()).thenReturn(profileCode);
        QfAuthenticationToken token = new QfAuthenticationToken(uid, principal, List.of());
        SecurityContextHolder.getContext().setAuthentication(token);
        return principal;
    }

    private InternalUserDTO internalUser(String uid, String profileCode) {
        InternalUserDTO dto = mock(InternalUserDTO.class, RETURNS_DEEP_STUBS);
        when(dto.getUid()).thenReturn(uid);
        when(dto.getProfile().getCode()).thenReturn(profileCode);
        when(dto.getEmail()).thenReturn(uid + "@bnpparibas.com");
        when(dto.getFirstName()).thenReturn("First-" + uid);
        when(dto.getLastName()).thenReturn("Last-" + uid);
        return dto;
    }

    // ------------------------------------------------------------------
    // findByUid / save (délégation)
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
        verify(userRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void findOrSyncUserByUid_unknownUser_fetchesAndSyncs() {
        when(userRepository.findByUid("u1")).thenReturn(Optional.empty());
        when(userDetailsService.fetchUser("u1")).thenReturn(internalUser("u1", "DA"));
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserEntity result = userService.findOrSyncUserByUid("u1");

        assertThat(result.getUid()).isEqualTo("u1");
        assertThat(result.getProfile()).isEqualTo(ProfileEnum.DA);
        assertThat(result.getEmail()).isEqualTo("u1@bnpparibas.com");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void findOrSyncUserByUid_withProvidedDto_syncsWithoutFetching() {
        when(userRepository.findByUid("u1")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UserEntity result = userService.findOrSyncUserByUid("u1", internalUser("u1", "DIE"));

        assertThat(result.getProfile()).isEqualTo(ProfileEnum.DIE);
        verify(userDetailsService, org.mockito.Mockito.never()).fetchUser(any());
    }

    // ------------------------------------------------------------------
    // getCurrentUser (3 branches)
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
        when(principal.getEmail()).thenReturn("u1@bnpparibas.com");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

        QfUserDetails details = userService.getCurrentUserDetails();

        // uid = authentication.getName() (dépend du toString du principal pour un mock,
        // donc on n'asserte pas sa valeur exacte — seulement email/profil/orgaUnit)
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
    // resolveWorkflowUserFrom / FromCurrentSession (switch 3 branches + orElseThrow)
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
    // getManagerOf (switch CONSEILLER / DA / DIE / default + requireXxx)
    // ------------------------------------------------------------------

    @Test
    void getManagerOf_conseiller_managerOfOrgaUnit() {
        InternalUserDTO target = internalUser("emp1", "CONSEILLER");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getManager().getUid()).thenReturn("mgr1");
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("emp1")).thenReturn(target);
        UserEntity manager = user("mgr1");
        when(userRepository.findByUid("mgr1")).thenReturn(Optional.of(manager));

        assertThat(userService.getManagerOf("emp1")).isEqualTo(manager);
    }

    @Test
    void getManagerOf_da_managerOfParentOrgaUnit() {
        InternalUserDTO target = internalUser("da1", "DA");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        OrgaUnitDTO parent = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getParent()).thenReturn(parent);
        when(parent.getManager().getUid()).thenReturn("mgr2");
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("da1")).thenReturn(target);
        UserEntity manager = user("mgr2");
        when(userRepository.findByUid("mgr2")).thenReturn(Optional.of(manager));

        assertThat(userService.getManagerOf("da1")).isEqualTo(manager);
    }

    @Test
    void getManagerOf_die_managerOfParentOrgaUnit() {
        InternalUserDTO target = internalUser("die1", "DIE");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        OrgaUnitDTO parent = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getParent()).thenReturn(parent);
        when(parent.getManager().getUid()).thenReturn("mgr3");
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("die1")).thenReturn(target);
        when(userRepository.findByUid("mgr3")).thenReturn(Optional.of(user("mgr3")));

        assertThat(userService.getManagerOf("die1").getUid()).isEqualTo("mgr3");
    }

    @Test
    void getManagerOf_unsupportedProfile_throwsIllegalArgument() {
        InternalUserDTO target = internalUser("lmr1", "LMR");
        when(target.getOrgaUnit()).thenReturn(mock(OrgaUnitDTO.class));
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
    void getManagerOf_daWithoutParentOrgaUnit_throwsIllegalState() {
        InternalUserDTO target = internalUser("da1", "DA");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class);
        when(orgaUnit.getParent()).thenReturn(null);
        when(target.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchUser("da1")).thenReturn(target);

        assertThatThrownBy(() -> userService.getManagerOf("da1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sans parent");
    }

    // ------------------------------------------------------------------
    // getUsersEligibleForUnassignment (DA / DZ / autres)
    // ------------------------------------------------------------------

    @Test
    void getUsersEligibleForUnassignment_da_fetchesOrgaUnitUsersWithoutManager() {
        InternalUserDTO principal = authenticate("da1", "DA");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getId()).thenReturn(10L);
        when(orgaUnit.getManager().getUid()).thenReturn("da1");
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);

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
        when(userDetailsService.fetchActiveUsersByParentOrgaUnit(20L))
                .thenReturn(List.of(internalUser("die1", "DIE")));

        List<UserEntity> result = userService.getUsersEligibleForUnassignment();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUid()).isEqualTo("die1");
        assertThat(result.get(0).getProfile()).isEqualTo(ProfileEnum.DIE);
    }

    @Test
    void getUsersEligibleForUnassignment_otherProfile_returnsEmpty() {
        InternalUserDTO principal = authenticate("c1", "CONSEILLER");
        when(principal.getOrgaUnit()).thenReturn(mock(OrgaUnitDTO.class));

        assertThat(userService.getUsersEligibleForUnassignment()).isEmpty();
    }

    // ------------------------------------------------------------------
    // getUsersCandidateToReaffctetionByCurrentUserProfile (switch type AGENCE/ZONE/GROUPE/default)
    // ⚠️ TODO : vérifier le nom exact de la méthode (typo "Reaffctetion" vue sur capture)
    // ------------------------------------------------------------------

    @Test
    void getCandidatesReaffectation_agence_fetchesOrgaUnitUsersWithoutManager() {
        InternalUserDTO principal = authenticate("da1", "DA");
        OrgaUnitDTO orgaUnit = mock(OrgaUnitDTO.class, RETURNS_DEEP_STUBS);
        when(orgaUnit.getType()).thenReturn("AGENCE");
        when(orgaUnit.getId()).thenReturn(10L);
        when(orgaUnit.getManager().getUid()).thenReturn("da1");
        when(principal.getOrgaUnit()).thenReturn(orgaUnit);
        when(userDetailsService.fetchActiveUsersByOrgaUnit(10L))
                .thenReturn(List.of(internalUser("da1", "DA"), internalUser("emp1", "CONSEILLER")));

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
        when(userDetailsService.fetchActiveUsersByParentOrgaUnit(20L))
                .thenReturn(List.of(internalUser("die1", "DIE")));

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
        when(userDetailsService.fetchActiveUsersByParentOrgaUnit(30L))
                .thenReturn(List.of(internalUser("die2", "DIE")));

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
