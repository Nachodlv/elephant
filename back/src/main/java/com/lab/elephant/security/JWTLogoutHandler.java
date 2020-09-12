package com.lab.elephant.security;

import com.auth0.jwt.JWT;
import com.lab.elephant.service.BlackListedTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.lab.elephant.security.SecurityConstants.HEADER_STRING;
import static com.lab.elephant.security.SecurityConstants.TOKEN_PREFIX;

public class JWTLogoutHandler implements LogoutHandler {
  
  @Autowired
  private BlackListedTokenServiceImpl tokenService;
  
  @Override
  public void logout(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
    String token = req.getHeader(HEADER_STRING);

    if (token == null || !token.startsWith(TOKEN_PREFIX)) {
      sendError(res, "No Token to disable");
    } else {
      token = token.replace(TOKEN_PREFIX, "");
      //if the token is expired
      if (JWT.decode(token).getExpiresAt().before(new Date())) {
        sendError(res, "Token is already expired");
      }
      tokenService.addToken(token);
      tokenService.update();
      res.setStatus(HttpServletResponse.SC_OK);
    }
  }
  
  private void sendError(HttpServletResponse res, String message) {
    try {
      res.sendError(403, message);
    } catch (IOException ignore) {}
  }
}
