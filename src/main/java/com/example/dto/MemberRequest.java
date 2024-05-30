package com.example.dto;

import lombok.Getter;

import java.time.LocalDate;

public class MemberRequest {

    @Getter
    public static class loginRequest {
        Long id;
        String loginType;
    }

    @Getter
    public static class signinRequest {
        String email;

        String name;

        String gender;

        String job;

        LocalDate birth;

        Long socialId;

        String loginType;
    }

}
