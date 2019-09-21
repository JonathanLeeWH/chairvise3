package sg.edu.nus.comp.cs3219.viz.common.entity;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

public class Mail {

    private List<String> mailTo;

    private String mailSubject;

    private String mailContent;

    private String attachmentName;

    public List<String> getMailTo() {
        return mailTo;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public String getMailContent() {
        return mailContent;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public InternetAddress[] getMailToAsInternetAddress() throws AddressException {
        List<InternetAddress> listOfInternetAddress = new ArrayList<InternetAddress>();
        for (String recipient : mailTo) {
            listOfInternetAddress.add(new InternetAddress(recipient, true));
        }
        return listOfInternetAddress.stream().toArray(InternetAddress[]::new);
    }
}
