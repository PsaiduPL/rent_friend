package org.rentfriend.service;

import lombok.extern.log4j.Log4j;
import org.rentfriend.entity.MyUser;
import org.rentfriend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

  final UserRepository userRepository;

  MyUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  Logger log = LoggerFactory.getLogger(MyUserDetailsService.class);
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.info("Sprawdzam uzytkownika");//TODO sprawdz te fetchowanie czemu on wykonuje 3 zapytania
    var myUser = userRepository.findUserByUsername(username);
    if (myUser.isPresent()) {
      var user = myUser.get();
      log.info("Uzytkownik istnieje");
      return User.builder()
          .username(user.getUsername())
          .password(user.getPassword())
          .roles(user.getRole())
          .build();
    }
    else {
      throw new UsernameNotFoundException("User with username "+ username +" not found");
    }
  }
}
