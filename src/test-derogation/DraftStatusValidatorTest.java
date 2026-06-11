package com.bnpparibas.irb.qlickflow.wfdtrp.dto;
// ⚠️ TODO : ajuster ce package pour qu'il corresponde EXACTEMENT au package de DraftStatusValidator
// (non visible sur la capture — probablement dto, dto.validation ou validation).
// Le test doit être dans le même package, sous src/test/java.

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationStatus;
// TODO : ajuster l'import de DerogationStatus si le package diffère (entities ou entities.derogation)

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DraftStatusValidatorTest {

    private final DraftStatusValidator validator = new DraftStatusValidator();

    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() {
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        lenient().when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        lenient().when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    /** DTO valide de référence (status SUBMITTED) — chaque test ne casse qu'une règle. */
    private CreateDerogationRequestDTO validDto() {
        return CreateDerogationRequestDTO.builder()
                .status(DerogationStatus.SUBMITTED)
                .clientSubId("0406102")
                .accountNumber("1000000023456789") // 16 chiffres
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(30))
                .tauxMontant(new BigDecimal("10.5"))
                .tauxMontantLabel("10,5 %")
                .convention("non")
                .firstName("Ahmed")
                .lastName("El Amrani")
                .packageField("PACK1")
                .segment("GRP")
                .businessLine("RET")
                .justification("Client fidèle, dérogation justifiée")
                .motifDerogation("MOTIF1")
                .build();
    }

    // ------------------------------------------------------------------
    // Court-circuit DRAFT
    // ------------------------------------------------------------------

    @Test
    @DisplayName("status DRAFT → true immédiatement, aucune validation effectuée")
    void isValid_draft_shortCircuitsToTrue() {
        CreateDerogationRequestDTO dto = CreateDerogationRequestDTO.builder()
                .status(DerogationStatus.DRAFT)
                .build(); // tous les autres champs null : aucune règle ne doit s'exécuter

        boolean result = validator.isValid(dto, context);

        assertThat(result).isTrue();
        verifyNoInteractions(context);
    }

    @Test
    @DisplayName("DTO complet et valide (SUBMITTED) → true")
    void isValid_fullyValidDto_returnsTrue() {
        assertThat(validator.isValid(validDto(), context)).isTrue();
    }

    // ------------------------------------------------------------------
    // clientSubId
    // ------------------------------------------------------------------

    @Test
    void isValid_clientSubIdNull_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setClientSubId(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'clientSubId' est requis.");
        verify(violationBuilder).addPropertyNode("clientSubId");
    }

    @Test
    void isValid_clientSubIdEmpty_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setClientSubId("");

        assertThat(validator.isValid(dto, context)).isFalse();
    }

    // ------------------------------------------------------------------
    // accountNumber (3 branches : null/empty, non numérique ou trop long)
    // ------------------------------------------------------------------

    @Test
    void isValid_accountNumberNull_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setAccountNumber(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'accountNumber' est requis.");
    }

    @Test
    void isValid_accountNumberNonNumeric_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setAccountNumber("ABC123");

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate(
                "Le 'accountNumber' doit être numérique et limité à 16 positions.");
    }

    @Test
    void isValid_accountNumberTooLong_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setAccountNumber("12345678901234567"); // 17 chiffres

        assertThat(validator.isValid(dto, context)).isFalse();
    }

    @Test
    void isValid_accountNumberSingleDigit_isAccepted() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setAccountNumber("7"); // \d{1,16} → 1 chiffre OK

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    // ------------------------------------------------------------------
    // effectiveDate (3 branches : null, antérieure à aujourd'hui, après endDate)
    // ------------------------------------------------------------------

    @Test
    void isValid_effectiveDateNull_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setEffectiveDate(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'effectiveDate' est requis.");
    }

    @Test
    void isValid_effectiveDateInPast_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setEffectiveDate(LocalDate.now().minusDays(1));

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate(
                "La 'effectiveDate' ne peut pas être antérieure à aujourd'hui.");
    }

    @Test
    void isValid_effectiveDateToday_isAccepted() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setEffectiveDate(LocalDate.now()); // isBefore(now) == false → OK

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void isValid_effectiveDateAfterEndDate_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setEffectiveDate(LocalDate.now().plusDays(60));
        dto.setEndDate(LocalDate.now().plusDays(30));

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate(
                "La 'effectiveDate' ne peut pas être après 'endDate'.");
    }

    // ------------------------------------------------------------------
    // endDate
    // ------------------------------------------------------------------

    @Test
    void isValid_endDateNull_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setEndDate(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'endDate' est requis.");
    }

    // ------------------------------------------------------------------
    // tauxMontant (null accepté ; <= 0 refusé)
    // ------------------------------------------------------------------

    @Test
    void isValid_tauxMontantNull_isAccepted() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setTauxMontant(null);

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void isValid_tauxMontantZero_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setTauxMontant(BigDecimal.ZERO);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le 'tauxMontant' doit être supérieur à zéro.");
    }

    @Test
    void isValid_tauxMontantNegative_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setTauxMontant(new BigDecimal("-5"));

        assertThat(validator.isValid(dto, context)).isFalse();
    }

    // ------------------------------------------------------------------
    // tauxMontantLabel
    // ------------------------------------------------------------------

    @Test
    void isValid_tauxMontantLabelNull_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setTauxMontantLabel(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'tauxMontantLabel' est requis.");
    }

    // ------------------------------------------------------------------
    // convention / employeur (conditionnel)
    // ------------------------------------------------------------------

    @Test
    void isValid_conventionOuiWithoutEmployeur_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setConvention("oui");
        dto.setEmployeur(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate(
                "Le champ 'employeur' est requis lorsque 'convention' est 'oui'.");
    }

    @Test
    void isValid_conventionOuiUppercaseWithoutEmployeur_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setConvention("OUI"); // equalsIgnoreCase
        dto.setEmployeur("");

        assertThat(validator.isValid(dto, context)).isFalse();
    }

    @Test
    void isValid_conventionOuiWithEmployeur_isAccepted() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setConvention("oui");
        dto.setEmployeur("BNP Paribas");

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void isValid_conventionNonWithoutEmployeur_isAccepted() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setConvention("non");
        dto.setEmployeur(null);

        assertThat(validator.isValid(dto, context)).isTrue();
    }

    // ------------------------------------------------------------------
    // Champs obligatoires simples
    // ------------------------------------------------------------------

    @Test
    void isValid_firstNameMissing_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setFirstName(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'firstName' est requis.");
    }

    @Test
    void isValid_lastNameMissing_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setLastName("");

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'lastName' est requis.");
    }

    @Test
    void isValid_packageFieldMissing_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setPackageField(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'packageField' est requis.");
    }

    @Test
    void isValid_segmentMissing_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setSegment("");

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'segment' est requis.");
    }

    @Test
    void isValid_businessLineMissing_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setBusinessLine(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'businessLine' est requis.");
    }

    @Test
    void isValid_justificationMissing_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setJustification("");

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'justification' est requis.");
    }

    @Test
    void isValid_motifDerogationMissing_returnsFalse() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setMotifDerogation(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'motifDerogation' est requis.");
    }

    // ------------------------------------------------------------------
    // Accumulation : plusieurs champs manquants → false, toutes les violations construites
    // ------------------------------------------------------------------

    @Test
    void isValid_multipleMissingFields_accumulatesViolations() {
        CreateDerogationRequestDTO dto = validDto();
        dto.setClientSubId(null);
        dto.setJustification(null);
        dto.setMotifDerogation(null);

        assertThat(validator.isValid(dto, context)).isFalse();
        verify(context).buildConstraintViolationWithTemplate("Le champ 'clientSubId' est requis.");
        verify(context).buildConstraintViolationWithTemplate("Le champ 'justification' est requis.");
        verify(context).buildConstraintViolationWithTemplate("Le champ 'motifDerogation' est requis.");
    }
}
