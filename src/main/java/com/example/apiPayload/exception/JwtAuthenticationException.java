package com.example.apiPayload.exception;

import com.example.apiPayload.code.status.ErrorStatus;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(ErrorStatus code) {
        super(code.name());
    }
}
