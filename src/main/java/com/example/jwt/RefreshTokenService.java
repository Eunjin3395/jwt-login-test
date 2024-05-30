package com.example.jwt;

import com.example.MemberRepository;
import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.LoginType;
import com.example.domain.Member;
import com.example.dto.MemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final ConcurrentMap<String, RefreshTokenDetails> refreshTokenStore = new ConcurrentHashMap<>();
    private final MemberRepository memberRepository;

    @Value("${jwt.refresh_expiration_day}")
    private int refreshExpireDay;

    // 새로운 refresh token 생성 및 저장
    public String generateRefreshToken(Long socialId, LoginType loginType) {
        Member member = memberRepository.findBySocialIdAndLoginType(socialId, loginType).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String refreshToken = UUID.randomUUID().toString();
        LocalDateTime expireAt = LocalDateTime.now().plusDays(refreshExpireDay);
        refreshTokenStore.put(refreshToken, new RefreshTokenDetails(member.getId(), expireAt));
        return refreshToken;
    }

    /**
     * request에 담긴 refresh token에 대한 유효성 검증 후, refresh token 재발급
     *
     * @param request
     * @return
     */
    public String reGenerateRefreshToken(MemberRequest.reissueRequest request) {
        if (request.getRefreshToken() == null) { // refresh token 값이 없는 경우
            throw new GeneralException(ErrorStatus.INVALID_TOKEN_EXCEPTION);
        }

        RefreshTokenDetails refreshTokenDetails = refreshTokenStore.get(request.getRefreshToken());
        if (refreshTokenDetails == null) { // 해당 refresh token이 db에 존재하지 않는 경우
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 해당 refresh token을 발급 받은 회원이 존재하는지 검증
        Member member = memberRepository.findById(refreshTokenDetails.getMemberId()).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        LocalDateTime expireTime = refreshTokenDetails.getExpireTime();
        LocalDateTime current = LocalDateTime.now();

        if (current.isAfter(expireTime)) { // 해당 refresh token도 만료되어 재로그인이 필요한 경우
            throw new GeneralException(ErrorStatus.RELOGIN_EXCEPTION);
        } else { // 모든 검증 통과, 새로운 refresh token 발급
            deleteRefreshToken(request.getRefreshToken());
            return generateRefreshToken(member.getSocialId(), member.getLoginType());
        }
    }

    // Method to validate a refresh token
//    public boolean validateRefreshToken(Long memberId, String token) {
//        RefreshTokenDetails storedToken = refreshTokenStore.get(memberId);
//        if (storedToken != null && storedToken.getToken().equals(token) && storedToken.getExpireAt().isAfter(LocalDateTime.now())) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 해당 token db에서 제거
     *
     * @param token
     */
    public void deleteRefreshToken(String token) {
        refreshTokenStore.remove(token);
    }


    /**
     * refresh token uuid로 RefreshTokenDetails 조회
     *
     * @param token
     * @return
     */
    public RefreshTokenDetails getRefreshTokenDetails(String token) {
        return refreshTokenStore.get(token);
    }
}
