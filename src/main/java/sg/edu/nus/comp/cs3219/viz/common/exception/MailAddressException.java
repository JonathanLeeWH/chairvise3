package sg.edu.nus.comp.cs3219.viz.common.exception;

public class MailAddressException extends RuntimeException {
    public MailAddressException() {
        super("Invalid Mail Address");
    }
}
