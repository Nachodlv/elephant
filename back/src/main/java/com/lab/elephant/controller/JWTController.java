package com.lab.elephant.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.lab.elephant.security.SecurityConstants.HEADER_STRING;
import static com.lab.elephant.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping(path = "/token")
public class JWTController {

  @GetMapping(path = "/verify")
  public boolean verifyToken(HttpServletRequest request) {
    String token = request.getHeader(HEADER_STRING);
    if (token != null) {
      final DecodedJWT decode = JWT.decode(token.replace(TOKEN_PREFIX, ""));
      return decode.getExpiresAt().after(new Date());
    }
    return false;
  }
}
