package com.moviebookingapp.api.domain.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String firstName;
  private String lastName;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String loginId;

  private String password;
  private String contactNumber;

  private String role; // USER / ADMIN
}
