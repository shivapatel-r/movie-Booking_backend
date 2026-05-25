package com.moviebookingapp.api.domain.entities;

import com.moviebookingapp.api.domain.enums.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Entity
@Data
public class Ticket {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String loginId;

  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "movie_name", referencedColumnName = "movieName"),
    @JoinColumn(name = "theatre_name", referencedColumnName = "theatreName")
  })
  private Movie movie;

  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Seat> seatsBooked;

  private LocalDateTime bookedAt;

  private long numberOfTickets;

  @Enumerated(EnumType.STRING)
  private Status status;
}
