package com.letseatall.letseatall.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 어플리케이션의 보안 설정
 */
@Configuration
@EnableWebSecurity // Spring Security에 대한 디버깅 모드를 사용하기 위한 어노테이션 (default : false)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Autowired
    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider,
                                 RedisTemplate redisTemplate) {

        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic().disable() // REST API는 UI를 사용하지 않으므로 기본설정을 비활성화

                .csrf().disable() // REST API는 csrf 보안이 필요 없으므로 비활성화

                .sessionManagement()
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS) // JWT Token 인증방식으로 세션은 필요 없으므로 비활성화

                .and()
                .authorizeRequests() // 리퀘스트에 대한 사용권한 체크
                .antMatchers(HttpMethod.POST, "/user/password/update", "/user/score").authenticated()
                .antMatchers(HttpMethod.POST, "/restaurant/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/restaurant/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/restaurant/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/menu/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/menu/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/menu/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/review/**").authenticated()
                .antMatchers(HttpMethod.PUT, "/review/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/review/**").authenticated()
                .antMatchers(HttpMethod.GET, "/review/user/me").authenticated()
                .antMatchers("**exception**").permitAll()
                .antMatchers(HttpMethod.GET, "/page/review/like").authenticated()
                .antMatchers(HttpMethod.GET, "/page/recommend").authenticated()
                .anyRequest().permitAll()

                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .formLogin()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class); // JWT Token 필터 추가

    }


    /**
     * Swagger 페이지 접근에 대한 예외 처리
     *
     * @param webSecurity
     */
    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity
                .ignoring()
                .antMatchers("/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception");
    }
}