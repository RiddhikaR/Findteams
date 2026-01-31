package com.findteams.findteams.security;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;


import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private  String secret;

    private final long EXPIRATION=1000 * 60 * 30;

    public String generateToken(String username){
        SecretKey key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION))
                    .signWith(key,SignatureAlgorithm.HS256)
                    .compact();
    }

    public String extractUsername(String token){
        SecretKey Key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims=Jwts.parserBuilder()
                        .setSigningKey(Key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

        return claims.getSubject();
    }
    public boolean isTokenExpired(String token){
        SecretKey Key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims=Jwts.parserBuilder()
                        .setSigningKey(Key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
        Date expiration=claims.getExpiration();
        return expiration.before(new Date());
                        
    }

    public boolean validateToken(String username,String token){
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }



    
}