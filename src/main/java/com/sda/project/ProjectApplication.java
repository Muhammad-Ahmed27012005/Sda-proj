package com.sda.project;

import com.sda.project.model.User;
import com.sda.project.model.enums.Role;
import com.sda.project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (!userRepository.existsByEmail("admin@streamflix.com")) {
				userRepository.save(User.builder()
						.fullName("StreamFlix Admin")
						.email("admin@streamflix.com")
						.password(passwordEncoder.encode("admin123"))
						.role(Role.ADMIN)
						.build());
			}
		};
	}
}
