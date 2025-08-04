package org.rentfriend.controller;


import jakarta.validation.Valid;
import org.rentfriend.entity.MyUser;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.MyUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/")
public class MyUserController {
  final UserRepository userRepository;
  final PasswordEncoder passwordEncoder;
  String roleRegex;

  public MyUserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          String roleRegex) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.roleRegex = roleRegex;
  }


  @PostMapping("/signup/{role}")
  ResponseEntity<Void> createUser(@Valid @RequestBody MyUserRequest user,
                                  @PathVariable("role") String role) {

    if (role.matches(roleRegex)) {
      Optional<MyUser> myUser = userRepository.findMyUserByUsernameOrEmail(user.username(), user.email());
      if (myUser.isPresent()) {
        return ResponseEntity.badRequest().build();

      }
      else{
        MyUser myUserEntity = new MyUser();
        myUserEntity.setUsername(user.username());
        myUserEntity.setEmail(user.email());
        myUserEntity.setPassword(passwordEncoder.encode(user.password()));
        myUserEntity.setRole(role);
        userRepository.save(myUserEntity);
        return ResponseEntity.ok().build();
      }


    }
    else{
      return ResponseEntity.badRequest().build();
    }

  }
}
