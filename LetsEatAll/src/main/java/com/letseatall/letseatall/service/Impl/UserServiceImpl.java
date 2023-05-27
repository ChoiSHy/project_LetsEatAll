package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.Login;
import com.letseatall.letseatall.data.dto.User.UserDto;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.dto.User.LoginRequestDto;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.data.repository.LoginRepository;
import com.letseatall.letseatall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LoginRepository loginRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, LoginRepository loginRepository) {
        this.userRepository = userRepository;
        this.loginRepository = loginRepository;
    }

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

        User user = User.builder()
                .name(userDto.getName())
                .birthDate(userDto.getBirthDate())
                .score(50)
                .build();
        User savedUser = userRepository.save(user);

        System.out.println(savedUser.getId());

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
    public UserResponseDto tryLogin(LoginRequestDto loginRequestDto) {
        Login foundLogin = loginRepository.findById(loginRequestDto.getId()).get();
        if(foundLogin == null)
            return null;

        if (foundLogin.getPw().compareTo(loginRequestDto.getPw()) != 0)
            return UserResponseDto.builder().id(-400L).name("id_duplication").build();

        else{
            User foundUser = foundLogin.getUser();
            UserResponseDto responseDto = UserResponseDto.builder()
                    .id(foundUser.getId())
                    .name(foundUser.getName())
                    .birthDate(foundUser.getBirthDate())
                    .score(foundUser.getScore())
                    .build();
            return responseDto;
        }
    }

    @Override
    public void deleteUser(String id) {
        loginRepository.deleteById(id);
    }
}
