package com.example.jwt;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefreshTokenDetails {
    private final Long memberId;
    private final LocalDateTime expireTime;

    public RefreshTokenDetails(Long memberId, LocalDateTime expireTime) {
        this.memberId = memberId;
        this.expireTime = expireTime;
    }
}
