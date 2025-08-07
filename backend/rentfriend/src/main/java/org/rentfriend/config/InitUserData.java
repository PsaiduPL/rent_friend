package org.rentfriend.config;


import org.rentfriend.entity.*;
import org.rentfriend.repository.InterestRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class InitUserData {

  @Bean
   public List<MyUser> users(PasswordEncoder passwordEncoder){
    MyUser myUser = new MyUser();
    myUser.setUsername("john");
    myUser.setPassword(passwordEncoder.encode("john"));
    myUser.setEmail("john@gmail.com");
    myUser.setRole("SELLER");

    MyUser myUser2 = new MyUser();
    myUser2.setUsername("pawel");
    myUser2.setPassword(passwordEncoder.encode("pawel"));
    myUser2.setEmail("pawel@gmail.com");
    myUser2.setRole("BUYER");
    return List.of(myUser,myUser2);
  }


  @Bean
  public List<Profile> profiles(InterestRepository interestRepository){
    Profile profile = new Profile();

    profile.setAge(15);
    profile.setDescription("Young lady from warsaw");
    profile.setCity("WARSAW");
    profile.setName("Hot pawel123");
    profile.setGender("male");
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

    //bodyParameterRepository.save(bodyParameter);
   // profile.setInterestList(List.of(new Interest(1L,null,null),new Interest(2L,null,null),new Interest(3L,null,null)));
    profile.setBodyParameter(bodyParameter);

    Profile profile1 = new Profile();

    profile1.setAge(19);
    profile1.setDescription("Young lady from warsaw");
    profile1.setCity("WARSAW");
    profile1.setName("Hot juan");
    profile1.setGender("female");
    List<Interest> interestList1 = interestRepository.findAll(Pageable.ofSize(2)).getContent();
    profile1.setInterestList(interestList1);

    Offer offer1 = new Offer();
    offer1.setTitle("Przejscie na kino");
    offer1.setDescription("Tylko w godzinach 16 - 20");
    offer1.setPricePerHour(BigDecimal.valueOf(20.5));
    profile1.setOfferList(List.of(offer1));
    offer1.setProfile(profile1);


    BodyParameter bodyParameter1 = new BodyParameter();
    bodyParameter1.setHeight(160.5);
    bodyParameter1.setWeight(75.2);
    bodyParameter1.setProfile(profile1);

    //bodyParameterRepository.save(bodyParameter);
    //profile1.setInterestList(List.of(new Interest(2L,null,null),new Interest(3L,null,null)));
    profile1.setBodyParameter(bodyParameter1);

    return List.of(profile,profile1);
  }
}
