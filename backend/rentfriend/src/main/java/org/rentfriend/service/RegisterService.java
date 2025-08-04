package org.rentfriend.service;

import org.rentfriend.entity.MyUser;
import org.rentfriend.exception.BadRoleException;
import org.rentfriend.exception.UserExistsException;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.MyUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterService {
  final UserRepository userRepository;
  final PasswordEncoder passwordEncoder;
  String roleRegex;

  public RegisterService(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          String roleRegex) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.roleRegex = roleRegex;
  }

  public void registerUser(MyUserRequest user, String role){
    if (role.matches(roleRegex)) {
      Optional<MyUser> myUser = userRepository.findTopByUsernameOrEmailAndRole(user.username(), user.email().toLowerCase(),role);
      if (myUser.isPresent()) {
        throw new UserExistsException("User with this email/username already exists");
      }
      else{
        MyUser myUserEntity = new MyUser();
        myUserEntity.setUsername(user.username());
        myUserEntity.setEmail(user.email().toLowerCase());
        myUserEntity.setPassword(passwordEncoder.encode(user.password()));
        myUserEntity.setRole(role);
        userRepository.save(myUserEntity);
       return;
      }


    }
    else{
      throw new BadRoleException("Bad role entered");
    }
  }
}
