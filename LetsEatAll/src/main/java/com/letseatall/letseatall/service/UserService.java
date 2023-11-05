package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDate;

public interface UserService extends UserDetailsService {
    //void deleteUser(String id);
    UserResponseDto getUser(String id);
    UserResponseDto updateUser(String id, String name, LocalDate birthDate);
}
