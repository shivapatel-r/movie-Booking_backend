package com.moviebookingapp.api.domain.entities;

import com.moviebookingapp.api.domain.enums.Status;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Data
@IdClass(MovieId.class)
public class Movie {

  @Id private String movieName;

  @Id private String theatreName;

  private int totalTickets;
  private int ticketsBooked;
  private int ticketsAvailable;

  @Enumerated(EnumType.STRING)
  private Status status;

  @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Seat> seats;

  @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Ticket> tickets;
}
