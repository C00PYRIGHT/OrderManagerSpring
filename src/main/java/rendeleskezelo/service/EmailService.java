package rendeleskezelo.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException;
}
