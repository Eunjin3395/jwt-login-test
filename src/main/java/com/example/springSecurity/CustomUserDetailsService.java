package com.example.springSecurity;

import com.example.MemberRepository;
import com.example.domain.LoginType;
import com.example.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public UserDetails loadUserByMemberIdAndSocialId(Long memberId, Long socialId) throws UsernameNotFoundException {
        Member member = memberRepository.findByIdAndSocialId(memberId, socialId)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저가 없습니다."));

        return new CustomUserDetails(member.getId(),member.getRoleType());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("loadUserByUsername(String username) is not supported. Use loadUserByMemberIdAndSocialId(Long socialId, LoginType loginType) instead.");
    }
}
