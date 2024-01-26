package com.coffee.house.library.email;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImp implements EmailService {
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private TemplateEngine htmlTemplateEngine;
	
	@Value("${spring.mail.verify.host}")
    private String host;
	
    @Value("${spring.mail.username}")
    private String fromEmail;
    
   
	@Override
	public void sendEmailHTML(String url, String recipientEmail, Locale locale, String token, 
			String html, String subject) throws MessagingException {
		StringBuilder confirmationUrl = new StringBuilder(); 
		confirmationUrl.append(host).append(url).append("?token=").append(token);
		
		System.out.println("Host : " + host);
		
		System.out.println("fromEmail : " + fromEmail);
		
		String recipientAddress = recipientEmail; // customer.getEmail();
			
		final Context ctx = new Context(locale);
		ctx.setVariable("href", confirmationUrl.toString());

		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
		message.setSubject(subject);
		message.setFrom(fromEmail);
		message.setTo(recipientAddress);

		final String htmlContent = this.htmlTemplateEngine.process(html, ctx);
		message.setText(htmlContent, true /* isHtml */);

		javaMailSender.send(mimeMessage);
	}
}
