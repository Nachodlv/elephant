package com.lab.elephant.service;

import com.lab.elephant.model.BlackListedToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BlackListedTokenService {
  List<BlackListedToken> findAllTokens();
  
  Optional<BlackListedToken> addToken(BlackListedToken token);
  
  Optional<BlackListedToken> findTokenById(long id);
  
  Optional<BlackListedToken> findToken(String token);
  
  void delete(BlackListedToken token);
  
  void update();
}
