package com.letseatall.letseatall.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.User.*;
import com.letseatall.letseatall.service.LoginService;
import com.letseatall.letseatall.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final LoginService loginService;

    private final UserService userService;

    @Autowired
    /* 생성자 */
    public UserController(UserService userService,
                          LoginService loginService) {
        this.userService = userService;
        this.loginService = loginService;
    }
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-AUTH-TOKEN", value = "로그인 성공 후 받은 access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @GetMapping()
    /* 회원 정보 요구 */
    public ResponseEntity<UserResponseDto> getUser(Long id) {
        UserResponseDto returnUser = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(returnUser);
    }

    @PostMapping("/sign-up")
    /* 회원 가입 */
    public SignUpResultDto signUp(
            //@RequestBody
            //SignUpRequestDto req,

            @ApiParam(value = "ID", required = true) @RequestParam String id,
            @ApiParam(value = "비밀번호", required = true) @RequestParam String password,
            @ApiParam(value = "이름", required = true) @RequestParam String name,
            @DateTimeFormat(pattern="yyyy-MM-dd")
            @ApiParam(value = "생년월일 (yyyy-MM-dd)", required = true) @RequestParam LocalDate birthDate,
            @ApiParam(value = "권한", required = true) @RequestParam String role) {
        LOGGER.info("[signUp] 회원가입을 수행합니다. id : {}, password : ****, name : {}, role : {}", id, name, role);
        SignUpResultDto signUpResultDto = loginService.signUp(id, password,name,birthDate, role);

        LOGGER.info("[signUp] 회원가입을 완료했습니다. id : {}", role);
        return signUpResultDto;
    }

    /*
    public ResponseEntity<UserResponseDto> register(@RequestBody UserDto UserDto){
        UserResponseDto returnUser = userService.saveUser(UserDto);
        if(returnUser.getScore() == -101)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(returnUser);
        else if (returnUser != null)
            return ResponseEntity.status(HttpStatus.OK).body(returnUser);

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }*/
    @PostMapping("/sign-in")
    /* 로그인 시도 */
    public SignInResultDto signIn(
            @ApiParam(value = "ID", required = true) @RequestParam String id,
            @ApiParam(value = "Password", required = true) @RequestParam String password)
            throws RuntimeException {
        LOGGER.info("[signIn] 로그인을 시도하고 있습니다. id : {}, pw : ****", id);
        SignInResultDto signInResultDto = loginService.signIn(id, password);

        if (signInResultDto.getCode() == 0) {
            LOGGER.info("[signIn] 정상적으로 로그인되었습니다. id : {}, token : {}", id,
                    signInResultDto.getToken());
        }
        return signInResultDto;
    }

    /*
    public ResponseEntity<UserResponseDto> tryLogin(@RequestBody LoginRequestDto loginRequestDto){
        UserResponseDto returnUser = userService.tryLogin(loginRequestDto);
        if(returnUser == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else if (returnUser.getId()== -400L){
            System.out.println("[Error]: 400 ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);}
        else
            return ResponseEntity.status(HttpStatus.OK).body(returnUser);
    }*/
    @GetMapping(value = "/exception")
    public void exceptionTest() throws RuntimeException {
        throw new RuntimeException("접근이 금지되었습니다.");
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Map<String, String>> ExceptionHandler(RuntimeException e) {
        HttpHeaders responseHeaders = new HttpHeaders();
        //responseHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        LOGGER.error("ExceptionHandler 호출, {}, {}", e.getCause(), e.getMessage());

        Map<String, String> map = new HashMap<>();
        map.put("error type", httpStatus.getReasonPhrase());
        map.put("code", "400");
        map.put("message", "에러 발생");

        return new ResponseEntity<>(map, responseHeaders, httpStatus);
    }


    @DeleteMapping()
    /* 회원정보 삭제 */
    public ResponseEntity<String> deleteUser(String id) throws Exception {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("[" + id + "] 삭제되었습니다.");
    }
}
