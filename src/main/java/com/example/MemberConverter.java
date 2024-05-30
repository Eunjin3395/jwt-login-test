package com.example;

import com.example.domain.LoginType;
import com.example.domain.Member;
import com.example.domain.RoleType;
import com.example.dto.MemberRequest;

public class MemberConverter {

    public static Member toMember(MemberRequest.signinRequest request, LoginType loginType) {
        return Member.builder()
                .email(request.getEmail())
                .name(request.getName())
                .gender(request.getGender())
                .job(request.getJob())
                .birth(request.getBirth())
                .roleType(RoleType.MEMBER)
                .socialId(request.getSocialId())
                .loginType(loginType)
                .build();
    }
}
