package org.rentfriend.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class ImageConfig {

  @Value("${image.storage.location}")
  String baseLocation;

  @Bean
  String location(){
    return baseLocation;
  }

  @Bean(name = "buckets")
  ConcurrentMap<Integer,Integer> buckets(){
    return new ConcurrentHashMap<Integer,Integer>();
  }
}
