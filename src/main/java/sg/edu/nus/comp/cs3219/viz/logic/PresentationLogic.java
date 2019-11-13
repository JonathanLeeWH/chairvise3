package sg.edu.nus.comp.cs3219.viz.logic;

import org.springframework.stereotype.Component;
import sg.edu.nus.comp.cs3219.viz.common.datatransfer.UserInfo;
import sg.edu.nus.comp.cs3219.viz.common.entity.Presentation;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.RecordGroup;
import sg.edu.nus.comp.cs3219.viz.common.exception.RecordGroupNotFoundException;
import sg.edu.nus.comp.cs3219.viz.storage.repository.PresentationRepository;
import sg.edu.nus.comp.cs3219.viz.storage.repository.RecordGroupRepository;

import java.util.List;
import java.util.Optional;

@Component
public class PresentationLogic {

    private final PresentationRepository presentationRepository;
    private final RecordGroupRepository recordGroupRepository;

    public PresentationLogic(PresentationRepository presentationRepository,
         RecordGroupRepository recordGroupRepository) {
            this.presentationRepository = presentationRepository;
            this.recordGroupRepository = recordGroupRepository;
        }

    public List<Presentation> findAllForUser(UserInfo userInfo) {
        return presentationRepository.findByCreatorIdentifier(userInfo.getUserEmail());
    }

    public Optional<Presentation> findById(Long id) {
        return presentationRepository.findById(id);
    }

    public Presentation saveForUser(Presentation presentation, UserInfo userInfo) {
        Presentation newPresentation = new Presentation();
        newPresentation.setName(presentation.getName());
        newPresentation.setDescription(presentation.getDescription());

        RecordGroup recordGroup = recordGroupRepository
                .findById(presentation.getRecordGroupId())
                .orElseThrow(() -> new RecordGroupNotFoundException(presentation.getRecordGroupId()));

        newPresentation.setRecordGroupId(presentation.getRecordGroupId());
        newPresentation.setRecordGroupName(recordGroup.getRecordGroupName());
        newPresentation.setCreatorIdentifier(userInfo.getUserEmail());

        return presentationRepository.save(newPresentation);
    }

    public Presentation updatePresentation(Presentation oldPresentation, Presentation newPresentation) {
        oldPresentation.setName(newPresentation.getName());
        oldPresentation.setDescription(newPresentation.getDescription());

        RecordGroup recordGroup = recordGroupRepository
                .findById(oldPresentation.getRecordGroupId())
                .orElseThrow(() -> new RecordGroupNotFoundException(oldPresentation.getRecordGroupId()));
        
        oldPresentation.setRecordGroupId(newPresentation.getRecordGroupId());
        oldPresentation.setRecordGroupName(recordGroup.getRecordGroupName());
        return presentationRepository.save(oldPresentation);
    }

    public void deleteById(Long id) {
        presentationRepository.deleteById(id);
    }
}
