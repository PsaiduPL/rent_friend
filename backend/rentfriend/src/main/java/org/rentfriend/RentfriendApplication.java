package org.rentfriend;

import org.rentfriend.entity.MyUser;
import org.rentfriend.entity.Profile;
import org.rentfriend.repository.ProfileRepository;
import org.rentfriend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class RentfriendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentfriendApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository,
												 ProfileRepository profileRepository
	, PasswordEncoder passwordEncoder) {
		return args->{
			MyUser myUser = new MyUser();
			myUser.setUsername("admin");
			myUser.setPassword(passwordEncoder.encode("admin"));
			myUser.setEmail("admin@gmail.com");
			myUser.setRole("SELLER");
			MyUser u = userRepository.save(myUser);

			Profile profile = new Profile();
			profile.setAge((short)15);
			profile.setUser(u);
			profile.setDescription("Young lady from warsaw");
			profile.setCity("WARSAW");
			profile.setName("Hot pawel123");
			profileRepository.save(profile);

		};
	}

}
