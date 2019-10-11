package sg.edu.nus.comp.cs3219.viz.ui.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.nus.comp.cs3219.viz.common.datatransfer.UserInfo;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.AuthorRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.RecordGroup;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.ReviewRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.SubmissionRecord;
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

    @PostMapping("/record/record_group")
    public ResponseEntity<?> addRecordGroup(@RequestBody String recordGroupName) throws URISyntaxException {
        UserInfo userInfo = gateKeeper.verifyLoginAccess();
        RecordGroup recordGroup = new RecordGroup();

        recordGroup.setDataSet(userInfo.getUserEmail());
        recordGroup.setRecordGroupName(recordGroupName);

        this.recordLogic.addRecordGroup(recordGroup);

        return ResponseEntity.created(new URI("/record/record_group")).build();
    }

    @PostMapping("/record/remove_record_group")
    public ResponseEntity<?> removeRecordGroup(@RequestBody Long recordGroupId,
                                               @RequestBody String recordGroupName) throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistRecordGroup(recordGroupId);

        return ResponseEntity.created(new URI("/record/remove_record_group")).build();
    }

    @PostMapping("/record/author")
    public ResponseEntity<?> importAuthorRecord(@RequestBody Long recordGroupId,
                                                @RequestBody List<AuthorRecord> authorRecordList) throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistAuthorRecordForRecordGroup(recordGroupId, authorRecordList);

        return ResponseEntity.created(new URI("/record/author")).build();
    }

    @PostMapping("/record/review")
    public ResponseEntity<?> importReviewRecord(@RequestBody Long recordGroupId,
                                                @RequestBody List<ReviewRecord> reviewRecordList) throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistReviewRecordForRecordGroup(recordGroupId, reviewRecordList);

        return ResponseEntity.created(new URI("/record/review")).build();
    }

    @PostMapping("/record/submission")
    public ResponseEntity<?> importSubmissionRecord(@RequestBody Long recordGroupId,
                                                    @RequestBody List<SubmissionRecord> submissionRecords) throws URISyntaxException {
        gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistSubmissionRecordForRecordGroup(recordGroupId, submissionRecords);

        return ResponseEntity.created(new URI("/record/review")).build();
    }
}
