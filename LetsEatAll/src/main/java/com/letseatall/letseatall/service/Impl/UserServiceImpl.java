package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

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
}
