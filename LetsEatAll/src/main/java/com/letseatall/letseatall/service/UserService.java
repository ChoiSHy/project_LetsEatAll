package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.Login;
import com.letseatall.letseatall.data.dto.User.UserDto;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.dto.User.LoginRequestDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    UserResponseDto getUser(Long id);
    UserResponseDto saveUser(UserDto UserDto);
    void deleteUser(String id);
}
