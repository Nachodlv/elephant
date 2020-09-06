package com.lab.elephant.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.lab.elephant.model.BlackListedToken;
import com.lab.elephant.service.BlackListedTokenServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static com.lab.elephant.security.SecurityConstants.*;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
  private final BlackListedTokenServiceImpl tokenService;
  public JWTAuthorizationFilter(AuthenticationManager authManager, BlackListedTokenServiceImpl tokenService) {
    super(authManager);
    this.tokenService = tokenService;
  }
  
  @Override
  protected void doFilterInternal(HttpServletRequest req,
                                  HttpServletResponse res,
                                  FilterChain chain) throws IOException, ServletException {
    String header = req.getHeader(HEADER_STRING);
    
    //this if ignores the Token
    final String equals = req.getRequestURI();
    if (header == null
            || !header.startsWith(TOKEN_PREFIX)
            || req.getRequestURI().equals(SIGN_UP_URL)
            || req.getRequestURI().equals(TOKEN_VERIFY_URL)
            || req.getRequestURI().equals(LOGOUT_URL)) {
      chain.doFilter(req, res);
      return;
    }
    
    UsernamePasswordAuthenticationToken authentication = getAuthentication(req, res);
    
    SecurityContextHolder.getContext().setAuthentication(authentication);
    chain.doFilter(req, res);
  }
  
  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String token = request.getHeader(HEADER_STRING);
    if (token != null && !isOnBlackList(token)) {
      // parse the token.
      String user;
      try {
        user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
        if (user != null) {
          return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        }
      } catch (TokenExpiredException ignored) {}
      return null;
    }
    return null;
  }
  
  private boolean isOnBlackList(String token) {
    token = token.replace(TOKEN_PREFIX, "");
    final Optional<BlackListedToken> bToken = tokenService.findToken(token);
    return bToken.isPresent();
  }
}
