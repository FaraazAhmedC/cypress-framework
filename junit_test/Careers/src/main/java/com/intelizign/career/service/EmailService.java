package com.intelizign.career.service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.intelizign.career.model.Job;
import com.intelizign.career.model.PasswordResetToken;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.repository.PasswordResetTokenRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	//    public void sendRecruiterCreationEmail(String recruiterEmail, String recruiterName, String recruiterPassword) {
	//        // Create the email body
	//        String loginUrl = "https://careers-dev.izserver24.in/login"; // Adjust to your login URL
	//        String subject = "Your Recruiter Account Details";
	//        String body = String.format("Hello %s,\n\n"
	//                        + "Your recruiter account has been created.\n\n"
	//                        + "Login URL :  %s\n"
	//                        + "Username (email) :  %s\n"
	//                        + "Password :  %s\n\n"
	//                        + "Regards,\nAdmin Team",
	//                recruiterName, loginUrl, recruiterEmail, recruiterPassword);
	//
	//        // Create the email message
	//        SimpleMailMessage message = new SimpleMailMessage();
	//        message.setFrom("vigneshwaran.n@intelizign.com"); // Adjust to your sender email
	//        message.setTo(recruiterEmail);
	//        message.setSubject(subject);
	//        message.setText(body);
	//
	//        // Send the email
	//        javaMailSender.send(message);
	//    }
	//    
	//    public void sendRecruiterCreationEmail(String recruiterEmail, String recruiterName, String recruiterPassword) {
	//        // Generate a secure token for password reset
	//        String token = UUID.randomUUID().toString();
	//        
	//        // Save the token and associate it with the recruiter in the database
	//        PasswordResetToken passwordResetToken = new PasswordResetToken(token, recruiterEmail);
	//        passwordResetTokenRepository.save(passwordResetToken); // Assuming you have a repository for the token
	//        
	//        // Create the reset URL
	//        String resetUrl = "https://careers-dev.izserver24.in/reset-password?token=          " + token; // Adjust the URL as needed
	//        String subject = "Your Recruiter Account Details";
	//        
	//        String body = String.format("Hello %s,\n\n"
	//                        + "Your recruiter account has been created.\n\n"
	//                        + "Click the link below to reset your password:\n"
	//                        + "Reset Password: %s\n\n"
	//                        + "If you did not request a password reset, please ignore this email.\n\n"
	//                        + "Regards,\nAdmin Team",
	//                recruiterName, resetUrl);
	//
	//        // Create the email message
	//        SimpleMailMessage message = new SimpleMailMessage();
	//        message.setFrom("vigneshwaran.n@intelizign.com"); // Adjust to your sender email
	//        message.setTo(recruiterEmail);
	//        message.setSubject(subject);
	//        message.setText(body);
	//
	//        // Send the email
	//        javaMailSender.send(message);
	//    }


//	public void sendRecruiterCreationEmail(String recruiterEmail, String recruiterName, String recruiterPassword) {
//		String subject = "Your Recruiter Account Details";
//		String body = "";
//		String token = UUID.randomUUID().toString();
//		String loginUrl = "https://careers-dev.izserver24.in/login";
//		String resetUrl = "https://careers-dev.izserver24.in/password/reset?token=" + token;
//
//		PasswordResetToken passwordResetToken = new PasswordResetToken(token, recruiterEmail);
//		passwordResetTokenRepository.save(passwordResetToken);
//		
//		body = String.format("Hello %s,\n\n"
//				+ "Your recruiter account has been created.\n\n"
//				+ "Login URL :  %s\n"
//				+ "Username (email) :  %s\n"
//				+ "Password :  %s\n\n"
//				+ "Click the link below to reset your password:\n\n"
//				+ "RESET LINK WILL EXPIRES IN 10 DAYS..!! \n\n"
//				+ "Reset Password: %s\n\n"
//				+ "Regards,\nAdmin Team",
//				recruiterName, loginUrl, recruiterEmail, recruiterPassword,resetUrl);
//		
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setFrom("vigneshwaran.n@intelizign.com");
//		message.setTo(recruiterEmail);
//		message.setSubject(subject);
//		message.setText(body);
//
//		javaMailSender.send(message);
//	}

	
	public void sendRecruiterCreationEmail(String recruiterEmail, String recruiterName, String recruiterPassword) throws MessagingException {
	    String subject = "Your Recruiter Account Details";
	    String bodyPlainText = "";
	    String bodyHtml = "";
	    String token = UUID.randomUUID().toString();
	    String loginUrl = "https://careers-dev.izserver24.in/login";
	    String resetUrl = "https://careers-dev.izserver24.in/password/reset?token=" + token;

	    PasswordResetToken passwordResetToken = new PasswordResetToken(token, recruiterEmail);
	    passwordResetTokenRepository.save(passwordResetToken);
	    
	    // Plain text version of the email body
	    bodyPlainText = String.format("Hello %s,\n\n"
	            + "Your recruiter account has been created.\n\n"
	            + "Login URL: %s\n"
	            + "Username (email): %s\n"
	            + "Password: %s\n\n"
	            + "Click the link below to reset your password:\n\n"
	            + "Reset Password: %s\n\n"
	            + "Regards,\nAdmin Team",
	            recruiterName, loginUrl, recruiterEmail, recruiterPassword, resetUrl);

	    // HTML version of the email body
	    bodyHtml = String.format("<p>Hello %s,</p><br>"
	            + "<p>Your recruiter account has been created.</p><br>"
	            + "<p>Login URL: <a href='%s'>Login here</a></p><br>"
	            + "<p>Username (email): %s</p><br>"
	            + "<p>Password: %s</p><br>"
	            + "<h1>RESET LINK WILL EXPIRES IN 10 DAYS..!!</h1><br>"
	            + "<p>Reset Password: <a href='%s'>Click here to reset</a></p><br>"
	            + "<p>Regards,<br>Admin Team</p>",
	            recruiterName, loginUrl, recruiterEmail, recruiterPassword, resetUrl);
	    
	    MimeMessage message = mailSender.createMimeMessage();
	    
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
	    
	    helper.setFrom("vigneshwaran.n@intelizign.com");
	    helper.setTo(recruiterEmail);
	    helper.setSubject(subject);
	    
	    // Set both plain text and HTML content
	    helper.setText(bodyPlainText, false);  // Plain text part
	    helper.setText(bodyHtml, true);        // HTML part

	    javaMailSender.send(message);
	}

	public void sendJobPostedNotification(Recruiter recruiter, Job job) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(recruiter.getEmail());
		message.setSubject("Job Posted Successfully");
		message.setText("Hello " + recruiter.getName() + ",\n\n" +
				"Your job titled '" + job.getJobTitle() + "' has been posted successfully.\n\n" +
				"Best regards,\nYour Company");

		mailSender.send(message);
	}

	public void sendJobStatusEmail(String to, String jobTitle, String status) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom("mail_id");
			helper.setTo(to);
			helper.setSubject("Job Status Update: " + jobTitle);
			helper.setText("Dear Recruiter,\n\nYour job posting '" + jobTitle + "' has been " + status.toLowerCase() + ".\n\nThank you!");

			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send email: " + e.getMessage());
		}
	}
}
