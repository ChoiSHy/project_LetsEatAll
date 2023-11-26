package com.letseatall.letseatall.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.Menu.MenuListDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.dto.User.*;
import com.letseatall.letseatall.service.Impl.PreferenceService;
import com.letseatall.letseatall.service.LoginService;
import com.letseatall.letseatall.service.UserService;
import com.letseatall.letseatall.service.recommender.Recommender;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
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
import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final LoginService loginService;

    private final UserService userService;
    private final PreferenceService preferenceService;

    private final Recommender recommender;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 받은 access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @GetMapping()
    /* 회원 정보 요구 */
    public ResponseEntity getUser(@RequestParam String id) {
        UserResponseDto returnUser = null;

        try {
            LOGGER.info("[getUser] : 사용자 정보 접근 시도");
            returnUser = userService.getUser(id);
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
        SignUpResultDto signUpResultDto;
        LOGGER.info("[signUp] 회원가입을 수행합니다. id : {}, password : ****, name : {}, role : {}", req.getId(), req.getName(),
                role);
        try {
            signUpResultDto = loginService.signUp(req.getId(), req.getPassword(), req.getName(), req.getBirthDate(), role);
            LOGGER.info("[signUp] 회원가입 정보 저장완료");
        } catch (Exception e) {
            LOGGER.info("[signUp] 회원가입 실패하였습니다. id : {}", req.getId());
            throw new BadRequestException("회원가입 실패");
        }

        return ResponseEntity.status(HttpStatus.OK).body(signUpResultDto);
    }


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

    @PostMapping("/score")
    public ResponseEntity changeScore(@RequestBody UserScoreDto req) {
        LOGGER.info("[changeScore] : Request 도착");
        try {
            UserScoreDto resDto = userService.changeScore(req.getId(), req.getScore());
            return ResponseEntity.ok().body(resDto);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("데이터 수정 실패");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request) {
        LOGGER.info("[ LOG-OUT ] 로그아웃 시도 중");
        loginService.logout(request);
        LOGGER.info("[ LOG-OUT ] 로그아웃 완료");

        return ResponseEntity.ok().build();
    }

    @GetMapping("/recommend/{user_id}")
    public ResponseEntity<List<MenuListDto>> recommend_menu(@PathVariable long user_id) throws IOException {
        try {
            List<MenuListDto> mlist = recommender.run(user_id);
            return ResponseEntity.ok().body(mlist);
        } catch (BadRequestException e) {
            throw e;
        }catch (IOException e){
            throw e;
        }
    }

}
