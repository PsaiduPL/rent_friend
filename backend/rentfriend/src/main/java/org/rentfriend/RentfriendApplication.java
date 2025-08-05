package org.rentfriend;

import org.rentfriend.entity.*;
import org.rentfriend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
                         ProfileRepository profileRepository
      , PasswordEncoder passwordEncoder,
                         InterestRepository interestRepository,
                         OfferRepository offerRepository,
                         BodyParameterRepository bodyParameterRepository) {
    return args -> {
      MyUser myUser = new MyUser();
      myUser.setUsername("admin");
      myUser.setPassword(passwordEncoder.encode("admin"));
      myUser.setEmail("admin@gmail.com");
      myUser.setRole("SELLER");
      MyUser u = userRepository.save(myUser);


      Profile profile = new Profile();
      profile.setAge(15);
      profile.setUser(u);
      profile.setDescription("Young lady from warsaw");
      profile.setCity("WARSAW");
      profile.setName("Hot pawel123");
      List<Interest> interestList = interestRepository.findAll(Pageable.ofSize(2)).getContent();
      profile.setInterestList(interestList);

      Offer offer = new Offer();
      offer.setTitle("Przejscie na spacer");
      offer.setDescription("Tylko w godzinach 17 - 20");
      offer.setPricePerHour(BigDecimal.valueOf(50.5));
      profile.setOfferList(List.of(offer));
      offer.setProfile(profile);


      BodyParameter bodyParameter = new BodyParameter();
      bodyParameter.setHeight(178.5);
      bodyParameter.setWeight(75.2);
      bodyParameter.setProfile(profile);
      bodyParameterRepository.save(bodyParameter);

      profile.setBodyParameter(bodyParameter);
      profileRepository.save(profile);
      //offerRepository.save(offer);

    };
  }

}
