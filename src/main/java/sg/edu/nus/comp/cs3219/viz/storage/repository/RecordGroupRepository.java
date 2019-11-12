package sg.edu.nus.comp.cs3219.viz.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.RecordGroup;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.ReviewRecord;

import java.util.List;

public interface RecordGroupRepository extends JpaRepository<RecordGroup, Long> {

    List<RecordGroup> findByDataSetEquals(String dataset);

    void deleteById(String id);
}
