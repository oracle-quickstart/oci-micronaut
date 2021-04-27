package newsletter.subscription;

import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Singleton
public class JavaMailSender implements MailSender {
    private final MailConfiguration configuration;
    private final String senderEmail;

    public JavaMailSender(
            @Property(name = "approved.sender.email") String senderEmail,
            MailConfiguration configuration) {
        this.configuration = configuration;
        this.senderEmail = senderEmail;
    }

    @Override
    public String send(String to, String subject, String body) {
        try {
            final Session mailSession = createSession();
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Hello from Mushop");
            String msg = "Thanks for confirming your <b>subscription</b>!";

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            message.saveChanges();
            final String messageID = message.getMessageID();

            final Transport transport = mailSession.getTransport();
            try {
                transport.connect(configuration.getHost(), configuration.getUser(), configuration.getPassword());
                transport.sendMessage(message, message.getAllRecipients());
                return messageID;
            } finally {
                transport.close();
            }
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending message: " + e.getMessage(), e);
        }
    }

    private Session createSession() {
        Properties prop = new Properties();
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        prop.put("mail.smtp.starttls.required", "true");
        prop.put("mail.smtp.auth.login.disable", "true");
        prop.put("mail.smtp.host", configuration.getHost());
        prop.put("mail.smtp.port", String.valueOf(configuration.getPort()));
        prop.put("mail.smtp.ssl.trust", configuration.getHost());
        return  Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(configuration.getUser(), configuration.getPassword());
            }
        });
    }
}
