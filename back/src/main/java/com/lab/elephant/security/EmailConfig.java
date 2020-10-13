package com.lab.elephant.security;

import com.lab.elephant.model.EmptyJavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
@PropertySource(value = "classpath:/email.properties", ignoreResourceNotFound = true)
public class EmailConfig {
  
  @Value("${spring.mail.host:emptyHost}")
  private String host;
  @Value("${spring.mail.port:-1}")
  private int port;
  @Value("${spring.mail.username:emptyUsername}")
  private String userName;
  @Value("${spring.mail.password:emptyPassword}")
  private String passwd;
  
  @Bean
  public JavaMailSender getJavaMailSender() {
    //if file is not found, or one of the values is not found. We return an emptyEmailSender.
    if (host.equals("emptyHost") || port == -1 || userName.equals("emptyUsername") || passwd.equals("emptyPassword"))
      return new EmptyJavaMailSender();
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
    return mailSender;
  }
}
