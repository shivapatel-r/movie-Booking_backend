package com.moviebookingapp.api.domain.filters;

import com.moviebookingapp.api.domain.config.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

  @Autowired private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getServletPath();

    if (path.equals("/api/v1.0/moviebooking/login")
        || path.equals("/api/v1.0/moviebooking/register")
        || path.contains("/forgot")) {

      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {

      String token = header.substring(7);

      try {
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractAllClaims(token).get("role", String.class);

        System.out.println("User: " + username + " Role: " + role);

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                username, null, List.of(new SimpleGrantedAuthority(role)));

        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (Exception e) {
        System.out.println("Invalid JWT: " + e.getMessage());
      }
    }

    filterChain.doFilter(request, response);
  }
}
