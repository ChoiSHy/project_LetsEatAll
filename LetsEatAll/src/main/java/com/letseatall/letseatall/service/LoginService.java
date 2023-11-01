package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.User.SignInResultDto;
import com.letseatall.letseatall.data.dto.User.SignUpResultDto;

import java.time.LocalDate;

public interface LoginService {
    SignUpResultDto signUp(String id, String password, String name, LocalDate birthDate, String role);       // 회원 가입 (이후 정보 추가 필요)
    SignInResultDto signIn(String id, String password) throws RuntimeException;         // 로그인 시도
}
