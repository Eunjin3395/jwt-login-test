package com.example.dto;

import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class loginRequest{
        Long id;
        String loginType;
    }

}
