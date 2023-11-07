package com.letseatall.letseatall.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.User.*;
import com.letseatall.letseatall.service.LoginService;
import com.letseatall.letseatall.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpResponse;
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
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 받은 access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @GetMapping()
    /* 회원 정보 요구 */
    public ResponseEntity getUser(@RequestParam String id) {
        LOGGER.info("[getUser] : security로부터 토큰의 사용자 정보 가져오기");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserResponseDto returnUser = null;

        try {
            LOGGER.info("[getUser] : 사용자 정보 접근 시도");
            returnUser = userService.getUser(id, userDetails.getUsername());
        } catch (EntityNotFoundException e) {
            LOGGER.info("[getUser] : 대상 정보를 찾지 못했습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("대상을 찾지 못했습니다.");
        } catch (BadRequestException e) {
            LOGGER.info("[getUser] : 검색 대상과 토큰 정보 불일치");
            return ResponseEntity.status(401).body("토큰과 검색 대상 불일치");
        }
        LOGGER.info("[getUser] : 사용자 정보 전송완료. name : {}", returnUser.getName());
        return ResponseEntity.status(HttpStatus.OK).body(returnUser);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 받은 access_token",
                    required = true, dataType = "String", paramType = "header")})
    @PostMapping("/password/user-check")
    public ResponseEntity checkUserData(@RequestBody UserDto userDto) {
        String id = userDto.getId();
        String name = userDto.getName();
        LocalDate birthDate = userDto.getBirthDate();
        LOGGER.info("[checkUserData] : 비밀번호 변경 전, 사용자 정보 확인");
        try {
            if (loginService.changeUserPassword_check(id, name, birthDate)) {
                LOGGER.info("[checkUserData] : 회원 정보 확인 완료");
                return ResponseEntity.ok("회원 정보 확인");
            }
        } catch (BadRequestException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
        LOGGER.info("[checkUserData] : 회원 정보 불일치");
        throw new BadRequestException("회원 정보 불일치");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 받은 access_token",
                    required = true, dataType = "String", paramType = "header")})
    @PostMapping("/password/update")
    public ResponseEntity updatePassword(@RequestBody SignInRequestDto dto) {
        LOGGER.info("[updatePassword] : 비밀번호 변경 시작");
        try {
            loginService.changeUserPassword(dto.getId(), dto.getPassword());
            LOGGER.info("[updatePassword] : 비밀번호 변경 완료");
            return ResponseEntity.ok("비밀번호 변경 완료");
        } catch (BadRequestException e) {
            LOGGER.info("[updatePassword] : 토큰 정보 불일치");
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.info("[updatePassword] : 비밀번호 변경 실패");
            throw (new BadRequestException("변경 실패"));
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "jwt token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/update")
    public ResponseEntity updateUser(@RequestBody UserDto userDto) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String id = userDto.getId();
        String name = userDto.getName();
        LocalDate birthDate = userDto.getBirthDate();
        LOGGER.info("[updateUser] : 요청으로부터 데이터 추출");
        try {
            UserResponseDto savedUser = userService.updateUser(id, name, birthDate, userDetails.getUsername());

            if (savedUser != null) {
                LOGGER.info("[updateUser] : 데이터 저장 완료. savedUser: {}", savedUser.toString());
                return ResponseEntity.status(HttpStatus.OK).body(savedUser);
            } else {
                LOGGER.info("[updateUser] : 데이터 저장 실패.");
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("데이터 수정 실패");
            }
        } catch (EntityNotFoundException e) {
            LOGGER.info("[updateUser] : 대상 검색 실패.");
            return ResponseEntity.status(404).body("대상을 찾지 못했습니다.");
        } catch (BadRequestException e) {
            LOGGER.info("[updateUser] : 토큰 정보 불일치");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 정보 불일치");
        }

    }

    @PostMapping("/sign-up")
    /* 회원 가입 */
    public ResponseEntity signUp(@RequestBody SignUpRequestDto req) {
        String role = "USER";
        LOGGER.info("[signUp] 회원가입을 수행합니다. id : {}, password : ****, name : {}, role : {}", req.getId(), req.getName(),
                role);
        try {
            SignUpResultDto signUpResultDto = loginService.signUp(req.getId(), req.getPassword(), req.getName(), req.getBirthDate(), role);
            LOGGER.info("[signUp] 회원가입을 완료했습니다. id : {}", req.getId());
            return ResponseEntity.status(HttpStatus.OK).body(signUpResultDto);
        } catch (Exception e) {
            LOGGER.info("[signUp] 회원가입 실패하였습니다. id : {}", req.getId());
            throw new BadRequestException("회원가입 실패");
        }
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
            @RequestBody SignInRequestDto dto) throws RuntimeException {
        LOGGER.info("[signIn] 로그인을 시도하고 있습니다. id : {}, pw : ****", dto.getId());
        SignInResultDto signInResultDto = loginService.signIn(dto.getId(), dto.getPassword());

        if (signInResultDto.getCode() == 0) {
            LOGGER.info("[signIn] 정상적으로 로그인되었습니다. id : {}, token : {}", dto.getId(),
                    signInResultDto.getToken());
        }
        return signInResultDto;
    }

    @PostMapping(value = "/id-check")
    public ResponseEntity<?> checkDuplication(@RequestBody IdCheckDto icd)
            throws BadRequestException {
        LOGGER.info("[checkDuplication] : id 중복 검사 시작 : {}", icd.getId());
        if (loginService.existId(icd.getId())) {
            LOGGER.info("[checkDuplication] : 중복된 id 존재 : {}", icd.getId());
            throw new BadRequestException("이미 사용중인 아이디입니다.");
        } else {
            LOGGER.info("[checkDuplication] : 사용 가능한 id : {}", icd.getId());
            return ResponseEntity.ok("사용 가능한 아이디입니다.");
        }
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
        map.put("message", e.getMessage());

        return new ResponseEntity<>(map, responseHeaders, httpStatus);
    }


}
