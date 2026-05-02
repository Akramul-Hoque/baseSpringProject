package org.averdev.basepeoject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (!emailEnabled) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        if (!emailEnabled || templateEngine == null) {
            return;
        }

        try {
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process(templateName, context);
            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send template email", e);
        }
    }

    public void sendWelcomeEmail(String to, String username) {
        Map<String, Object> variables = Map.of(
            "username", username,
            "email", to
        );
        
        if (templateEngine != null) {
            sendTemplateEmail(to, "Welcome to Spring Boot Base Project", "welcome", variables);
        } else {
            String text = String.format(
                "Welcome %s!\n\n" +
                "Thank you for registering with Spring Boot Base Project.\n\n" +
                "Best regards,\n" +
                "The Development Team",
                username
            );
            sendSimpleEmail(to, "Welcome to Spring Boot Base Project", text);
        }
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        Map<String, Object> variables = Map.of(
            "resetToken", resetToken,
            "email", to
        );
        
        if (templateEngine != null) {
            sendTemplateEmail(to, "Password Reset Request", "password-reset", variables);
        } else {
            String text = String.format(
                "Hello,\n\n" +
                "You requested a password reset. Use the following token: %s\n\n" +
                "This token will expire in 24 hours.\n\n" +
                "Best regards,\n" +
                "The Development Team",
                resetToken
            );
            sendSimpleEmail(to, "Password Reset Request", text);
        }
    }

    public void sendAccountLockedEmail(String to, String username) {
        Map<String, Object> variables = Map.of(
            "username", username,
            "email", to
        );
        
        if (templateEngine != null) {
            sendTemplateEmail(to, "Account Locked", "account-locked", variables);
        } else {
            String text = String.format(
                "Hello %s,\n\n" +
                "Your account has been locked due to multiple failed login attempts.\n\n" +
                "Please contact support if you believe this is an error.\n\n" +
                "Best regards,\n" +
                "The Development Team",
                username
            );
            sendSimpleEmail(to, "Account Locked", text);
        }
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }
}
