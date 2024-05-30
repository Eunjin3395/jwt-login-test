package com.example;

import com.example.apiPayload.code.status.ErrorStatus;
import com.example.apiPayload.exception.GeneralException;
import com.example.domain.LoginType;
import com.example.domain.Member;
import com.example.dto.MemberRequest;
import com.example.dto.MemberResponse;
import com.example.jwt.JwtUtil;
import com.example.jwt.RefreshTokenDetails;
import com.example.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;

    /**
     * member외 socialId와 LoginType String을 받아 access token, refresh token 발급
     *
     * @param socialId
     * @param requestLoginType
     * @return
     */
    @Transactional
    public MemberResponse.loginDto login(Long socialId, String requestLoginType) {
        LoginType loginType = null;

        if (requestLoginType.equals(LoginType.KAKAO.toString())) {
            loginType = LoginType.KAKAO;
        } else if (requestLoginType.equals(LoginType.APPLE.toString())) {
            loginType = LoginType.APPLE;
        } else {
            throw new RuntimeException("Invalid LoginType");
        }

        Member member = memberRepository.findBySocialIdAndLoginType(socialId, loginType).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String accessToken = jwtUtil.createAccessToken(member.getId(), member.getSocialId(), member.getRoleType());
        String refreshToken = refreshTokenService.generateRefreshToken(member.getSocialId(), member.getLoginType());

        return MemberResponse.loginDto.builder()
                .memberId(member.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtUtil.getTokenExpirationTime(accessToken))
                .build();
    }

    @Transactional
    public Member join(MemberRequest.signinRequest request, String reqLoginType) {
        LoginType loginType = null;
        if (reqLoginType.equals(LoginType.KAKAO.toString())) {
            loginType = LoginType.KAKAO;
        } else if (reqLoginType.equals(LoginType.APPLE.toString())) {
            loginType = LoginType.APPLE;
        }

        Member member = MemberConverter.toMember(request, loginType);
        return memberRepository.save(member);

    }

    public MemberResponse.reissueDto reissueToken(MemberRequest.reissueRequest request) {
        String refreshToken = refreshTokenService.reGenerateRefreshToken(request);
        RefreshTokenDetails refreshTokenDetails = refreshTokenService.getRefreshTokenDetails(refreshToken);
        Member parsedMember = memberRepository.findById(refreshTokenDetails.getMemberId()).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String accessToken = jwtUtil.createAccessToken(parsedMember.getId(), parsedMember.getSocialId(), parsedMember.getRoleType());

        return MemberResponse.reissueDto.builder()
                .memberId(parsedMember.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtUtil.getTokenExpirationTime(accessToken))
                .build();
    }

}
