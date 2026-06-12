package service;
// ⚠️ Mets ce fichier dans n'importe lequel de tes packages de test existants.
// ⚠️ TODO : remplacer "DateValidationUtils" par le VRAI nom de la classe
// (package common.validators — celle de tes captures avec validateForCreation/validateForWorkflow)
// et importer DateValidationException depuis son vrai package.

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateValidationUtilsTest {

    private final LocalDate today = LocalDate.now();

    // ------------------------------------------------------------------
    // validateForCreation
    // ------------------------------------------------------------------

    @Test
    void validateForCreation_bothNull_passes() {
        assertThatCode(() -> DateValidationUtils.validateForCreation(null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateForCreation_effectiveDateInPast_throws() {
        assertThatThrownBy(() ->
                DateValidationUtils.validateForCreation(today.minusDays(1), today.plusMonths(1)))
                .isInstanceOf(DateValidationException.class)
                .hasMessageContaining("passé");
    }

    @Test
    void validateForCreation_validDates_passes() {
        assertThatCode(() ->
                DateValidationUtils.validateForCreation(today.plusDays(1), today.plusMonths(6)))
                .doesNotThrowAnyException();
    }

    @Test
    void validateForCreation_endBeforeEffective_throws() {
        assertThatThrownBy(() ->
                DateValidationUtils.validateForCreation(today.plusDays(10), today.plusDays(5)))
                .isInstanceOf(DateValidationException.class)
                .hasMessageContaining("postérieure ou égale");
    }

    @Test
    void validateForCreation_endMoreThanOneYearAfterEffective_throws() {
        assertThatThrownBy(() ->
                DateValidationUtils.validateForCreation(today.plusDays(1), today.plusDays(1).plusYears(1).plusDays(1)))
                .isInstanceOf(DateValidationException.class)
                .hasMessageContaining("un an");
    }

    @Test
    void validateForCreation_endExactlyOneYearAfterEffective_passes() {
        // borne : maxEndDate = effectiveDate.plusYears(1), isAfter strict → égalité OK
        assertThatCode(() ->
                DateValidationUtils.validateForCreation(today.plusDays(1), today.plusDays(1).plusYears(1)))
                .doesNotThrowAnyException();
    }

    @Test
    void validateForCreation_endEqualsEffective_passes() {
        // isBefore strict → égalité OK
        assertThatCode(() ->
                DateValidationUtils.validateForCreation(today.plusDays(5), today.plusDays(5)))
                .doesNotThrowAnyException();
    }

    // ------------------------------------------------------------------
    // validateForWorkflow (la règle "date d'effet non passée" est ignorée)
    // ------------------------------------------------------------------

    @Test
    void validateForWorkflow_effectiveDateInPast_passes() {
        assertThatCode(() ->
                DateValidationUtils.validateForWorkflow(today.minusMonths(2), today.plusMonths(1)))
                .doesNotThrowAnyException();
    }

    @Test
    void validateForWorkflow_oneDateNull_passes() {
        // branche (effectiveDate == null || endDate == null) → return
        assertThatCode(() -> DateValidationUtils.validateForWorkflow(today, null))
                .doesNotThrowAnyException();
        assertThatCode(() -> DateValidationUtils.validateForWorkflow(null, today))
                .doesNotThrowAnyException();
    }

    @Test
    void validateForWorkflow_endBeforeEffective_throws() {
        assertThatThrownBy(() ->
                DateValidationUtils.validateForWorkflow(today.minusDays(5), today.minusDays(10)))
                .isInstanceOf(DateValidationException.class)
                .hasMessageContaining("postérieure ou égale");
    }

    @Test
    void validateForWorkflow_endMoreThanOneYearAfterEffective_throws() {
        assertThatThrownBy(() ->
                DateValidationUtils.validateForWorkflow(today, today.plusYears(1).plusDays(1)))
                .isInstanceOf(DateValidationException.class)
                .hasMessageContaining("un an");
    }
}
