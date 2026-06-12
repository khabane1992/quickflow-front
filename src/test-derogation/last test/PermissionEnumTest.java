package service;
// ⚠️ Mets ce fichier dans n'importe lequel de tes packages de test existants.

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.user.PermissionEnum;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionEnumTest {

    @Test
    void allValues_haveNonBlankDesc() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            assertThat(permission.getDesc())
                    .as("desc de %s", permission.name())
                    .isNotBlank();
        }
    }

    @Test
    void enumContainsAllExpectedPermissions() {
        assertThat(PermissionEnum.values()).hasSize(16);
        assertThat(PermissionEnum.valueOf("SUBMIT")).isEqualTo(PermissionEnum.SUBMIT);
        assertThat(PermissionEnum.valueOf("RE_AFFECTATION")).isEqualTo(PermissionEnum.RE_AFFECTATION);
    }

    @Test
    void descriptions_spotChecks() {
        assertThat(PermissionEnum.CHOIX_WF.getDesc()).isEqualTo("Choix de WF");
        assertThat(PermissionEnum.SUBMIT.getDesc()).isEqualTo("Soumettre");
        assertThat(PermissionEnum.VALIDATE.getDesc()).isEqualTo("Valider");
        assertThat(PermissionEnum.RE_AFFECTATION.getDesc()).isEqualTo("Accès au dossiers et ré-affectation");
    }
}
