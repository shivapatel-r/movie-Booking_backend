package com.moviebookingapp.api.domain.constants;

public class ApiEndPoints {

  // Base Url
  public static final String BASE_URL = "/api/v1.0/moviebooking";
  public static final String LOCAL_HOST = "http://localhost:4200";

  // User-Registration/Login related endpoints
  public static final String REGISTER_USER = "/register";
  public static final String USER_LOGIN = "/login";
  public static final String FORGOT_PASSWORD = "/forgot";
  public static final String RESET_PASSWORD = "/reset";

  // Admin-related endpoints
  public static final String ADD_NEW_MOVIE = "/addMovie";
  public static final String DELETE_MOVIE = "/{movieName}/delete/{theatreName}";
  public static final String UPDATE_TICKET_STATUS = "/updateStatus/{movieName}/{theatreName}";

  // User-related Endpoints
  public static final String VIEW_ALL_MOVIES = "/all";
  public static final String RETRIEVE_BY_MOVIE_NAME = "/movies/search/{movieName}";
  public static final String BOOK_MOVIE = "/book";
  public static final String VIEW_BOOKED_TICKETS = "/view/booking";
}
