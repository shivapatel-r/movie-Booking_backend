package com.moviebookingapp.api.domain;

import com.moviebookingapp.api.domain.entities.User;
import com.moviebookingapp.api.domain.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  @Profile("!test")
  CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
    return args -> {
      if (!repo.existsByRole("ROLE_ADMIN")) {

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setLoginId("admin1234");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(encoder.encode("Admin@1234"));
        admin.setRole("ROLE_ADMIN");
        admin.setContactNumber("1234123412");

        repo.save(admin);
      }
    };
  }
}
