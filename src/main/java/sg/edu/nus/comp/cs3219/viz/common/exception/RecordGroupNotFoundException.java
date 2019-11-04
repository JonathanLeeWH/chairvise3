package sg.edu.nus.comp.cs3219.viz.common.exception;

public class RecordGroupNotFoundException extends RuntimeException {
    public RecordGroupNotFoundException(Long id) {
        super("Could not find record group " + id);
    }

}
