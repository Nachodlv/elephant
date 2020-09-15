package com.lab.elephant.repository;

import com.auth0.jwt.JWT;
import com.lab.elephant.model.BlackListedToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.lab.elephant.security.SecurityConstants.EXPIRATION_TIME;
import static com.lab.elephant.security.SecurityConstants.SECRET;
import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BlackListedTokenRepositoryTest {
  
  @Autowired
  private BlackListedTokenRepository tokenRepository;
  
  @Before
  public void deleteDB() {
    tokenRepository.deleteAll();
  }
  
  @Test
  public void findByToken_WhenTokenDoesExist() {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    BlackListedToken bToken = new BlackListedToken(token);
    tokenRepository.save(bToken);
    assertThat(tokenRepository.findByToken(token).isPresent()).isTrue();
    assertThat(tokenRepository.findByToken(token).get().getToken()).isEqualTo(bToken.getToken());
  }
  
  @Test
  public void findByToken_WhenTokenDoesNotExist() {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    assertThat(tokenRepository.findByToken(token).isPresent()).isFalse();
  }
}
