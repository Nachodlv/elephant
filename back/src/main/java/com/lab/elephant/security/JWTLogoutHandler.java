package com.lab.elephant.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lab.elephant.model.BlackListedToken;
import com.lab.elephant.service.BlackListedTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.lab.elephant.security.SecurityConstants.HEADER_STRING;
import static com.lab.elephant.security.SecurityConstants.TOKEN_PREFIX;

public class JWTLogoutHandler implements LogoutHandler {
  
  @Autowired
  private BlackListedTokenServiceImpl tokenService;
  
  @Override
  public void logout(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
    String token = req.getHeader(HEADER_STRING);
    final String requestURI = req.getRequestURI();
    if (token == null || !token.startsWith(TOKEN_PREFIX)) {
      try {
        res.sendError(403, "No Token to disable");
      } catch (IOException ignore) {}
    } else {
      //todo agregar catch antes del caso que el token este expirado cuando haces logout
      //lo ignoro y listo
      addTokenToBlackList(token.replace(TOKEN_PREFIX, ""));
      updateBlackList();
      res.setStatus(200);
    }
  }
  
  private void addTokenToBlackList(String token) {
    tokenService.addToken(token);
  }
  
  private void updateBlackList() {
    //the idea is that we remove expired tokens from the DB
    final List<BlackListedToken> allTokens = tokenService.findAllTokens();
    for (BlackListedToken t : allTokens) {
      final DecodedJWT decode = JWT.decode(t.getToken());
      if (decode.getExpiresAt().before(new Date())) {
        tokenService.delete(t);
      }
    }
  }
}
