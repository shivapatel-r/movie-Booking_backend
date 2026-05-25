package com.moviebookingapp.api.domain.entities;

import java.io.Serializable;
import lombok.Data;

@Data
public class MovieId implements Serializable {
  private String movieName;
  private String theatreName;
}
