package com.bnpparibas.irb.qlickflow.wfdtrp.bpm;
// ⚠️ TODO : ajuster le package selon l'emplacement réel de WfInstanceManager.
// ⚠️ WfInstance est abstract → on teste via DerogationRequest (héritier concret).

import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
import com.bnpparibas.irb.qlickflow.wfdtrp.repository.WfInstanceRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WfInstanceManagerTest {

    @Mock
    private WfInstanceRepository wfInstanceRepository;

    @InjectMocks
    private WfInstanceManager wfInstanceManager;

    @Test
    void saveWfInstance_delegatesToRepository() {
        DerogationRequest wfInstance = DerogationRequest.builder().businessKey("BK-001").build();
        when(wfInstanceRepository.save(wfInstance)).thenReturn(wfInstance);

        assertThat(wfInstanceManager.saveWfInstance(wfInstance)).isEqualTo(wfInstance);
        verify(wfInstanceRepository).save(wfInstance);
    }

    @Test
    void updateWfInstance_delegatesToRepository() {
        DerogationRequest wfInstance = DerogationRequest.builder().businessKey("BK-002").build();
        when(wfInstanceRepository.save(wfInstance)).thenReturn(wfInstance);

        assertThat(wfInstanceManager.updateWfInstance(wfInstance)).isEqualTo(wfInstance);
        verify(wfInstanceRepository).save(wfInstance);
    }
}
