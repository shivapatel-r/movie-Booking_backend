package com.moviebookingapp.api.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moviebookingapp.api.domain.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String seatNumber;

  @Enumerated(EnumType.STRING)
  private Status status = Status.AVAILABLE;

  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "movie_name", referencedColumnName = "movieName"),
    @JoinColumn(name = "theatre_name", referencedColumnName = "theatreName")
  })
  @JsonIgnore
  private Movie movie;

  @ManyToOne
  @JoinColumn(name = "ticket_id")
  private Ticket ticket;
}
