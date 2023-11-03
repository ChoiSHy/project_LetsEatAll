package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.Login;
import com.letseatall.letseatall.data.dto.User.UserDto;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.dto.User.LoginRequestDto;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.data.repository.LoginRepository;
import com.letseatall.letseatall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final LoginRepository loginRepository;

    @Override
    public UserResponseDto getUser(Long id) {
        User user = userRepository.getById(id);
        UserResponseDto responseUser = UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .score(user.getScore())
                .birthDate(user.getBirthDate())
                .build();
        return responseUser;
    }

    @Override
    public UserResponseDto saveUser(UserDto userDto) {
        if (loginRepository.existsById(userDto.getId()))
            return UserResponseDto.builder().name("id_duplication").score(-101).build();

        User user = new User();
        user.setName(userDto.getName());
        user.setBirthDate(userDto.getBirthDate());
        user.setScore(50);

        User savedUser = userRepository.save(user);

        Login login = Login.builder()
                .id(userDto.getId())
                .pw(userDto.getPw())
                .user(savedUser)
                .build();
        loginRepository.save(login);

        return UserResponseDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .score(savedUser.getScore())
                .birthDate(savedUser.getBirthDate())
                .build();
    }

    @Override
    public void deleteUser(String id) {
        loginRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("[loadUserByUsername] loadUserByUsername 수행. username : {}", username);
        return userRepository.getByUid(username);
    }
}
