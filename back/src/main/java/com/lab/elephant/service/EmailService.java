package com.lab.elephant.service;

import com.lab.elephant.model.User;

public interface EmailService {
  void sendSimpleEmail(User user);
}
