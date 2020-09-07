package com.lab.elephant.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lab.elephant.model.BlackListedToken;
import com.lab.elephant.repository.BlackListedTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BlackListedTokenServiceImpl implements BlackListedTokenService {
  
  private final BlackListedTokenRepository repository;
  
  public BlackListedTokenServiceImpl(BlackListedTokenRepository repository) {
    this.repository = repository;
  }
  
  @Override
  public List<BlackListedToken> findAllTokens() {
    return repository.findAll();
  }
  
  @Override
  public BlackListedToken addToken(BlackListedToken token) {
    return findToken(token.getToken()).isPresent() ? null : repository.save(token);
  }
  
  @Override
  public Optional<BlackListedToken> findTokenById(long id) {
    return repository.findById(id);
  }
  
  @Override
  public Optional<BlackListedToken> findToken(String token) {
    return repository.findByToken(token);
  }
  
  @Override
  public void delete(BlackListedToken token) {
    final Optional<BlackListedToken> byId = repository.findById(token.getUuid());
    if (byId.isPresent()) {
      repository.delete(token);
    }
  }
  
  @Override
  public void update() {
    //the idea is that we remove expired tokens from the DB
    final List<BlackListedToken> allTokens = findAllTokens();
    for (BlackListedToken t : allTokens) {
      final DecodedJWT decode = JWT.decode(t.getToken());
      if (decode.getExpiresAt().before(new Date())) {
        delete(t);
      }
    }
  }
  
  public BlackListedToken addToken(String token) {
    return addToken(new BlackListedToken(token));
  }
}
