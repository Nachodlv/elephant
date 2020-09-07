package com.lab.elephant.service;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {

  String getEmailByToken(String token);

}
