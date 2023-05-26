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
                .uid(savedUser.getId())
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
        System.out.println(foundLogin.getId());
        System.out.println("loginRequestDto: "+loginRequestDto.getPw());
        System.out.println("foundLogin: "+foundLogin.getPw());
        if (foundLogin == null)
            return null;

        else if (loginRequestDto.getPw().compareTo(foundLogin.getPw())!=0){
            return UserResponseDto.builder()
                    .id(-400L)
                    .build();}
        else{
            User foundUser = userRepository.findById(foundLogin.getUid()).get();

            return UserResponseDto.builder()
                    .id(foundUser.getId())
                    .name(foundUser.getName())
                    .score(foundUser.getScore())
                    .birthDate(foundUser.getBirthDate())
                    .build();
        }
    }

    @Override
    public String deleteUser(Long uid) {
        userRepository.deleteById(uid);
        String id = loginRepository.findIdByUid(uid);
        loginRepository.deleteById(id);

        return id;
    }
}
