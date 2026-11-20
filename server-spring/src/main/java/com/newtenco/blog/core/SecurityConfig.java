package com.newtenco.blog.core;

import com.newtenco.blog.core.filter.JwtAuthorizationFilter;
import com.newtenco.blog.core.util.RespFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.addAllowedHeader("*");
        config.setExposedHeaders(List.of("Authorization", "Refresh-Token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http.csrf(csrf -> csrf.disable());
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());
        http.logout(logout -> logout.disable());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> RespFilter.fail(res, 401, "로그인 후 이용해주세요"))
                .accessDeniedHandler((req, res, e) -> RespFilter.fail(res, 403, "권한이 없습니다")));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/join", "/login", "/check-username").permitAll()
                .requestMatchers(HttpMethod.POST, "/reissue", "/logout").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/boards/*/edit").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().denyAll()
        );

        return http.build();
    }
}
