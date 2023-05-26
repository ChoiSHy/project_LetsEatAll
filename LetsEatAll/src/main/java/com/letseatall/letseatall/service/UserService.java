package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.Login;
import com.letseatall.letseatall.data.dto.User.UserDto;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.dto.User.LoginRequestDto;

public interface UserService {
    UserResponseDto getUser(Long id);
    UserResponseDto saveUser(UserDto UserDto);
    UserResponseDto tryLogin(LoginRequestDto loginRequestDto);
    String deleteUser(Long id);
}
