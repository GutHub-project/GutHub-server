package com.guthub.guthubserver.config;

import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.filter.JWTFilter;
import com.guthub.guthubserver.filter.LoginFilter;
import com.guthub.guthubserver.handler.RefreshTokenLogoutHandler;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.core.userdetails.UserDetailsService; // UserDetailsService import 추가

import java.util.List;
// import org.springframework.security.core.authority.SimpleGrantedAuthority; // 제거
// import org.springframework.security.core.context.SecurityContextHolder; // 제거
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 제거


@Configuration
@EnableWebSecurity
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class DevSecurityConfig {

    private final JWTUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // UserDetailsService 주입 추가
    @Qualifier("SocialSuccessHandler")
    private final AuthenticationSuccessHandler socialSuccessHandler;
    @Qualifier("LoginSuccessHandler")
    private final AuthenticationSuccessHandler loginSuccessHandler;


    @Value("${frontend.url}")
    private String frontendUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl, "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/test/token", "/login/**", "/oauth2/**", "/logout", "/jwt/refresh").permitAll() // /jwt/refresh 추가
                .anyRequest().authenticated());

        // 소셜 로그인 설정 추가
        http.oauth2Login(oauth2 -> oauth2.successHandler(socialSuccessHandler));

        // JWT 필터 및 로그인/로그아웃 필터 추가
        http.addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil, userDetailsService), org.springframework.security.web.authentication.logout.LogoutFilter.class); // userDetailsService 전달
        http.logout(logout -> logout
                .addLogoutHandler(new RefreshTokenLogoutHandler(jwtService, jwtUtil))
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Logout successful");
                    response.getWriter().flush();
                })
        );

        http.exceptionHandling(e -> e
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN))
        );

        return http.build();
    }
}
