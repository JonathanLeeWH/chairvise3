package sg.edu.nus.comp.cs3219.viz.common.exception;

public class MailMessageException extends RuntimeException {

    public MailMessageException() {
        super("There is an error sending the mail.");
    }
}
