package com.lab.elephant.security;

import com.lab.elephant.model.User;
import com.lab.elephant.model.UserDetailsImpl;
import com.lab.elephant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service("userDetailsService")
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;
  //
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    
    Optional<User> oUser = userRepository.findByEmail(email);
    if (!oUser.isPresent()) {
      throw new UsernameNotFoundException(
              "No user found with email: "+ email);
    }
    return new UserDetailsImpl(oUser.get());
  }
}
