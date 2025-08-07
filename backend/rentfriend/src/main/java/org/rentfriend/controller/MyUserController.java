package org.rentfriend.controller;


import jakarta.validation.Valid;
import org.rentfriend.dto.UserDTO;
import org.rentfriend.entity.MyUser;
import org.rentfriend.exception.BadRoleException;
import org.rentfriend.exception.UserExistsException;
import org.rentfriend.repository.UserRepository;
import org.rentfriend.requestData.MyUserRequest;
import org.rentfriend.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class MyUserController {
  final RegisterService registerService;
  final UserRepository userRepository;
  public MyUserController(RegisterService registerService,
                          UserRepository userRepository) {
    this.registerService = registerService;
    this.userRepository = userRepository;
  }
  @GetMapping("/user/details")
  ResponseEntity<UserDTO> getUserDetails(Principal principal){
    Optional<MyUser> user = userRepository.findMyUserByUsername(principal.getName());
    if(user.isPresent()){
      UserDTO userDTO = new UserDTO(user.get().getUsername(),user.get().getEmail(),user.get().getRole(),user.get().getProfile());
      return ResponseEntity.ok(userDTO);
    }else{
      return ResponseEntity.notFound().build();
    }
  }


  @PostMapping(value = "/signup/{role}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> createUserFromJson(@Valid @RequestBody MyUserRequest user,
                                                 @PathVariable("role") String role){
   // System.out.println(STR."virtual ---------\{Thread.currentThread().isVirtual()}");
    registerService.registerUser(user,role);
    return ResponseEntity.ok().build();

  }
  @PostMapping(value = "/signup/{role}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<Void> createUserFromForm(@Valid MyUserRequest user,
                                                 @PathVariable("role") String role) {

    registerService.registerUser(user, role);
    return ResponseEntity.ok().build();
  }


  @ExceptionHandler({BadRoleException.class,UserExistsException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleHttpServerErrorException(Throwable ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getBindingResult().getFieldError().getDefaultMessage());
  }

  public record ErrorResponse(int status, String message) {
  }
}
