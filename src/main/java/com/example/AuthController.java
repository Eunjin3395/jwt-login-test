package com.example;

import com.example.apiPayload.ApiResponse;
import com.example.dto.MemberRequest;
import com.example.dto.MemberResponse;
import com.example.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<MemberResponse.JwtDto> login(@RequestBody MemberRequest.loginRequest request) {
        String jwtToken = authService.login(request);

        MemberResponse.JwtDto jwtDto = MemberResponse.JwtDto.builder()
                .accessToken(jwtToken)
                .accessTokenExpiresIn(jwtUtil.getTokenExpirationTime(jwtToken))
                .build();

        return ApiResponse.onSuccess(jwtDto);
    }
}
