package com.example.jwt;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.JwtAuthenticationException;
import com.example.domain.RoleType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

/**
 * [JWT 관련 메서드를 제공하는 클래스]
 */
@Slf4j
@Component
public class JwtUtil {

    private final Key key;
    private final long accessTokenExpTime;
    public static final String AUTHORIZATION_HEADER = "Authorization";


    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_expiration_time}") long accessTokenExpTime
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    /**
     * @param memberId
     * @param socialId
     * @param roleType
     * @return
     */
    public String createAccessToken(Long memberId, Long socialId, RoleType roleType) {
        return createToken(memberId, socialId, roleType, accessTokenExpTime);
    }


    private String createToken(Long memberId, Long socialId, RoleType roleType, Long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);
        claims.put("socialId", socialId);
        claims.put("role", roleType.toString());

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenExpTime);


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * JWT토큰에서 socialId 추출
     *
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

    public long getTokenExpirationTime(String token) {
        return parseClaims(token).getExpiration().getTime();
    }


    /**
     * JWT 검증
     *
     * @param token
     * @return IsValidate
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new JwtAuthenticationException(ErrorStatus.INVALID_TOKEN_EXCEPTION);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw new JwtAuthenticationException(ErrorStatus.EXPIRED_JWT_EXCEPTION);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new JwtAuthenticationException(ErrorStatus.INVALID_TOKEN_EXCEPTION);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new JwtAuthenticationException(ErrorStatus.INVALID_TOKEN_EXCEPTION);
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT Claims 추출
     *
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
