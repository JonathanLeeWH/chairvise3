package sg.edu.nus.comp.cs3219.viz.logic;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.nus.comp.cs3219.viz.common.datatransfer.UserInfo;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.*;
import sg.edu.nus.comp.cs3219.viz.storage.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RecordLogic {
    private AuthorRecordRepository authorRecordRepository;

    private SubmissionRecordRepository submissionRecordRepository;

    private SubmissionAuthorRecordRepository submissionAuthorRecordRepository;

    private ReviewRecordRepository reviewRecordRepository;

    private RecordGroupRepository recordGroupRepository;

    public RecordLogic(AuthorRecordRepository authorRecordRepository,
                       SubmissionRecordRepository submissionRecordRepository,
                       SubmissionAuthorRecordRepository submissionAuthorRecordRepository,
                       ReviewRecordRepository reviewRecordRepository,
                       RecordGroupRepository recordGroupRepository) {
        this.authorRecordRepository = authorRecordRepository;
        this.submissionRecordRepository = submissionRecordRepository;
        this.submissionAuthorRecordRepository = submissionAuthorRecordRepository;
        this.reviewRecordRepository = reviewRecordRepository;
        this.recordGroupRepository = recordGroupRepository;
    }

    public List<RecordGroup> findAllForUser(UserInfo userInfo) {
        return recordGroupRepository.findByDataSetEquals(userInfo.getUserEmail());
    }

    public Optional<RecordGroup> findById(Long id) {
        return recordGroupRepository.findById(id);
    }

    public RecordGroup saveForRecordGroup(RecordGroup recordGroup, UserInfo userInfo) {
        RecordGroup newRecordGroup = new RecordGroup();
        newRecordGroup.setRecordGroupName(recordGroup.getRecordGroupName());
        newRecordGroup.setDataSet(userInfo.getUserEmail());
        newRecordGroup.setAuthorRecordUploadStatus(false);
        newRecordGroup.setReviewRecordUploadStatus(false);
        newRecordGroup.setSubmissionRecordUploadStatus(false);

        return recordGroupRepository.save(recordGroup);
    }

    public RecordGroup updateRecordGroup(RecordGroup oldRecordGroup, RecordGroup newRecordGroup) {
        oldRecordGroup.setRecordGroupName(newRecordGroup.getRecordGroupName());
        return recordGroupRepository.save(oldRecordGroup);
    }

    public void deleteRecordGroupById(long id) {
        recordGroupRepository.deleteById(id);
    }

    @Transactional
    public void removeAndPersistAuthorRecordForRecordGroup(Long recordGroupId, String dataSet,
                                                       List<AuthorRecord> authorRecordList) {
        authorRecordRepository.deleteAllByRecordGroupIdEquals(recordGroupId);
        authorRecordRepository.saveAll(authorRecordList.stream().peek(r -> {
            // should not set ID when creating records
            r.setId(null);
            r.setDataSet(dataSet);
            // set the recordGroupId
            r.setRecordGroupId(recordGroupId);
            // the other field can be arbitrary
        }).collect(Collectors.toList()));
    }

    @Transactional
    public void removeAndPersistReviewRecordForRecordGroup(Long recordGroupId, String dataSet,
                                                       List<ReviewRecord> reviewRecordList) {
        reviewRecordRepository.deleteAllByRecordGroupIdEquals(recordGroupId);
        reviewRecordRepository.saveAll(reviewRecordList.stream().peek(r -> {
            // should not set ID when creating records
            r.setDataSet(dataSet);
            r.setId(null);
            // set the recordGroupId
            r.setRecordGroupId(recordGroupId);
            // the other field can be arbitrary
        }).collect(Collectors.toList()));
    }

    @Transactional
    public void removeAndPersistSubmissionRecordForRecordGroup(Long recordGroupId, String dataSet,
                                                           List<SubmissionRecord> submissionRecordList) {
        submissionRecordRepository.deleteAllByRecordGroupIdEquals(recordGroupId);
        submissionAuthorRecordRepository.deleteAllByRecordGroupIdEquals(recordGroupId);
        submissionRecordRepository.saveAll(submissionRecordList.stream().peek(s -> {
            // should not set ID when creating records
            s.setId(null);
            // set the recordGroupId
            s.setDataSet(dataSet);
            s.setRecordGroupId(recordGroupId);
            // create many to many relationship for authors
            List<SubmissionAuthorRecord> submissionAuthorRecords = s.getAuthors().stream()
                    .map(authorName -> {
                        SubmissionAuthorRecord existing =
                                submissionAuthorRecordRepository
                                        .findFirstByNameEqualsAndRecordGroupIdEquals(authorName, recordGroupId);
                        if (existing == null) {
                            existing = new SubmissionAuthorRecord();
                            existing.setName(authorName);
                            existing.setDataSet(dataSet);
                            existing.setRecordGroupId(recordGroupId);
                            existing = submissionAuthorRecordRepository.save(existing);
                        }
                        return existing;
                    })
                    .collect(Collectors.toList());
            s.setAuthorSet(submissionAuthorRecords);
            // the other field can be arbitrary
        }).collect(Collectors.toList()));
    }
}
