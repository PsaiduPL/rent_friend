package org.rentfriend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class ProfileConfig {


  @Scope("prototype")
  @Bean
  UriComponentsBuilder uriComponentsBuilder(){
    return UriComponentsBuilder.newInstance();
  }
}
