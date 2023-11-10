package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.User.BadRequestException;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.dto.User.UserScoreDto;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.service.UserService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
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
        User user = null;

        LOGGER.info("[ServiceImpl/getUser] : user 정보 검색 시작");
        user = userRepository.getByUid(id);
        if(user == null) {
            LOGGER.info("[ServiceImpl/getUser] : user 정보 검색 실패");
            throw new EntityNotFoundException();
        }

        UserResponseDto udto = UserResponseDto.builder()
                .id(user.getUid())
                .name(user.getName())
                .birthDate(user.getBirthDate())
                .score(user.getScore())
                .build();
        return udto;
    }
    public UserResponseDto updateUser(String id, String name, LocalDate birthDate, String tokenName){
        LOGGER.info("[updateUser] : user 데이터 탐색 시작");
        User user = userRepository.getByUid(id);
        if(user!=null){
            LOGGER.info("[updateUser] : user 데이터 탐색 성공");
            try {
                LOGGER.info("[updateUser] : user 데이터와 토큰의 정보 비교");
                identityVerification(user.getUsername(), tokenName);
            }catch (BadRequestException e){
                LOGGER.info("[updateUser] : user 데이터와 토큰의 정보 불일치");
                throw e;
            }
            LOGGER.info("[updateUser] : user 데이터와 토큰의 정보 일치. id: {}",tokenName);
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
        throw new EntityNotFoundException();
    }
    private void identityVerification(String userName, String tokenName){
        LOGGER.info("[identityVerification] : token 정보와 검색 정보 일치 여부 검사");
        if(!tokenName.equals(userName)){
            LOGGER.info("[identityVerification] : token 정보 불일치");
            throw new BadRequestException("토큰 불일치");
        }
        LOGGER.info("[identityVerification] : token 정보와 검색 정보 일치");
    }

    @Override
    public UserScoreDto changeScore(String id, int score) {
        LOGGER.info("[changeScore] : 해당 계정에 대한 점수 수정 시작 id: {}, score: {}", id, score);
        User user = userRepository.getByUid(id);
        if(user == null){
            LOGGER.info("[changeScore] : 해당 계정을 찾을 수 없음.");
            throw new EntityNotFoundException("해당 계정을 찾을 수 없음.");
        }
        user.setScore(user.getScore() + score);

        LOGGER.info("[changeScore] : 수정된 정보 저장 시작");
        User changedUser = userRepository.save(user);

        if(changedUser == null){
            LOGGER.info("[changeScore] : 데이터 수정 실패");
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED);
        }
        LOGGER.info("[changeScore] : 데이터 수정 성공");
        UserScoreDto responseDto = UserScoreDto.builder()
                .id(changedUser.getUid())
                .score(changedUser.getScore())
                .build();
        return responseDto;
    }
}
