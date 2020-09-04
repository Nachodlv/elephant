package com.lab.elephant.security;

import com.lab.elephant.model.User;
import com.lab.elephant.model.UserDetailsImpl;
import com.lab.elephant.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;
  
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    
    Optional<User> oUser = userRepository.findByEmail(email);
    if (!oUser.isPresent()) {
      throw new UsernameNotFoundException(
              "No user found with email: "+ email);
    }
    return new UserDetailsImpl(oUser.get());
  }
}
