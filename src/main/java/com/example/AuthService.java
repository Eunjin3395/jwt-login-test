package com.example;

import com.example.domain.LoginType;
import com.example.domain.Member;
import com.example.dto.MemberRequest;
import com.example.jwt.JwtUtil;
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

    @Transactional
    public String login(MemberRequest.loginRequest request) {
        log.info("Request ID: {}, Login Type: {}", request.getId(), request.getLoginType());
        Long socialId = request.getId();
        String  requestLoginType = request.getLoginType();
        LoginType loginType = null;

        if (requestLoginType.equals(LoginType.KAKAO.toString())) {
            loginType = LoginType.KAKAO;
        } else if (requestLoginType.equals(LoginType.APPLE.toString())) {
            loginType = LoginType.APPLE;
        }else{
            throw new RuntimeException("Invalid LoginType");
        }

        Member member = memberRepository.findBySocialIdAndLoginType(socialId, loginType).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String accessToken = jwtUtil.createAccessToken(member.getId(), member.getSocialId(), member.getRoleType());
        return accessToken;
    }
}
