package com.example;

import com.example.domain.LoginType;
import com.example.domain.Member;
import com.example.dto.MemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

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
}
