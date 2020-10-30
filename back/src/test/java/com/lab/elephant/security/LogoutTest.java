package com.lab.elephant.security;

import com.auth0.jwt.JWT;
import com.lab.elephant.model.BlackListedToken;
import com.lab.elephant.service.BlackListedTokenServiceImpl;
import com.lab.elephant.service.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.lab.elephant.security.SecurityConstants.EXPIRATION_TIME;
import static com.lab.elephant.security.SecurityConstants.SECRET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(JWTLogoutHandler.class)
 public class LogoutTest {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private BlackListedTokenServiceImpl tokenService;
  //MockBeans bellow are not used but are needed for the test to run
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private UserDetailsServiceImpl userDetailsService;
  
  @Test
  public void logoutWithToken_ShouldReturn_200() throws Exception {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    mvc.perform(post("/logout").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
  }
  
  @Test
  public void logoutWithoutToken_ShouldReturn_403() throws Exception {
    mvc.perform(post("/logout"))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("No Token to disable"));
  }
  @Test
  public void logoutWithExpiredToken_ShouldReturn_403() throws Exception {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() - EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    
    mvc.perform(post("/logout").header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden())
            .andExpect(status().reason("Token is already expired"));
  }
  
  @Test
  public void logoutWithBlackListedToken_ShouldReturn_200() throws Exception {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    Mockito.when(tokenService.findToken(token)).thenReturn(Optional.of(new BlackListedToken(token)));
    mvc.perform(post("/logout").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
  }
}
