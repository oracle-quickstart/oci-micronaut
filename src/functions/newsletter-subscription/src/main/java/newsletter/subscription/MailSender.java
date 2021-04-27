package newsletter.subscription;

public interface MailSender {
    String send(String to, String subject, String body);
}
