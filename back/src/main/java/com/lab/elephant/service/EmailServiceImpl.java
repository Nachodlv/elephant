package com.lab.elephant.service;

import com.lab.elephant.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Component
public class EmailServiceImpl implements EmailService {
  
  @Autowired
  private JavaMailSender emailSender;
  
  @Override
  public void sendSimpleEmail(User user) {
    final String subject = "Welcome to Elephant " + user.getFirstName() + " " + user.getLastName();
    final String text = getHtml();
    sendEmail(user.getEmail(), subject, text);
  }
  
  private String getHtml() {
    StringBuilder data = new StringBuilder();
    try {
      File myObj = new File("src/main/resources/verification-email.html");
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        data.append(myReader.nextLine());
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("File could not be read.");
      e.printStackTrace();
    }
    return data.toString();
  }
  
  private void sendEmail(String to, String subject, String text) {
    final MimeMessage message = emailSender.createMimeMessage();
    final MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
    try {
      helper.setFrom("johnfromelephant@gmail.com");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(text, true);
      emailSender.send(message);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
