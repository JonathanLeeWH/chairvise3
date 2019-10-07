package sg.edu.nus.comp.cs3219.viz.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import sg.edu.nus.comp.cs3219.viz.common.entity.Mail;
import sg.edu.nus.comp.cs3219.viz.common.JavaMailWrapper;
import sg.edu.nus.comp.cs3219.viz.common.exception.MailAddressException;
import sg.edu.nus.comp.cs3219.viz.common.exception.MailMessageException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class MailLogic {

    private static final Logger log = Logger.getLogger(MailLogic.class.getSimpleName());

    @Value("${smtpconfiguration.mail.mailaddress}")
    private String smtpMailAddress;

    @Value("${spring.servlet.multipart.location=${java.io.tmpdir}}")
    private String multiPartLocation;

    private JavaMailWrapper javaMailWrapper;

    @Autowired
    public MailLogic(JavaMailWrapper javaMailWrapper) {
        this.javaMailWrapper = javaMailWrapper;
    }

    public void sendMessage(Mail mailRequest) {
        try {
            Message message = new MimeMessage(this.javaMailWrapper.getJavaMailSession());
            message.setFrom(new InternetAddress(smtpMailAddress));
            message.setRecipients(Message.RecipientType.TO, mailRequest.getMailToAsInternetAddress());
            message.setSubject(mailRequest.getMailSubject());

            Multipart multiPartMessage = new MimeMultipart();

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(mailRequest.getMailContent());
            multiPartMessage.addBodyPart(messageBodyPart);

            MultipartFile attachment = mailRequest.getAttachment();
            if (attachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                File multiPartFileAttachment = new File(multiPartLocation + attachment.getOriginalFilename());
                attachment.transferTo(new File(attachment.getOriginalFilename()));
                log.info(multiPartLocation);
                log.info(attachment.getOriginalFilename());
                DataSource dataSource = new FileDataSource(multiPartFileAttachment);
                attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
                attachmentBodyPart.setFileName(mailRequest.getAttachmentName());
                multiPartMessage.addBodyPart(attachmentBodyPart);
            }

            message.setContent(multiPartMessage);

            Transport.send(message);
            log.info("Mail successfully sent to: " + mailRequest.getMailTo());

        } catch (AddressException ex) {
            log.info(ex.getMessage());
            throw new MailAddressException();
        } catch (MessagingException | IOException ex) {
            log.info(ex.getMessage());
            throw new MailMessageException();
        }
    }
}
