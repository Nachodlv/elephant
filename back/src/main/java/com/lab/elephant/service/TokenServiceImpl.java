package com.lab.elephant.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Base64;

import static com.lab.elephant.security.SecurityConstants.TOKEN_PREFIX;

@Service
public class TokenServiceImpl implements TokenService {

  @Override
  public String getEmailByToken(String token) {

    // get payload encoded
    final DecodedJWT decode = JWT.decode(token.replace(TOKEN_PREFIX, ""));
    String payload = decode.getPayload();

    // decode payload to get the email
    String[] body = new String(Base64.getDecoder().decode(payload)).split("\"");

    return body[3]; // The email
  }

}
