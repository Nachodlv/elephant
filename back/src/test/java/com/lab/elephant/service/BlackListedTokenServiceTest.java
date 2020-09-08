package com.lab.elephant.service;

import com.auth0.jwt.JWT;
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
    
    final List<BlackListedToken> removedTokens = new ArrayList<>();
    Mockito.doAnswer(invocationOnMock -> {
      BlackListedToken object = invocationOnMock.getArgument(0);
      removedTokens.add(object);
      return null;
    }).when(tokenRepository).delete(Mockito.any());
    Mockito.when(tokenRepository.findAll()).thenReturn(list);
    Mockito.when(tokenRepository.findById(b1.getUuid())).thenReturn(Optional.of(b1));
    Mockito.when(tokenRepository.findById(b2.getUuid())).thenReturn(Optional.of(b2));
    Mockito.when(tokenRepository.findById(b3.getUuid())).thenReturn(Optional.of(b3));
    
    tokenService.update();
    
    assertThat(removedTokens.size()).isEqualTo(1);
    assertThat(removedTokens.get(0)).isEqualTo(b2);
  }
}