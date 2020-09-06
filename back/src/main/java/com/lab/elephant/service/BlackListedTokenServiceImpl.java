package com.lab.elephant.service;

import com.lab.elephant.model.BlackListedToken;
import com.lab.elephant.repository.BlackListedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlackListedTokenServiceImpl implements BlackListedTokenService {
  
  @Autowired
  private BlackListedTokenRepository repository;
  
  @Override
  public List<BlackListedToken> findAllTokens() {
    return repository.findAll();
  }
  
  @Override
  public BlackListedToken addToken(BlackListedToken token) {
    return repository.save(token);
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
  
  public BlackListedToken addToken(String token) {
    return addToken(new BlackListedToken(token));
  }
}
