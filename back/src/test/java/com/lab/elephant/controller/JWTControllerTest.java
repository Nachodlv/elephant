package com.lab.elephant.controller;


import com.auth0.jwt.JWT;
import com.lab.elephant.model.BlackListedToken;
import com.lab.elephant.security.UserDetailsServiceImpl;
import com.lab.elephant.service.BlackListedTokenServiceImpl;
import com.lab.elephant.service.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Date;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.lab.elephant.security.SecurityConstants.EXPIRATION_TIME;
import static com.lab.elephant.security.SecurityConstants.SECRET;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(JWTController.class)
public class JWTControllerTest {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private BlackListedTokenServiceImpl tokenService;
  // UserServiceImpl, UserDetailsServiceImpl and BCryptPasswordEncoder
  // are not used but are needed for the tests to run.
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private UserDetailsServiceImpl userDetailsService;
  @MockBean
  private BCryptPasswordEncoder passwordEncoder;
  
  @Test
  public void verifyValidToken_shouldReturnTrue() throws Exception {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    mvc.perform(get("/token/verify").header("Authorization", "Bearer " + token))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(content().string("true"))
            .andExpect(status().isOk());
  }
  
  @Test
  public void verifyExpiredToken_shouldReturnFalse() throws Exception {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() - EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    mvc.perform(get("/token/verify").header("Authorization", "Bearer " + token))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(content().string("false"))
            .andExpect(status().isOk());
  }
  
  @Test
  public void verifyBlackListedToken_shouldReturnFalse() throws Exception {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    given(tokenService.findToken(token)).willReturn(Optional.of(new BlackListedToken(token)));
    mvc.perform(get("/token/verify").header("Authorization", "Bearer " + token))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(content().string("false"))
            .andExpect(status().isOk());
  }
}

