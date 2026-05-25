package com.moviebookingapp.api.domain.repositories;

import com.moviebookingapp.api.domain.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findByLoginId(String loginId);

  Optional<User> findByEmail(String email);

  boolean existsByRole(String role);
}
