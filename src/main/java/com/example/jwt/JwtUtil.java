package com.example.jwt;

import com.example.domain.LoginType;
import com.example.domain.RoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * [JWT 관련 메서드를 제공하는 클래스]
 */
@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_expiration_time}") long accessTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    /**
     * Access Token 생성
     * @param member
     * @return Access Token String
     */
    public String createAccessToken(Long socialId, LoginType loginType, RoleType roleType) {
        return createToken(socialId,loginType, roleType,accessTokenExpTime);
    }


    /**
     * JWT 토큰 생성
     * @param socialId
     * @param loginType
     * @param email
     * @param expireTime
     * @return JWT String
     */
    private String createToken( Long socialId, LoginType loginType, RoleType roleType, Long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("socialId", socialId);
        claims.put("loginType", loginType.toString());
        claims.put("role", roleType.toString());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * JWT토큰에서 socialId 추출
     * @param token
     * @return
     */
    public Long getSocialId(String token) {
        return parseClaims(token).get("socialId", Long.class);
    }

    public LoginType getLoginType(String token) {

        Claims claims = parseClaims(token);
        String loginTypeStr = claims.get("loginType", String.class); // Retrieve as String
        return LoginType.valueOf(loginTypeStr); // Convert String to LoginType enum
    }


    /**
     * JWT 검증
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    /**
     * JWT Claims 추출
     * @param accessToken
     * @return JWT Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


}
