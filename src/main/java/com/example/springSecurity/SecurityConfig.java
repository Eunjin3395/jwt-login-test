package com.example.springSecurity;

import com.example.domain.RoleType;
import com.example.jwt.JwtAuthFilter;
import com.example.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;
    private final CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
    private final EntryPointUnauthorizedHandler unauthorizedHandler = new EntryPointUnauthorizedHandler();
    private final JwtAuthenticationExceptionHandler jwtAuthenticationExceptionHandler = new JwtAuthenticationExceptionHandler();


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .formLogin((form) -> form.disable())
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf((csrf) -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS));


        httpSecurity
                .authorizeHttpRequests(
                        (authorizeRequests) -> {
                            authorizeRequests
                                    .requestMatchers("/api/auth/login").permitAll() // 로그인 엔드포인트를 허용
                                    .requestMatchers("/api/**").hasAnyRole("MEMBER", "ADMIN")
                                    .requestMatchers("/admin/**").hasRole(RoleType.ADMIN.toString())
                                    .anyRequest().authenticated();
                        })
                .exceptionHandling(
                        (exceptionHandling) ->
                                exceptionHandling
                                        .accessDeniedHandler(accessDeniedHandler)
                                        .authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationExceptionHandler, JwtAuthFilter.class);


        return httpSecurity.build();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        //CSRF, CORS
//        http.csrf((csrf) -> csrf.disable());
//        http.cors(Customizer.withDefaults());
//
//        //세션 관리 상태 없음으로 구성, Spring Security가 세션 생성 or 사용 X
//        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
//                SessionCreationPolicy.STATELESS));
//
//        //FormLogin, BasicHttp 비활성화
//        http.formLogin((form) -> form.disable());
//        http.httpBasic(AbstractHttpConfigurer::disable);
//
//
//        //JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
//        http.addFilterBefore(new JwtAuthFilter(customUserDetailsService, jwtUtil), UsernamePasswordAuthenticationFilter.class);
//
//        http.exceptionHandling((exceptionHandling) -> exceptionHandling
//                .authenticationEntryPoint(authenticationEntryPoint)
//                .accessDeniedHandler(accessDeniedHandler)
//        );
//
//        // 권한 규칙 작성
//        http.authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                        //@PreAuthrization을 사용할 것이기 때문에 모든 경로에 대한 인증처리는 Pass
//                        .anyRequest().permitAll()
////                        .anyRequest().authenticated()
//        );
//
//        return http.build();
//    }


}
