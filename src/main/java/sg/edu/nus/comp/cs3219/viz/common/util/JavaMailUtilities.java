package sg.edu.nus.comp.cs3219.viz.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Component
public class JavaMailUtilities {

    @Value("${smtpconfiguration.mail.host}")
    private String smtpHost;

    @Value("${smtpconfiguration.mail.port}")
    private String smtpPort;

    @Value("${smtpconfiguration.mail.username}")
    private String smtpUsername;

    @Value("${smtpconfiguration.mail.password}")
    private String smtpPassword;

    @Value("${smtpconfiguration.mail.properties.mail.smtp.auth}")
    private String mailSmtpAuth;

    @Value("${smtpconfiguration.mail.properties.mail.smtp.starttls.enable}")
    private String mailSmtpStartTlsEnable;

    private Properties getJavaMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", mailSmtpAuth);
        properties.put("mail.smtp.starttls.enable", mailSmtpStartTlsEnable);

        return properties;
    }

    public Session getJavaMailSession() {
        Session session = Session.getInstance(getJavaMailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });
        // So that it works for local development server for debugging purposes
        session.setProtocolForAddress("rfc822", "smtp");
        return session;
    }
}
