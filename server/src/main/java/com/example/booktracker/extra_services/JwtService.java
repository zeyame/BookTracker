package com.example.booktracker.extra_services;

import com.example.booktracker.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import jakarta.annotation.PostConstruct;
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
    private String expirationTimeStr;

    private static long expirationTime;

    @PostConstruct
    public void init() {
        this.expirationTime = Long.parseLong(expirationTimeStr);
    }
    private String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JSON Web Token (JWT) for the specified user.
     *
     * This method creates a JWT with the user's username as the subject and sets
     * the token's expiration time based on the configured expiration time. The token
     * is signed using the specified signing key.
     *
     *
     * @param username The {@link User} object for which the JWT is to be generated.
     *             The user's username is used as the subject of the token.
     * @return A {@link String} representing the generated JWT.
     * @throws IllegalArgumentException if any argument provided to the JWT builder
     *                                  is invalid.
     * @throws SecurityException if an error occurs while signing the JWT.
     * @throws JwtException if any unexpected errors occur during token creation.
     */
    public String generateToken(String username) {
        return buildToken(username, expirationTime);
    }

    /**
     * Constructs a JWT token for the given user with the specified expiration time.
     *
     * This method builds a JWT token using the provided user details and expiration time. It sets the token's
     * issuer, subject, expiration date, and issued date. It then signs the token with a secret key using the HS256
     * algorithm and returns the compacted token as a string.
     *
     * @param username The user for whom the JWT token is being generated. The token's subject is set to the user's username.
     * @param expirationTime The duration in milliseconds for which the token will be valid from the time of issuance.
     * @return The generated JWT token as a string.
     * @throws RuntimeException if there is an issue with token arguments, signing the JWT, or any unexpected error during
     *      token construction.
     */
    private String buildToken (String username, long expirationTime) {
        try {
            return Jwts.builder()
                    .issuer("ShelfQuest")
                    .subject(username)
                    .expiration(new Date((System.currentTimeMillis() + expirationTime)))
                    .issuedAt(new Date())
                    .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                    .compact();

        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("Illegal token arguments." + e.getMessage());
        }
        catch (SecurityException e) {
            throw new RuntimeException("Error signing JWT. " + e.getMessage());
        }
        catch (JwtException e) {
            throw new RuntimeException("Unexpected error when building JWT. " + e.getMessage());
        }
    }

    /**
     * Extracts the claims from the provided JWT token.
     *
     * <This method parses the JWT token to extract the claims using the provided secret key. It is intended for internal
     * use within the JwtService class to facilitate claims extraction.
     *
     * @param token The JWT token from which to extract claims.
     * @return The claims extracted from the token.
     * @throws JwtException If there is an issue parsing the token or extracting claims.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getSignInKey(secretKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (JwtException e) {
            throw new RuntimeException("Unexpected error when parsing/extracting claims from JWT. " + e.getMessage());
        }
    }

    /**
     * Checks if the provided JWT token is valid for the specified username.
     *
     * This method validates the JWT token by ensuring that the token's username matches the provided username
     * and that the token has not expired. It is used to verify the authenticity and validity of the token.
     *
     * @param token The JWT token to be validated.
     * @param username The username to be compared with the username in the token.
     * @return {@code true} if the token is valid and matches the username, {@code false} otherwise.
     */
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
