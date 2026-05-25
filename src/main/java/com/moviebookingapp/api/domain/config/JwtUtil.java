package com.moviebookingapp.api.domain.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final String SECRET = "mysecretkeymysecretkeymysecretkey";
  private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

  public String generateToken(String username, String role) {

    return Jwts.builder()
        .setSubject(username)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
        .signWith(key)
        .compact();
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  public Claims extractAllClaims(String token) {

      //parser checks the token’s signature against our secret key
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }
}
