package org.rentfriend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegexConfig {

  @Bean(name="roleRegex")
  String roleRegex(){
    return "SELLER|BUYER";
  }
}
