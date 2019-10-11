package sg.edu.nus.comp.cs3219.viz.common.entity.record;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SubmissionAuthorRecord {

    @Id
    @GenericGenerator(name = "UseExistingIdOtherwiseGenerateUsingIdentity", strategy = "sg.edu.nus.comp.cs3219.viz.common.entity.UseExistingIdOtherwiseGenerateUsingIdentity")
    @GeneratedValue(generator = "UseExistingIdOtherwiseGenerateUsingIdentity")
    @JsonSerialize(using = ToStringSerializer.class)
    @Column(name = "s_author_id")
    private Long id;

    @Column(name = "s_author_name")
    private String name;

    @Column(name = "rg_id")
    private Long recordGroupId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRecordGroupId() {
        return recordGroupId;
    }

    public void setRecordGroupId(Long recordGroupId) {
        this.recordGroupId = recordGroupId;
    }
}
