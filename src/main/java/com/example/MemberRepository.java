package com.example;

import com.example.domain.LoginType;
import com.example.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialIdAndLoginType(Long socialId, LoginType loginType);

    Optional<Member> findByIdAndSocialId(Long memberId, Long socialId);
}
