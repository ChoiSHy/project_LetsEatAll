package com.letseatall.letseatall.config.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.lang.model.type.ErrorType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(servletRequest);
        LOGGER.info("[doFilterInternal] token 값 추출 완료. token : {}", token);

        LOGGER.info("[doFilterInternal] token 값 유효성 체크 시작");
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String key = "JWT_TOKEN:" + jwtTokenProvider.getUsername(token);
            String storedToken = redisTemplate.opsForValue().get(key);
            LOGGER.info("[doFilterInternal] redis 저장된 값 (key : {})");
            LOGGER.info("[doFilterInternal] redis 저장된 값 (storedToken : {})", storedToken);

            if (redisTemplate.hasKey(key) && storedToken != null) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOGGER.info("[doFilterInternal] token 값 유효성 체크 완료");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}