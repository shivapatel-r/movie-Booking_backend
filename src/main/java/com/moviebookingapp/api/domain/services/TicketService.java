package com.moviebookingapp.api.domain.services;

import com.moviebookingapp.api.domain.dtos.BookingSummaryResponseDto;
import com.moviebookingapp.api.domain.dtos.TicketRequestDto;
import com.moviebookingapp.api.domain.dtos.ViewMovieResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface TicketService {
  String bookTicket(@Valid TicketRequestDto ticketRequestDto, String loginId);

  List<BookingSummaryResponseDto> getUserTickets(String loginId);

  List<ViewMovieResponse> searchMoviesByName(String movieName);
}
