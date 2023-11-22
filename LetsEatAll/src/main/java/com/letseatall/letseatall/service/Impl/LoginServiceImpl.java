package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.common.CommonResponse;
import com.letseatall.letseatall.config.security.JwtTokenProvider;
import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.Entity.Preference;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.User.BadRequestException;
import com.letseatall.letseatall.data.dto.User.SignInResultDto;
import com.letseatall.letseatall.data.dto.User.SignUpResultDto;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.repository.CategoryRepository;
import com.letseatall.letseatall.data.repository.PreferenceRepository;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.service.LoginService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final Logger LOGGER = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final RedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PreferenceRepository preferenceRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public SignUpResultDto signUp(String id, String password, String name, LocalDate birthDate, String role)
            throws RuntimeException {
        LOGGER.info("[getSignUpResult] 회원 가입 정보 전달");
        User user;
        if (role.equalsIgnoreCase("admin")) {
            user = User.builder()
                    .uid(id)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .birthDate(birthDate)
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();
        } else {
            user = User.builder()
                    .uid(id)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .birthDate(birthDate)
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

        }
        try {
            LOGGER.info("[signUp] 회원정보 저장중");
            User savedUser = userRepository.save(user);
            LOGGER.info("[signUp] 회원정보 저장 완료");
            SignUpResultDto signUpResultDto = new SignInResultDto();
            LOGGER.info("[getSignUpResult] userEntity 값이 들어왔는지 확인 후 결과값 주입");
            if (!savedUser.getName().isEmpty()) {
                LOGGER.info("[getSignUpResult] 정상 처리 완료");
                setSuccessResult(signUpResultDto);
            } else {
                LOGGER.info("[getSignUpResult] 실패 처리 완료");
                setFailResult(signUpResultDto);
            }
            return signUpResultDto;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Transactional
    public SignInResultDto signIn(String id, String password) throws RuntimeException {
        LOGGER.info("[getSignInResult] signDataHandler 로 회원 정보 요청");
        User user = userRepository.getByUid(id);
        LOGGER.info("[getSignInResult] Id : {}", id);

        LOGGER.info("[getSignInResult] 패스워드 비교 수행");
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException();
        }
        LOGGER.info("[getSignInResult] 패스워드 일치");

        LOGGER.info("[getSignInResult] SignInResultDto 객체 생성");
        String token = jwtTokenProvider.createToken(String.valueOf(user.getUid()),
                user.getRoles());
        SignInResultDto signInResultDto = SignInResultDto.builder()
                .token(token)
                .name(user.getName())
                .id(user.getUid())
                .build();

        LOGGER.info("[getSignInResult] SignInResultDto 객체에 값 주입");
        setSuccessResult(signInResultDto);
        LOGGER.info("[getSignInResult] redisTemplate에 id, token 저장 시도");
        redisTemplate.opsForValue().set("JWT_TOKEN:" + id, token);
        LOGGER.info("[getSignInResult] redisTemplate에 id, token 저장 완료");

        return signInResultDto;
    }

    // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
    private void setSuccessResult(SignUpResultDto result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    // 결과 모델에 api 요청 실패 데이터를 세팅해주는 메소드
    private void setFailResult(SignUpResultDto result) {
        result.setSuccess(false);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
    }

    public boolean existId(String id) {
        LOGGER.info("[existId] : ID 중복 체크 id: {}", id);
        return userRepository.existsByUid(id);
    }

    public boolean changeUserPassword_check(String id, String name, LocalDate birthDate) {
        //UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LOGGER.info("[changeUserPassword_check] : User 정보 불러오기 시작. id: {}", id);
        User user = userRepository.getByUid(id);
        if (user != null) {
            LOGGER.info("[changeUserPassword_check] : User 정보 불러오기 성공. id: {}", id);/*
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            try{
                identityVerification(userDetails.getUsername(), user.getUsername());
            }catch (BadRequestException e){
                throw e;
            }*/

            if (!user.getName().equals(name)) {
                LOGGER.info("[changeUserPassword_check] : User 정보 불일치. name: {} vs {}", user.getName(), name);
                return false;
            }
            if (!user.getBirthDate().isEqual(birthDate)) {
                LOGGER.info("[changeUserPassword_check] : User 정보 불일치. birthDate: {} vs {}", user.getBirthDate().toString(), birthDate.toString());
                return false;
            }
            LOGGER.info("[changeUserPassword_check] : User 정보 일치.");
            return true;
        }
        LOGGER.info("[changeUserPassword_check] : User 정보 불러오기 실패. id: {}", id);
        return false;
    }

    public void changeUserPassword(String id, String newPassword) {
        LOGGER.info("[changeUserPassword] : User 정보 불러오기 시작. id: {}", id);
        User user = userRepository.getByUid(id);
        if (user != null && newPassword != null) {
            LOGGER.info("[changeUserPassword] : User 정보 불러오기 성공. id: {}", id);
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            try {
                identityVerification(userDetails.getUsername(), user.getUsername());
            } catch (BadRequestException e) {
                throw e;
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            try {
                LOGGER.info("[changeUserPassword] : User 정보 저장 시도.");
                userRepository.save(user);
                LOGGER.info("[changeUserPassword] : User 정보 저장 성공");
            } catch (RuntimeException e) {
                LOGGER.info("[changeUserPassword] : User 정보 저장 실패");
                throw e;
            }
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        LOGGER.info("[ LOG-OUT ] 현재 요청자 정보 불러오는 중");
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOGGER.info("[ LOG-OUT ] 현재 요청자 정보 불러오기 완료 id: {}", user.getUsername());
        if (redisTemplate.opsForValue().get("JWT_TOKEN:" + user.getUsername()) != null) {
            LOGGER.info("[ LOG-OUT ] Redis에서 토큰 정보 삭제 시도");
            redisTemplate.delete("JWT_TOKEN:" + user.getUsername());
            LOGGER.info("[ LOG-OUT ] Redis에서 토큰 정보 삭제 완료");
        }
    }

    private void identityVerification(String userName, String tokenName) {
        LOGGER.info("[identityVerification] : token 정보와 검색 정보 일치 여부 검사");
        if (!tokenName.equals(userName)) {
            LOGGER.info("[identityVerification] : token 정보 불일치");
            throw new BadRequestException("토큰 불일치");
        }
        LOGGER.info("[identityVerification] : token 정보와 검색 정보 일치");
    }



}