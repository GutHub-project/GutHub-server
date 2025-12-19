package com.guthub.guthubserver.config;

import com.guthub.guthubserver.domain.jwt.service.JwtService;
import com.guthub.guthubserver.domain.user.entity.UserRoleType;
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
@RequiredArgsConstructor
@Profile("prod")
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtService jwtService;
    @Qualifier("SocialSuccessHandler")
    private final AuthenticationSuccessHandler socialSuccessHandler;
    @Qualifier("LoginSuccessHandler")
    private final AuthenticationSuccessHandler loginSuccessHandler;

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(jwtUtil);
    }

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
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/test/token",
                        "/login/**",
                        "/oauth2/**",
                        "/user",
                        "/user/exist",
                        "/jwt/refresh",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                .requestMatchers("/user/profile").hasAnyRole("TEMP", "USER")
                .requestMatchers("/api/**").hasRole("USER")

                .anyRequest().authenticated()
        );

        http.oauth2Login(oauth2 ->
                oauth2.successHandler(socialSuccessHandler)
        );

        http.addFilterBefore(
                jwtFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        http.addFilterBefore(
                new LoginFilter(
                        authenticationManager(authenticationConfiguration),
                        loginSuccessHandler
                ),
                UsernamePasswordAuthenticationFilter.class
        );

        http.logout(logout -> logout
                .addLogoutHandler(new RefreshTokenLogoutHandler(jwtService, jwtUtil))
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                })
        );

        http.exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) ->
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
                .accessDeniedHandler((req, res, ex) ->
                        res.sendError(HttpServletResponse.SC_FORBIDDEN)
                )
        );

        return http.build();
    }
}
