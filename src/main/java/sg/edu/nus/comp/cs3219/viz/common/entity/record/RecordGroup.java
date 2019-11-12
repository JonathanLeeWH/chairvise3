package sg.edu.nus.comp.cs3219.viz.common.entity.record;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Exportable(name = "Data Upload", nameInDB = "data_upload")
@Entity
public class RecordGroup {

    @Id
    @GenericGenerator(name = "UseExistingIdOtherwiseGenerateUsingIdentity", strategy = "sg.edu.nus.comp.cs3219.viz.common.entity.UseExistingIdOtherwiseGenerateUsingIdentity")
    @GeneratedValue(generator = "UseExistingIdOtherwiseGenerateUsingIdentity")
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "rg_id")
    private Long id;

    // each record will be imported by each user, dataSet is used to distinguished records submitted by different user
    private String dataSet;

    @Exportable(name = "Record Group Name", nameInDB = "rg_name")
    @Column(name = "rg_name")
    private String recordGroupName;

    @Exportable(name = "Is Author Record Uploaded", nameInDB = "isAuthorRecordUploaded")
    @Column(name = "isAuthorRecordUploaded")
    private boolean isAuthorRecordUploaded;

    @Exportable(name = "Is Review Record Uploaded", nameInDB = "isReviewRecordUploaded")
    @Column(name = "isReviewRecordUploaded")
    private boolean isReviewRecordUploaded;

    @Exportable(name = "Is Submission Record Uploaded", nameInDB = "isSubmissionRecordUploaded")
    @Column(name = "isSubmissionRecordUploaded")
    private boolean isSubmissionRecordUploaded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public String getRecordGroupName() {
        return recordGroupName;
    }

    public void setRecordGroupName(String recordGroupName) {
        this.recordGroupName = recordGroupName;
    }

    public boolean getAuthorRecordUploadStatus() {
        return isAuthorRecordUploaded;
    }

    public void setAuthorRecordUploadStatus(boolean isAuthorRecordUploaded) {
        this.isAuthorRecordUploaded = isAuthorRecordUploaded;
    }

    public boolean getReviewRecordUploadedStatus() {
        return isReviewRecordUploaded;
    }

    public void setReviewRecordUploadStatus(boolean isReviewRecordUploaded) {
        this.isReviewRecordUploaded = isReviewRecordUploaded;
    }

    public boolean getSubmissionRecordUploadStatus() {
        return isSubmissionRecordUploaded;
    }

    public void setSubmissionRecordUploadStatus(boolean isSubmissionRecordUploaded) {
        this.isSubmissionRecordUploaded = isSubmissionRecordUploaded;
    }
}
