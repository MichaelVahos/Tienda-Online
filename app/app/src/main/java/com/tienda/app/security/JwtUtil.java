package com.tienda.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(this.secret.getBytes());
    }

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.expiration))
                .signWith(this.getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = this.extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token){
        return this.extractExpiration(token).before(new Date());
    }

    private  Date extractExpiration(String token){
        return this.extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, String username){
        return username.equals(this.extractUsername(token)) && !this.isTokenExpired(token);
    }
}
