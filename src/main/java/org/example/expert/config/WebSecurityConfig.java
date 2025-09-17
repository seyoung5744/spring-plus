package org.example.expert.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtSecurityProperties jwtSecurityProperties;
    private final JwtFilter jwtFilter;

    /**
     * HTTP에 대해서 ‘인증’과 ‘인가’를 담당하는 메서드이며 필터를 통해 인증 방식과 인증 절차에 대해서 등록하며 설정을 담당하는 메서드
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))// 세션 사용 안함
                .formLogin(AbstractHttpConfigurer::disable) // 기본 로그인 폼 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // http basic 인증 비활성화, Basic 인증은 사용자 이름과 비밀번호를 Base64로 인코딩하여 인증값으로 활용
                .headers(headers -> headers
                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.from("1; mode=block"))) // XSS 공격 방지
                        .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable) // MIME 스니핑 방지
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // ClickJacking 방지
                        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)) // Referrer 비활성화
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self' 'nonce-randomValue'"))// XSS 방지
                );
        http
                .authorizeHttpRequests(auth -> auth
                        // 정적 자원에 대해서 Security를 적용하지 않음으로 설정
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // 특정 url 패턴에 대해서는 인증처리(Authentication 객체 생성) 제외
                        .requestMatchers(jwtSecurityProperties.getSecret().getWhiteList().toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()
                )
                // UsernamePasswordAuthenticationFilter 에서 폼 로그인 인증 처리, 폼 로그인은 MVP(서버 자체에서 화면단까지)에서 사용됨.
                // 그러므로 jwtAuthenticationFilter 에서 인증 객체를 생성하면 이미 인증이 됐으므로 UsernamePasswordAuthenticationFilter는 무시된다.
                .addFilterBefore(jwtFilter, SecurityContextHolderAwareRequestFilter.class);
//                .addFilterAt(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
