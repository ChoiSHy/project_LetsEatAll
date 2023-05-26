package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.User.UserDto;
import com.letseatall.letseatall.data.dto.User.UserResponseDto;
import com.letseatall.letseatall.data.dto.User.LoginRequestDto;
import com.letseatall.letseatall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/User")
public class UserController {

    private final UserService UserService;

    @Autowired
    /* 생성자 */
    public UserController(UserService UserService){
        this.UserService = UserService;
    }

    @GetMapping()
    /* 회원 정보 요구 */
    public ResponseEntity<UserResponseDto> getUser(Long id){
        UserResponseDto returnUser = UserService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(returnUser);
    }
    @PostMapping()
    /* 회원 가입 */
    public ResponseEntity<UserResponseDto> register(@RequestBody UserDto UserDto){
        UserResponseDto returnUser = UserService.saveUser(UserDto);
        if(returnUser.getScore() == -101)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(returnUser);
        else if (returnUser != null)
            return ResponseEntity.status(HttpStatus.OK).body(returnUser);

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @PostMapping("/login")
    /* 로그인 시도 */
    public ResponseEntity<UserResponseDto> tryLogin(@RequestBody LoginRequestDto loginRequestDto){
        UserResponseDto returnUser = UserService.tryLogin(loginRequestDto);
        if(returnUser == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else if (returnUser.getId()== -400L){
            System.out.println("[Error]: 400 ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);}
        else
            return ResponseEntity.status(HttpStatus.OK).body(returnUser);
    }

    @DeleteMapping()
    /* 회원정보 삭제 */
    public ResponseEntity<String> deleteUser(Long id) throws Exception{
        String deletedId = UserService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.OK).body("ID: "+ deletedId + "삭제 완료되었습니다.");
    }
}