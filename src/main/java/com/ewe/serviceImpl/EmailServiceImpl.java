package com.ewe.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import com.ewe.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
	@Autowired
    private JavaMailSender javaMailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.name}")
    private String adminName;

    @Override
    public String sendEmailWithTemplate(String to, String name) {
        String resetLink = "http://localhost:5173/set-password" ;
        Context context = new Context();
        context.setVariable("name", name);  
        context.setVariable("resetLink", resetLink);

        String content = templateEngine.process("email-template", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(to);
            messageHelper.setSubject("Email Subject");  
            messageHelper.setText(content, true);  
            javaMailSender.send(mimeMessage);
            return "Email sent successfully!";
        } 
        catch (MessagingException e) {
            e.printStackTrace();
            return "failed to send email";
        }
    }
    
    @Override
    public void sendPasswordResetEmail(String email, String name, String resetLink) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject("Password Reset Request - Avyaya E-We Power Solutions");
        
        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("resetLink", resetLink);
        
        // Process the template
        String htmlContent = templateEngine.process("reset-password-link", context);
        helper.setText(htmlContent, true);
        javaMailSender.send(message);

    }
    
    @Override
    public void sendOTPEmail(String otp) { 
        try {
           
            if (adminEmail == null || adminEmail.contains("$")) {
                throw new IllegalStateException("Admin email not properly configured");
            }

            Context context = new Context();
            context.setVariable("name", adminName);
            context.setVariable("otp", otp);

            String content = templateEngine.process("delete-template", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(adminEmail); // Use the injected value
            messageHelper.setSubject("Whitelabel Deletion Verification - Avyaya E-We Power Solutions Pvt Ltd");
            messageHelper.setText(content, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to " + adminEmail, e);
        }
    }
    
    @Override
    public void sendPasswordUpdateSuccessEmail(String email, String name,String userName) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        // Set email details
        helper.setFrom(fromEmail);
        helper.setTo(email);
        helper.setSubject("Password Updated Successfully - Avyaya E-We Power Solutions");
       
       String password="passowrd";
        // Create thymeleaf context and add variables
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("password", password);
        context.setVariable("username", userName);
        
        // Process the HTML template
        String htmlContent = templateEngine.process("set-password", context);
        helper.setText(htmlContent, true);
        
        // Send the email
        javaMailSender.send(message);
    }
    
    @Override
    public void sendTicketStatusUpdateEmail(String chargerId, String createdBy, 
                                            String status,  String ticketId,
                                            String comment) throws MessagingException {
        
        // Prepare the context with template variables
        Context context = new Context();
        context.setVariable("name", createdBy);
        context.setVariable("ticketId", ticketId);
        context.setVariable("ticketSubject", comment);
        context.setVariable("status", status);
        context.setVariable("resolutionDate", new SimpleDateFormat("MMM dd, yyyy").format(new Date()));
        
        // Process the HTML template
        String htmlContent = templateEngine.process("issues-template", context);
        
        // Create and configure the email message
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(chargerId); // Assuming chargerId is actually the email
        helper.setSubject("Ticket #" + ticketId + " has been " + status + " - Avyaya E-We Power Solutions");
        helper.setText(htmlContent, true);
        
        // Send the email
        javaMailSender.send(message);
    }
    
    @Override
    public void sendTicketCreationEmail(
        String toEmail,
        String name,
        String ticketId,
        String issueDescription,
        String category,
        Date createdDate
    ) throws MessagingException {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("ticketId", ticketId);
            context.setVariable("ticketSubject", issueDescription);
            context.setVariable("status", "created");
            context.setVariable("createdDate", new SimpleDateFormat("MMM dd, yyyy").format(createdDate));
            context.setVariable("category", category);
            
            String htmlContent = templateEngine.process("ticket-creation-email", context);
            
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("New Ticket Created - " + ticketId);
            helper.setText(htmlContent, true);
            
            javaMailSender.send(message);
            logger.info("Ticket creation email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send ticket creation email to: {}", toEmail, e);
            throw new MessagingException("Failed to send ticket creation email", e);
        }
    }
}