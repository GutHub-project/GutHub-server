package com.guthub.guthubserver.config;

import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.domain.user.entity.UserRoleType;
import com.guthub.guthubserver.filter.JWTFilter;
import com.guthub.guthubserver.filter.LoginFilter;
import com.guthub.guthubserver.handler.RefreshTokenLogoutHandler;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
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
// import org.springframework.security.core.userdetails.UserDetailsService; // UserDetailsService import 제거

import java.util.List;

@Configuration
@EnableWebSecurity
@Profile("prod") // "!local"에서 "prod"로 변경
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final AuthenticationSuccessHandler loginSuccessHandler;
    private final AuthenticationSuccessHandler socialSuccessHandler;
    private final JwtService jwtService;
    private final JWTUtil jwtUtil;
    // private final UserDetailsService userDetailsService; // 제거
    private final String frontendUrl;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
                          @Qualifier("LoginSuccessHandler") AuthenticationSuccessHandler loginSuccessHandler,
                          @Qualifier("SocialSuccessHandler") AuthenticationSuccessHandler socialSuccessHandler,
                          JwtService jwtService, JWTUtil jwtUtil, // UserDetailsService 인자 제거
                          @Value("${frontend.url") String frontendUrl) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.loginSuccessHandler = loginSuccessHandler;
        this.socialSuccessHandler = socialSuccessHandler;
        this.jwtService = jwtService;
        this.jwtUtil = jwtUtil;
        // this.userDetailsService = userDetailsService; // 제거
        this.frontendUrl = frontendUrl;
    }

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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withRolePrefix("ROLE_")
                .role(UserRoleType.ADMIN.name()).implies(UserRoleType.USER.name())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        http.oauth2Login(oauth2 -> oauth2.successHandler(socialSuccessHandler));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(
                        "/user",
                        "/user/exist",
                        "/jwt/refresh",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/logout"
                ).permitAll()
                .anyRequest().authenticated()
        );


        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionFixation().migrateSession()
        );

        http.addFilterBefore(new LoginFilter(authenticationManager(authenticationConfiguration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JWTFilter(jwtUtil), org.springframework.security.web.authentication.logout.LogoutFilter.class); // userDetailsService 전달 부분 제거
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
