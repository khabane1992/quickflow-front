package bpmn;
// ⚠️ Mets ce fichier dans n'importe lequel de tes packages de test existants.

import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.wf.SequentialBusinessKeyGenerator;
import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.wf.WfType;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.WfInstanceRepository;
// TODO : ajuster les imports (WfType, WfInstanceRepository) selon tes packages

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SequentialBusinessKeyGeneratorTest {

    @Mock
    private WfInstanceRepository wfInstanceRepository;

    @InjectMocks
    private SequentialBusinessKeyGenerator generator;

    @Test
    void generate_noExistingKey_returnsFirstKey() {
        when(wfInstanceRepository.findMaxBusinessKeyByCreatedAt(WfType.DER_TAR_RET_PACK))
                .thenReturn(null);

        String key = generator.generate(WfType.DER_TAR_RET_PACK, "DRG");

        assertThat(key).isEqualTo("DRG-000001");
    }

    @Test
    void generate_existingKey_incrementsNumericPart() {
        when(wfInstanceRepository.findMaxBusinessKeyByCreatedAt(WfType.DER_TAR_RET_PACK))
                .thenReturn("DRG-000041");

        String key = generator.generate(WfType.DER_TAR_RET_PACK, "DRG");

        assertThat(key).isEqualTo("DRG-000042");
    }

    @Test
    void generate_keepsZeroPadding() {
        when(wfInstanceRepository.findMaxBusinessKeyByCreatedAt(WfType.DER_TAR_RET_PACK))
                .thenReturn("DRG-000999");

        // ⚠️ suppose un format %06d (padding sur 6) — ajuster si le format diffère
        assertThat(generator.generate(WfType.DER_TAR_RET_PACK, "DRG"))
                .isEqualTo("DRG-001000");
    }

    @Test
    void generate_keyWithMultipleSeparators_incrementsOnlyLastPart() {
        when(wfInstanceRepository.findMaxBusinessKeyByCreatedAt(WfType.DER_TAR_RET_PACK))
                .thenReturn("DER-TAR-000007");

        assertThat(generator.generate(WfType.DER_TAR_RET_PACK, "DER-TAR"))
                .isEqualTo("DER-TAR-000008");
    }
}
