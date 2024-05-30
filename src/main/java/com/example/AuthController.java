package com.example;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.SuccessStatus;
import com.example.domain.Member;
import com.example.dto.MemberRequest;
import com.example.dto.MemberResponse;
import com.example.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<MemberResponse.loginDto> login(@RequestBody MemberRequest.loginRequest request) {

        return ApiResponse.onSuccess(authService.login(request.getId(), request.getLoginType()));
    }

    @PostMapping("/signin/{loginType}")
    public ApiResponse<MemberResponse.loginDto> signin(@RequestBody MemberRequest.signinRequest request,
                                                       @PathVariable(name = "loginType") String loginType
    ) {

        Member member = memberService.join(request, loginType);
        return ApiResponse.of(SuccessStatus.JOIN_SUCCESS, authService.login(member.getSocialId(), member.getLoginType().toString()));
    }
}
