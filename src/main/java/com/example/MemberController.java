package com.example;

import com.example.domain.Member;
import com.example.dto.MemberResponse;
import com.example.springSecurity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/member")
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping
    public ResponseEntity<MemberResponse.myInfoDto> getMyInfo(Authentication authentication){
        CustomUserDetails userDetails =  (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(()-> new RuntimeException("Member not found"));

        MemberResponse.myInfoDto response = MemberResponse.myInfoDto.builder()
                .socialId(member.getSocialId())
                .name(member.getName())
                .email(member.getEmail())
                .loginType(member.getLoginType())
                .job(member.getJob())
                .gender(member.getGender())
                .birth(member.getBirth())
                .build();

        return ResponseEntity.status(200).body(response);
    }
}
