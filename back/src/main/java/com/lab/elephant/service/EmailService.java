package com.lab.elephant.service;

import com.lab.elephant.model.User;

public interface EmailService {
  void sendWelcomeEmail(User user);
  void sendUpdatedPasswordEmail(User user);
}
