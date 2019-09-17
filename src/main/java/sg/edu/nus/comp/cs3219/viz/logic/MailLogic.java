package sg.edu.nus.comp.cs3219.viz.logic;

import org.springframework.stereotype.Component;
import sg.edu.nus.comp.cs3219.viz.common.util.JavaMailUtilities;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.logging.Logger;

@Component
public class MailLogic {

    private static final Logger log = Logger.getLogger(MailLogic.class.getSimpleName());

    private JavaMailUtilities javaMailUtilities;

    public MailLogic() {
        this.javaMailUtilities = new JavaMailUtilities();
    }

    public void sendMessage() {
        try {
            Message message = new MimeMessage(this.javaMailUtilities.getJavaMailSession());
            message.setFrom(new InternetAddress("<send from email address>"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("<send to email address>"));
            message.setSubject("Hello World");
            message.setText("Hello World");
            Transport.send(message);
        } catch (AddressException ex) {
            log.info("Error Sending Email: " + ex.getMessage());
        } catch (MessagingException ex) {
            log.info("Error Sending Email: " + ex.getMessage());
        }
    }
}
