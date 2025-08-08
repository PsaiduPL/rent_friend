package org.rentfriend;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.rentfriend.entity.*;
import org.rentfriend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.config.Profiles;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
public class RentfriendApplication {

  public static void main(String[] args) {
    SpringApplication.run(RentfriendApplication.class, args);
  }
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(); // Podaj nazwy cache'y
    cacheManager.setCaffeine(Caffeine.newBuilder()
        .initialCapacity(200)
        .maximumSize(500)
        .expireAfterWrite(1, TimeUnit.MINUTES)); // Ustaw czas życia wpisów
    return cacheManager;
  }
  @Bean
  CommandLineRunner init(UserRepository userRepository,
                         ProfileRepository profileRepository,
                         InterestRepository interestRepository,
                         OfferRepository offerRepository,
                         BodyParameterRepository bodyParameterRepository,
                         List<MyUser> users,
                         List<Profile> profiles) {
    return args -> {
      List<MyUser> u = userRepository.saveAll(users);

      for(int i = 0 ; i < profiles.size() ; i++){
        profiles.get(i).setUser(u.get(i));

      }


      profileRepository.saveAll(profiles);
      //offerRepository.save(offer);

    };
  }

}
