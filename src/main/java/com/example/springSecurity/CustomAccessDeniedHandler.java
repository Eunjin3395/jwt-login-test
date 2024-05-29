package com.example.springSecurity;

import com.example.apiPayload.ApiResponse;
import com.example.apiPayload.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setHeader("Content-Type", "applicaiton/json");
//
//        Map<String, String> responseBody = new HashMap<>();
//        responseBody.put("message", "Forbidden");
//
//        String jsonResponse = objectMapper.writeValueAsString(responseBody);
//
//        response.getWriter().write(jsonResponse);
//        response.getWriter().flush();
//        response.getWriter().close();

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(403);
        PrintWriter writer = response.getWriter();

        ApiResponse<Object> apiResponse =
                ApiResponse.builder()
                        .isSuccess(false)
                        .code(ErrorStatus._FORBIDDEN.getCode())
                        .message(ErrorStatus._FORBIDDEN.getMessage())
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
