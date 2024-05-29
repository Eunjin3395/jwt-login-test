package com.example.springSecurity;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setHeader("Content-Type", "application/json");
//
//        Map<String, String> responseBody = new HashMap<>();
//        responseBody.put("message", "Unauthorized");
//
//        String jsonResponse = objectMapper.writeValueAsString(responseBody);
//        response.getWriter().write(jsonResponse);
//        response.getWriter().flush();
//        response.getWriter().close();

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(401);
        PrintWriter writer = response.getWriter();
        ApiResponse<Object> apiResponse =
                ApiResponse.builder()
                        .isSuccess(false)
                        .code(ErrorStatus.UNAUTHORIZED_EXCEPTION.getCode())
                        .message(ErrorStatus.UNAUTHORIZED_EXCEPTION.getMessage())
                        .result(null)
                        .build();
        try {
            writer.write(apiResponse.toString());
        } catch (NullPointerException e) {
            log.error("응답 메시지 작성 에러", e);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}
