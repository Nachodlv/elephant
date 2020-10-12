package com.lab.elephant.security;

import com.lab.elephant.model.EmptyJavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
@PropertySource("classpath:/email.properties")
public class EmailConfig {
  
  @Value("${spring.mail.host}")
  private String host;
  @Value("${spring.mail.port}")
  private int port;
  @Value("${spring.mail.username}")
  private String userName;
  @Value("${spring.mail.password}")
  private String passwd;
  
  @Bean
  public JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(userName);
    mailSender.setPassword(passwd);
    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.from.email", userName);
    props.put("mail.debug", "true");
    // creates a new session with an authenticator
    Authenticator auth = new Authenticator() {
      public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, passwd);
      }
    };
    Session session = Session.getInstance(props, auth);
    mailSender.setSession(session);
  
    try {
      mailSender.testConnection();
    } catch (MessagingException e) {
      //if mailSender can't connect we return an Empty Email Sender.
      return new EmptyJavaMailSender();
    }
    return mailSender;
  }
}
