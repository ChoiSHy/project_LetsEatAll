package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    //void deleteUser(String id);
    UserResponseDto getUser(String id);
}
