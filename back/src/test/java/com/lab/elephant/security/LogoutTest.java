package com.lab.elephant.security;

import com.auth0.jwt.JWT;
import com.lab.elephant.service.BlackListedTokenServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

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
  private UserDetailsServiceImpl userDetailsService;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;
  // BlackListedTokenServiceImpl is not used but is needed for the test to run
  @MockBean
  private BlackListedTokenServiceImpl tokenService;
  
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
}