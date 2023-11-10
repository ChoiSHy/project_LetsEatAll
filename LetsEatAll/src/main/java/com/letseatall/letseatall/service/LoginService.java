package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.User.SignInResultDto;
import com.letseatall.letseatall.data.dto.User.SignUpResultDto;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.time.LocalDate;

public interface LoginService {
    SignUpResultDto signUp(String id, String password, String name, LocalDate birthDate, String role) throws Exception;       // 회원 가입 (이후 정보 추가 필요)
    SignInResultDto signIn(String id, String password) throws RuntimeException;         // 로그인 시도
    boolean existId(String id);
    boolean changeUserPassword_check(String id, String name, LocalDate birthDate);
    void changeUserPassword(String id, String password);
    void logout(HttpServletRequest request);
}
