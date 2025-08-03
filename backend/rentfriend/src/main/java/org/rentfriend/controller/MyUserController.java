package org.rentfriend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MyUserController {




  @GetMapping()
  ResponseEntity<String> getString(){
    return ResponseEntity.ok("Hello word");
  }
}
