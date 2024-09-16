package com.example.booktracker;

import com.example.booktracker.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long expirationTime;

    private String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user) {
        return buildToken(user, expirationTime);
    }

    private String buildToken (User user, long expirationTime) {
        return Jwts.builder()
                .issuer("booktracker")
                .subject(user.getUsername())
                .expiration(new Date((System.currentTimeMillis() + expirationTime)))
                .issuedAt(new Date())
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey(secretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationTime(token).before(new Date());
    }

    private Date extractExpirationTime (String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public long getExpirationTime () {
        return expirationTime;
    }

    private SecretKey getSignInKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
