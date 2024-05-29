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
     *
     * @param memberId
     * @param socialId
     * @param roleType
     * @return
     */
    public String createAccessToken(Long memberId, Long socialId, RoleType roleType) {
        return createToken(memberId, socialId, roleType, accessTokenExpTime);
    }



    private String createToken( Long memberId, Long socialId, RoleType roleType, Long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("socialId", socialId);
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
    public Long getMemberId(String token) {
        return parseClaims(token).get("memberId", Long.class);
    }

    public Long getSocialId(String token) {
        return parseClaims(token).get("socialId", Long.class);
    }

    public RoleType getRoleType(String token) {

        Claims claims = parseClaims(token);
        String roleType = claims.get("roleType", String.class); // Retrieve as String
        return RoleType.valueOf(roleType); // Convert String to LoginType enum
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
