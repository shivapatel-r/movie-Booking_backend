package com.moviebookingapp.api.domain.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.moviebookingapp.api.domain.entities.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  // ✅ Test: findByLoginId
  @Test
  @DisplayName("Should find user by loginId")
  void testFindByLoginId() {

    // Given
    User user = new User();
    user.setLoginId("shiva");
    user.setEmail("shiva@gmail.com");

    userRepository.save(user);

    // When
    Optional<User> result = userRepository.findByLoginId("shiva");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getEmail()).isEqualTo("shiva@gmail.com");
  }

  // ✅ Test: findByEmail
  @Test
  @DisplayName("Should find user by email")
  void testFindByEmail() {

    // Given
    User user = new User();
    user.setLoginId("ram");
    user.setEmail("ram@gmail.com");

    userRepository.save(user);

    // When
    Optional<User> result = userRepository.findByEmail("ram@gmail.com");

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getLoginId()).isEqualTo("ram");
  }

  // ✅ Negative Test: loginId not found
  @Test
  @DisplayName("Should return empty when loginId not found")
  void testFindByLoginId_NotFound() {

    // When
    Optional<User> result = userRepository.findByLoginId("unknown");

    // Then
    assertThat(result).isEmpty();
  }

  // ✅ Negative Test: email not found
  @Test
  @DisplayName("Should return empty when email not found")
  void testFindByEmail_NotFound() {

    // When
    Optional<User> result = userRepository.findByEmail("no@email.com");

    // Then
    assertThat(result).isEmpty();
  }
}
