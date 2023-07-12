package com.champ.nocash.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.token.expiry.in.minutes}")
    private int EXPIRY_TIME;

    private String salt = "";

    public void setSalt(String salt) {
        this.salt = salt;
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractIPAddress(String token) {
        return extractAllClaims(token).get("ipAddress", String.class);
    }

    public String extractUserAgent(String token) {
        return extractAllClaims(token).get("userAgent", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY+salt).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails, String ipAddress, String userAgent) {
        System.out.println(SECRET_KEY);
        Map<String, Object> claims = new HashMap<>();
        claims.put("ipAddress", ipAddress);
        claims.put("userAgent", userAgent);
        return  createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * EXPIRY_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY+salt).compact();
    }

    public boolean validateToken(String token, UserDetails userDetails, String ipAddress, String userAgent) {
        final String username = extractUsername(token);
        final String extractedIPAddress = extractIPAddress(token);
        final String extractedUserAgent = extractUserAgent(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && extractedUserAgent.equals(userAgent) && extractedIPAddress.equals(ipAddress));
    }
}
