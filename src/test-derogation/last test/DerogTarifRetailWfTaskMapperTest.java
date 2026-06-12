package bpmn;
// ⚠️ Mets ce fichier dans n'importe lequel de tes packages de test existants.

import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.WfTask;
import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.wf.DerogTarifRetailWfTaskMapper;
import com.bnpparibas.irb.qlickflow.wfdtrp.bpm.wf.WfTaskDto;
import com.bnpparibas.irb.qlickflow.wfdtrp.entities.DerogationRequest;
// TODO : ajuster les imports selon tes packages réels

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DerogTarifRetailWfTaskMapperTest {

    private final DerogTarifRetailWfTaskMapper mapper = new DerogTarifRetailWfTaskMapper();

    private WfTask taskWithBusinessKey(String engineTaskId, String businessKey) {
        DerogationRequest wfInstance = DerogationRequest.builder().businessKey(businessKey).build();
        WfTask task = WfTask.builder()
                .engineTaskId(engineTaskId)
                .createdAt(OffsetDateTime.now())
                .build();
        task.setWfInstance(wfInstance);
        return task;
    }

    @Test
    void map_nullTask_returnsNull() {
        assertThat(mapper.map((WfTask) null)).isNull();
    }

    @Test
    void map_task_mapsTaskIdAndBusinessKey() {
        WfTaskDto dto = mapper.map(taskWithBusinessKey("engine-42", "BK-001"));

        assertThat(dto.getTaskId()).isEqualTo("engine-42");
        assertThat(dto.getWfInstance().getBusinessKey()).isEqualTo("BK-001");
    }

    @Test
    void map_nullList_returnsNull() {
        assertThat(mapper.map((List<WfTask>) null)).isNull();
    }

    @Test
    void map_list_mapsEachTask() {
        List<WfTaskDto> result = mapper.map(List.of(
                taskWithBusinessKey("e1", "BK-001"),
                taskWithBusinessKey("e2", "BK-002")));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTaskId()).isEqualTo("e1");
        assertThat(result.get(1).getWfInstance().getBusinessKey()).isEqualTo("BK-002");
    }
}
