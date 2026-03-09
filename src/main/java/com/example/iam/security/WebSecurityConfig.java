package com.example.iam.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig  {

    /**
     * Spring Security 필터 체인을 구성합니다.
     *
     * <p>인가 정책:</p>
     * <ul>
     *   <li>GET /users/** — 인증 없이 접근 허용</li>
     *   <li>그 외 모든 요청 — 인증 필요</li>
     * </ul>
     * <p>세션 정책: STATELESS (JWT 등 토큰 기반 인증 전제)</p>
     *
     * @param httpSecurity Spring Security의 {@link HttpSecurity} 빌더
     * @return 구성이 완료된 {@link SecurityFilterChain}
     * @throws Exception HttpSecurity 구성 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                        .anyRequest().authenticated()
                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt
//                                .jwtAuthenticationConverter(jwtAuthConverter)
//                        )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }

    /**
     * Spring Security 필터 체인을 우회(bypass)할 요청 경로를 설정합니다.
     *
     * <p>아래 경로들은 보안 필터를 거치지 않으므로 인증·인가가 적용되지 않습니다:</p>
     * <ul>
     *   <li>POST, DELETE, PUT, GET /users/** — 사용자 관련 API</li>
     *   <li>/configuration/**, swagger-ui.html, swagger-ui/**, /api-docs/** — API 문서</li>
     * </ul>
     *
     * @return 보안 필터 우회 규칙이 적용된 {@link WebSecurityCustomizer}
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> {
            web.ignoring().requestMatchers(
                    HttpMethod.POST, "/users/**"
            );
            web.ignoring().requestMatchers(
                    HttpMethod.DELETE, "/users/**"
            );
            web.ignoring().requestMatchers(
                    HttpMethod.PUT, "/users/**"
            );
            web.ignoring().requestMatchers(
                    HttpMethod.GET, "/users/**"
            )
                    .requestMatchers("/configuration/**", "swagger-ui.html", "swagger-ui/**", "/api-docs/**");
        };
    }
}