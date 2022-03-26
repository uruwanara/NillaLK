package lk.nilla.services;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
	
	public Boolean sendMail(String recipient, String subject, String template, Map<String, String> params) {
		
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(System.getenv("SMTPUsername"), System.getenv("SMTPPassword"));
		    }
		});

	    try {
	        TemplateService tmpService = new TemplateService("WEB-INF/templates/"+template+".html", params);
	        MimeMessage message = new MimeMessage(session);
	
	        message.setFrom(new InternetAddress("projectnilla@gmail.com"));
	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
	        message.setSubject(subject);
	        message.setContent(tmpService.templated, "text/html");
	
	        Transport.send(message);
	        
	        // TODO -- logs --
	        System.out.println("Email sent to " + recipient + " using template " + template);
	        return true;
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    	// TODO -- logs --
			return false;
	    } catch (PatternSyntaxException  e) {
	    	e.printStackTrace();
	    	// TODO -- logs --
			return false;
	    } catch (IOException e) {
			// TODO -- logs --
			e.printStackTrace();
			return false;
		}
	}

}
