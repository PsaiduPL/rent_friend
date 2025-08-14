package org.rentfriend.config;


import com.github.javafaker.Faker;
import org.rentfriend.entity.*;
import org.rentfriend.repository.InterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.context.annotation.Profile("init-data")
@Configuration
public class InitUserData {
    @Value("${init.data.size}")
    Integer size;
    @Value("${init.data.cityRegex}")
    String citiesRegex;
    Faker faker = new Faker();

    @Lazy
    @Bean
    public List<MyUser> users(PasswordEncoder passwordEncoder) {

        System.out.println("----------" + size);
        var users = Stream.generate(() -> {
            MyUser a = new MyUser();

            a.setUsername(faker.name().firstName() + faker.random().hex(3));
            a.setEmail(faker.internet().emailAddress());
            a.setPassword(passwordEncoder.encode("123"));
            a.setRole(faker.regexify("SELLER|BUYER"));
            return a;
        }).parallel().limit(size).collect(Collectors.toList());

        users.add(new MyUser(null, "user", "user@gmail.com", passwordEncoder.encode("user"), "SELLER", null, null));
        return users;

    }

    @Lazy
    @Bean
    public List<Profile> profiles(InterestRepository interestRepository) {

        List<Profile> profiles = Stream.generate(() -> {
            var p = new Profile();
            p.setAge(faker.number().numberBetween(18, 50));
            p.setCity(faker.regexify(citiesRegex));
            p.setDescription(faker.howIMetYourMother().quote());
            p.setName(faker.name().username());
            p.setGender(faker.regexify("male|female"));
            BodyParameter bodyParameter = null;
            if (faker.number().numberBetween(0, 500) % 3 != 0) {

                bodyParameter = new BodyParameter();
                bodyParameter.setHeight(faker.number().randomDouble(1, 150, 210));
                bodyParameter.setWeight(faker.number().randomDouble(1, 45, 120));
                bodyParameter.setProfile(p);
            }
            p.setBodyParameter(bodyParameter);
            var a = Stream.generate(() -> {
                Interest i = new Interest();
                i.setId((long) faker.number().numberBetween(1, 40));
                return i;
            }).parallel().limit(faker.number().numberBetween(1, 10)).distinct().collect(Collectors.toList());
            p.setInterestList(a);
            var offerList = Stream.generate(() -> {
                var offer = new Offer();
                offer.setPricePerHour(BigDecimal.valueOf(faker.number().randomDouble(1, 10, 500)));
                offer.setTitle(faker.harryPotter().quote());
                offer.setDescription(faker.chuckNorris().fact());
                offer.setProfile(p);
                return offer;
                //offer.setProfile();
            }).limit(faker.number().numberBetween(0, 4)).collect(Collectors.toList());
            p.setOfferList(offerList);
            return p;
        }).limit(size).collect(Collectors.toList());
        var profile = profiles.get(0);
        profiles.add(new Profile(null, profile.getName(), profile.getDescription(), profile.getCity(),
            profile.getAge(),
            profile.getGender(),
            null,
            profile.getInterestList(), null, profile.getBodyParameter(), null
        ));

        return profiles;

    }
}
