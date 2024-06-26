package com.example.dto;

import com.example.domain.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MemberResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class loginDto {
        Long memberId;
        String accessToken;
        String refreshToken;

        Long accessTokenExpiresIn;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class myInfoDto {
        Long socialId;
        String name;
        String email;
        LoginType loginType;
        String job;
        String gender;
        LocalDate birth;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class reissueDto {
        Long memberId;
        String accessToken;
        String refreshToken;

        Long accessTokenExpiresIn;
    }

}
