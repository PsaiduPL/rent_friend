package org.rentfriend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class ProfileConfig {



  @Bean
  UriComponentsBuilder uriComponentsBuilder(){
    return UriComponentsBuilder.newInstance();
  }
}
