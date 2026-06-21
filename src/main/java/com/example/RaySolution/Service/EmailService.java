package com.example.RaySolution.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);

        emailSender.send(message);
    }

    public void sendSupportEmail(String fromName, String fromEmail, String category, String content) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("h14426519@gmail.com");
        helper.setReplyTo(fromEmail);
        helper.setSubject("Support Ticket: " + category + " — from " + fromName);

        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                  <h2 style="color: #2563eb;">New Support Ticket</h2>
                  <table style="width: 100%%; border-collapse: collapse;">
                    <tr>
                      <td style="padding: 8px; font-weight: bold; color: #475569;">From:</td>
                      <td style="padding: 8px;">%s &lt;%s&gt;</td>
                    </tr>
                    <tr>
                      <td style="padding: 8px; font-weight: bold; color: #475569;">Category:</td>
                      <td style="padding: 8px;">%s</td>
                    </tr>
                  </table>
                  <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 16px 0;" />
                  <p style="color: #475569;">%s</p>
                </div>
                """.formatted(fromName, fromEmail, category, content.replace("\n", "<br>"));

        helper.setText(html, true);
        emailSender.send(message);
    }
}
