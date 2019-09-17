package sg.edu.nus.comp.cs3219.viz.config;

public class MailConfiguration {

    private String smtpHost = "<SMTP host>";

    private int smtpPort = 587;

    private String smtpUsername = "<SMTP username>";

    private String smtpPassword = "<SMTP password>";

    private String mailSmtpAuth = "true";

    private String mailSmtpStartTlsEnable = "true";

    public String getSmtpHost() {
        return smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public String getMailSmtpAuth() {
        return mailSmtpAuth;
    }

    public String getMailSmtpStartTlsEnable() {
        return mailSmtpStartTlsEnable;
    }
}
