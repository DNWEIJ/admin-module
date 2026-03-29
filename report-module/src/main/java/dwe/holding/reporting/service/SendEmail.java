package dwe.holding.reporting.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SendEmail {

    private final JavaMailSender mailSender;

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        sendHtmlEmail(to, subject, htmlContent, null);
    }

    public boolean sendHtmlEmail(String to, String subject, String htmlContent, String from)  {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            if (from != null && !from.isEmpty()) {
                helper.setFrom(from);
            }
            String plainText = htmlToPlainText(htmlContent);
            helper.setText(plainText, htmlContent);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            log.error("cannot send email message" + e.getMessage(), e);
        }
        return false;
    }

    public void sendHtmlEmail(String[] to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);

        String plainText = htmlToPlainText(htmlContent);
        helper.setText(plainText, htmlContent);

        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String[] cc, String[] bcc, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        if (cc != null && cc.length > 0) {
            helper.setCc(cc);
        }
        if (bcc != null && bcc.length > 0) {
            helper.setBcc(bcc);
        }
        helper.setSubject(subject);

        String plainText = htmlToPlainText(htmlContent);
        helper.setText(plainText, htmlContent);

        mailSender.send(message);
    }

    private String htmlToPlainText(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }

        // Parse HTML
        org.jsoup.nodes.Document doc = Jsoup.parse(html);

        // Convert <br> to newlines before stripping tags
        doc.select("br").append("\\n");
        doc.select("p").append("\\n\\n");
        doc.select("div").append("\\n");
        doc.select("h1, h2, h3, h4, h5, h6").append("\\n\\n");
        doc.select("li").prepend("- ");
        doc.select("li").append("\\n");

        String text = doc.text();
        text = text.replaceAll("\\\\n", "\n");
        text = text.replaceAll("\n{3,}", "\n\n");

        return text.trim();
    }
}
