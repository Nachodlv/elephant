package com.lab.elephant.model;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

public class EmptyJavaMailSender implements JavaMailSender {
  @Override
  public MimeMessage createMimeMessage() {
    return null;
  }
  
  @Override
  public MimeMessage createMimeMessage(InputStream inputStream) throws MailException {
    return null;
  }
  
  @Override
  public void send(MimeMessage mimeMessage) throws MailException {
  
  }
  
  @Override
  public void send(MimeMessage... mimeMessages) throws MailException {
  
  }
  
  @Override
  public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
  
  }
  
  @Override
  public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
  
  }
  
  @Override
  public void send(SimpleMailMessage simpleMailMessage) throws MailException {
  
  }
  
  @Override
  public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
  
  }
}
