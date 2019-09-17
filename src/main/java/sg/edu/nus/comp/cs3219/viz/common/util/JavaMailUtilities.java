package sg.edu.nus.comp.cs3219.viz.common.util;

import sg.edu.nus.comp.cs3219.viz.config.MailConfiguration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

public class JavaMailUtilities {

    private MailConfiguration mailConfiguration;

    public JavaMailUtilities() {
        this.mailConfiguration = new MailConfiguration();
    }

    private Properties getJavaMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailConfiguration.getSmtpHost());
        properties.put("mail.smtp.port", Integer.toString(mailConfiguration.getSmtpPort()));
        properties.put("mail.smtp.auth", mailConfiguration.getMailSmtpAuth());
        properties.put("mail.smtp.starttls.enable", mailConfiguration.getMailSmtpStartTlsEnable());

        return properties;
    }

    public Session getJavaMailSession() {
        Session session = Session.getInstance(getJavaMailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConfiguration.getSmtpUsername(), mailConfiguration.getSmtpPassword());
            }
        });
        // So that it works for local development server for debugging purposes
        session.setProtocolForAddress("rfc822", "smtp");
        return session;
    }
}
