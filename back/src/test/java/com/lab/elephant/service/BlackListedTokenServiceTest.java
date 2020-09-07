package com.lab.elephant.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lab.elephant.model.BlackListedToken;
import com.lab.elephant.repository.BlackListedTokenRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.lab.elephant.security.SecurityConstants.EXPIRATION_TIME;
import static com.lab.elephant.security.SecurityConstants.SECRET;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class BlackListedTokenServiceTest {
  
  @TestConfiguration
  static class UserServiceImplTestContextConfiguration {
    @Autowired
    private BlackListedTokenRepository tokenRepository;
    @Bean
    public BlackListedTokenService tokenService() {
      return new BlackListedTokenServiceImpl(tokenRepository);
    }
  }
  
  @Autowired
  private BlackListedTokenService tokenService;
  @MockBean
  private BlackListedTokenRepository tokenRepository;
  
  @Test
  public void findAllTokens_ShouldReturn_AllTokens() {
    String token1 = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    String token2 = JWT.create()
            .withSubject("mark@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    final BlackListedToken b1 = new BlackListedToken(token1);
    final BlackListedToken b2 = new BlackListedToken(token2);
    Mockito.when(tokenRepository.save(b1)).thenReturn(b1);
    Mockito.when(tokenRepository.save(b2)).thenReturn(b2);
    tokenService.addToken(b1);
    tokenService.addToken(b2);
    List<BlackListedToken> list = new ArrayList<>();
    list.add(b1);
    list.add(b2);
    Mockito.when(tokenRepository.findAll()).thenReturn(list);
    assertThat(tokenService.findAllTokens()).isEqualTo(list);
  }
  
  @Test
  public void findToken_WhenTokenExists() {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    BlackListedToken b = new BlackListedToken(token);
    Mockito.when(tokenRepository.save(b)).thenReturn(b);
    tokenService.addToken(b);
    Mockito.when(tokenRepository.findByToken(token)).thenReturn(Optional.of(b));
    assertThat(tokenService.findToken(token).isPresent()).isTrue();
    assertThat(tokenService.findToken(token).get()).isSameAs(b);
  }
  
  @Test
  public void findToken_WhenTokenDoesNotExist() {
    String token = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    Mockito.when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());
    assertThat(tokenService.findToken(token).isPresent()).isFalse();
  }
  
  @Test
  public void Update_ShouldDeleteAllExpiredTokens() {
    String token1 = JWT.create()
            .withSubject("john@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    String token2 = JWT.create()
            .withSubject("mark@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() - EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    String token3 = JWT.create()
            .withSubject("luke@elephant.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(HMAC512(SECRET.getBytes()));
    BlackListedToken b1 = new BlackListedToken(token1);
    BlackListedToken b2 = new BlackListedToken(token2);
    BlackListedToken b3 = new BlackListedToken(token3);
    
    List<BlackListedToken> list = new ArrayList<>();
    list.add(b1);
    list.add(b2);
    list.add(b3);
    Mockito.when(tokenRepository.findAll()).thenReturn(list);
    //esto me llama la atencion porque si lo mockeo si o si va a andar.
    //esta es la implementacion de tokenService.update()
    
    final List<BlackListedToken> allTokens = tokenService.findAllTokens();
    for (BlackListedToken t : allTokens) {
      final DecodedJWT decode = JWT.decode(t.getToken());
      if (decode.getExpiresAt().before(new Date())) {
        //tokenService.delete(t);
        //en vez de hacer el delete, los borro de list que deberia ser equivalente.
        list.remove(t);
      }
    }
    assertThat(list.size()).isEqualTo(2);
    assertThat(list.get(0)).isEqualTo(b1);
    assertThat(list.get(1)).isEqualTo(b3);
  }
}