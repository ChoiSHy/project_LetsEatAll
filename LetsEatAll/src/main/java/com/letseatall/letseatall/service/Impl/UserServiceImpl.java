package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.service.UserService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
/*
    public void deleteUser(String id) {
        User user = userRepository.getByUid(id);
        LOGGER.info("[deleteUser] : UserServiceImpl 접근. repository로부터 User 정보 받음");
        LOGGER.info("[deleteUser] : roles 리스트 수정 시작");

        LOGGER.info("[deleteUser] : roles 리스트 수정 완료");
        LOGGER.info("[deleteUser] : 해당 user와 관련된 role 제거 완료");
        userRepository.deleteByUid(id);
    }
*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("[loadUserByUsername] loadUserByUsername 수행. username : {}", username);
        return userRepository.getByUid(username);
    }

    public UserResponseDto getUser(String id){
        User user = userRepository.getByUid(id);
        UserResponseDto udto = UserResponseDto.builder()
                .id(user.getUid())
                .name(user.getName())
                .birthDate(user.getBirthDate())
                .score(user.getScore())
                .build();
        return udto;
    }
    public UserResponseDto updateUser(String id, String name, LocalDate birthDate){
        LOGGER.info("[updateUser] : user 데이터 탐색 시작");
        User user = userRepository.getByUid(id);
        if(user!=null){
            LOGGER.info("[updateUser] : user 데이터 탐색 성공");
            user.setName(name);
            user.setBirthDate(birthDate);
            LOGGER.info("[updateUser] : user 데이터 수정 시작");
            User savedUser = userRepository.save(user);
            if(savedUser != null){
                LOGGER.info("[updateUser] : user 데이터 수정 성공");
                UserResponseDto responseDto = UserResponseDto.builder()
                        .id(savedUser.getUid())
                        .name(savedUser.getName())
                        .birthDate(savedUser.getBirthDate())
                        .score(savedUser.getScore())
                        .build();
                return responseDto;
            }
            else{
                LOGGER.info("[updateUser] : user 데이터 수정 실패");
                return null;
            }

        }
        else
            LOGGER.info("[updateUser] : user 데이터 탐색 실패");
        throw new RuntimeException();
    }
}
