package org.rentfriend;

import org.rentfriend.entity.MyUser;
import org.rentfriend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RentfriendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentfriendApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args->{
			MyUser myUser = new MyUser();
			myUser.setUsername("admin");
			myUser.setPassword("admin");
			myUser.setEmail("admin@gmail.com");
			myUser.setRole("ADMIN");
			userRepository.save(myUser);


		};
	}

}
