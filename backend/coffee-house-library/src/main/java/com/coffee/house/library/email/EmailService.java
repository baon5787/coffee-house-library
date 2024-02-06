package com.coffee.house.library.email;

import java.util.Locale;

import jakarta.mail.MessagingException;



public interface EmailService {
	void sendEmailHTML(String url, String recipientEmail, Locale locale, String token,
			String html, String subject) throws MessagingException;
}
