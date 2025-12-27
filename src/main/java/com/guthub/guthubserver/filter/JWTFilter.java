package com.guthub.guthubserver.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guthub.guthubserver.global.exception.ErrorCode;
import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // logout 경로에 대한 요청은 JWT 검증을 건너뛴다.
        if (request.getRequestURI().equals("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 파싱
        String accessToken = authorization.substring(BEARER_PREFIX.length());

        // 토큰 만료 여부 확인
        if (jwtUtil.isExpired(accessToken)) {
            sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return;
        }

        // access 토큰만 허용
        if (!jwtUtil.isValid(accessToken, true)) {
            sendErrorResponse(response, ErrorCode.TOKEN_INVALID);
            return;
        }

        setAuthentication(accessToken);
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken); // "TEMP" | "USER"

        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", errorCode.getHttpStatus().value());
        errorResponse.put("message", errorCode.getMessage());
        errorResponse.put("code", errorCode.getCode());

        PrintWriter writer = response.getWriter();
        writer.print(objectMapper.writeValueAsString(errorResponse));
        writer.flush();
    }
}
