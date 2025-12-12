package com.guthub.guthubserver.filter;

import com.guthub.guthubserver.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;


    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // /logout 경로에 대한 요청은 JWT 검증을 건너뛴다.
        if (request.getRequestURI().equals("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorization.startsWith("Bearer ")) {
            throw new ServletException("Invalid JWT token");
        }

        // 토큰 파싱
        String accessToken = authorization.split(" ")[1];

        if (jwtUtil.isValid(accessToken, true)) {
            String username = jwtUtil.getUsername(accessToken);
            String role = jwtUtil.getRole(accessToken);

            // UserDetailsService를 사용하여 UserDetails를 로드하는 부분 제거
            // UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            // UsernamePasswordAuthenticationToken의 principal로 username (String)을 설정
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities); // userDetails 대신 username 사용
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"토큰 만료 또는 유효하지 않은 토큰\"}");
            return;
        }
    }
}
