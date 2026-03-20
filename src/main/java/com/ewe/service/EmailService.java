package com.ewe.service;

import java.util.Date;

import javax.mail.MessagingException;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;


public interface EmailService {

	String sendEmailWithTemplate(String to, String name);

	void sendOTPEmail(String otp);

	void sendPasswordUpdateSuccessEmail(String email, String name,String userName) throws MessagingException;

	
	void sendTicketStatusUpdateEmail(String chargerId, String createdBy, String status, String ticketId, String comment)
			throws MessagingException;

	void sendTicketCreationEmail(String email, String string, String ticketId, String issue, String category,
			Date createdDate) throws MessagingException;

	void sendPasswordResetEmail(String email, String name, String resetLink) throws MessagingException;


}
