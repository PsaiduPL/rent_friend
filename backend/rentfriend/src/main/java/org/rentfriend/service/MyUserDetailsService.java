package org.rentfriend.service;

import org.rentfriend.entity.MyUser;
import org.rentfriend.repository.UserRepository;
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

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<MyUser> myUser = userRepository.findMyUserByUsername(username);
    if (myUser.isPresent()) {
      var user = myUser.get();

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
