package sg.edu.nus.comp.cs3219.viz.ui.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.comp.cs3219.viz.common.datatransfer.AccessLevel;
import sg.edu.nus.comp.cs3219.viz.common.datatransfer.UserInfo;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.AuthorRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.RecordGroup;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.ReviewRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.SubmissionRecord;
import sg.edu.nus.comp.cs3219.viz.common.exception.RecordGroupNotFoundException;
import sg.edu.nus.comp.cs3219.viz.logic.GateKeeper;
import sg.edu.nus.comp.cs3219.viz.logic.RecordLogic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class RecordController extends BaseRestController {

    private GateKeeper gateKeeper;

    private RecordLogic recordLogic;

    public RecordController(GateKeeper gateKeeper, RecordLogic recordLogic) {
        this.gateKeeper = gateKeeper;
        this.recordLogic = recordLogic;
    }

    @PostMapping("/record/record_groups")
    public ResponseEntity<?> addRecordGroup(@RequestBody String recordGroupName) throws URISyntaxException {
        UserInfo userInfo = gateKeeper.verifyLoginAccess();
        RecordGroup recordGroup = new RecordGroup();

        recordGroup.setDataSet(userInfo.getUserEmail());
        recordGroup.setRecordGroupName(recordGroupName);

        this.recordLogic.saveForRecordGroup(recordGroup, userInfo);

        return ResponseEntity.created(new URI("/record/record_group")).build();
    }

    @PutMapping("/record/record_groups/{id}")
    public ResponseEntity<?> updateRecordGroup(@RequestBody RecordGroup newRecordGroup, @PathVariable Long id)
                                                throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        RecordGroup oldRecordGroup = recordLogic.findById(id)
                .orElseThrow(() -> new RecordGroupNotFoundException(id));

        RecordGroup updatedRecordGroup = recordLogic.updateRecordGroup(oldRecordGroup, newRecordGroup);
        return ResponseEntity
                .created(new URI("/record/record_groups/" + updatedRecordGroup.getId()))
                .body(updatedRecordGroup);
    }

    @DeleteMapping("/record/record_groups/{recordGroupId}")
    public ResponseEntity<?> removeRecordGroup(@PathVariable Long recordGroupId,
                                               @RequestBody String recordGroupName) throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        this.recordLogic.deleteRecordGroupById(recordGroupId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/record_groups")
    public List<RecordGroup> all() {
        UserInfo currentUser = gateKeeper.verifyLoginAccess();

        return this.recordLogic.findAllForUser(currentUser);
    }

    @PostMapping("/record/author/{recordGroupId}")
    public ResponseEntity<?> importAuthorRecord(@PathVariable Long recordGroupId,
                                                @RequestBody List<AuthorRecord> authorRecordList)
                                                throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistAuthorRecordForRecordGroup(recordGroupId, authorRecordList);

        RecordGroup oldRecordGroup = recordLogic.findById(recordGroupId)
                .orElseThrow(() -> new RecordGroupNotFoundException(recordGroupId));

        RecordGroup newRecordGroup = oldRecordGroup;
        newRecordGroup.setAuthorRecordUploadStatus(true);

        this.recordLogic.updateRecordGroup(oldRecordGroup, newRecordGroup);

        return ResponseEntity.created(new URI("/record/author")).build();
    }

    @PostMapping("/record/review/{recordGroupId}")
    public ResponseEntity<?> importReviewRecord(@PathVariable Long recordGroupId,
                                                @RequestBody List<ReviewRecord> reviewRecordList)
                                                throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistReviewRecordForRecordGroup(recordGroupId, reviewRecordList);


        RecordGroup oldRecordGroup = recordLogic.findById(recordGroupId)
                .orElseThrow(() -> new RecordGroupNotFoundException(recordGroupId));

        RecordGroup newRecordGroup = oldRecordGroup;
        newRecordGroup.setReviewRecordUploadStatus(true);

        this.recordLogic.updateRecordGroup(oldRecordGroup, newRecordGroup);

        return ResponseEntity.created(new URI("/record/review")).build();
    }

    @PostMapping("/record/submission/{recordGroupId}")
    public ResponseEntity<?> importSubmissionRecord(@PathVariable Long recordGroupId,
                                                    @RequestBody List<SubmissionRecord> submissionRecords)
                                                    throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistSubmissionRecordForRecordGroup(recordGroupId, submissionRecords);

        RecordGroup oldRecordGroup = recordLogic.findById(recordGroupId)
                .orElseThrow(() -> new RecordGroupNotFoundException(recordGroupId));

        RecordGroup newRecordGroup = oldRecordGroup;
        newRecordGroup.setSubmissionRecordUploadStatus(true);

        this.recordLogic.updateRecordGroup(oldRecordGroup, newRecordGroup);

        return ResponseEntity.created(new URI("/record/submission")).build();
    }
}
