package org.rentfriend;

import org.rentfriend.entity.*;
import org.rentfriend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.config.Profiles;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@SpringBootApplication
public class RentfriendApplication {

  public static void main(String[] args) {
    SpringApplication.run(RentfriendApplication.class, args);
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
