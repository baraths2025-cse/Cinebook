
package com.cinebook.config;

import com.cinebook.model.Role;
import com.cinebook.model.User;
import com.cinebook.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (!userRepository.existsByEmail("admin@cinebook.com")) {

                User admin = new User();

                admin.setName("CineBook Admin");
                admin.setEmail("admin@cinebook.com");
                admin.setPassword(
                    passwordEncoder.encode("Admin@12345")
                );
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println("Admin account created!");
            }
        };
    }
}
