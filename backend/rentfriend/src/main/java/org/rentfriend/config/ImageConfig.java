package org.rentfriend.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageConfig {

  @Value("${image.storage.location}")
  String baseLocation;

  @Bean
  String location(){
    return baseLocation;
  }
}
