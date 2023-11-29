package com.letseatall.letseatall.controller.page;

import com.letseatall.letseatall.controller.UserController;
import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.Category.CategoryDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.dto.User.*;
import com.letseatall.letseatall.data.repository.CategoryRepository;
import com.letseatall.letseatall.service.LoginService;
import com.letseatall.letseatall.service.RestaurantService;
import com.letseatall.letseatall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spi.service.contexts.SecurityContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class PageController {
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final RestaurantService restaurantService;
    private final UserService userService;
    private final LoginService loginService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public PageController(RestaurantService restaurantService,
                          CategoryRepository categoryRepository,
                          UserService userService,
                          LoginService loginService) {
        this.restaurantService = restaurantService;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.loginService = loginService;
    }

    @GetMapping("page/login")
    public String loginPage(Model model) {
        return "login/Login";
    }

    @PostMapping("page/login")
    public String tryLogin(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getParameter("id");
        String pw = req.getParameter("password");
        LOGGER.info("[tryLogin] id={}, pw={}", id, pw);
        try {
            SignInResultDto result = loginService.signIn(id, pw);
            Cookie token = new Cookie("token", result.getToken());
            token.setPath("/");
            token.setMaxAge(Integer.MAX_VALUE);
            res.addCookie(token);

            return "redirect:/";
        } catch (RuntimeException e) {
            throw e;
        }


    }

    @GetMapping("page/sign-up")
    public String sign_up(Model model) {
        return "login/sign-up";
    }
    @PostMapping("page/sign-up")
    public String sign_up_try(PageSignUpRequestDto dto, Model model) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(dto);
        SignUpResultDto result = restTemplate.postForObject(
                "http://localhost:8080/user/sign-up",
                dto,
                SignUpResultDto.class
        );
        if(result.isSuccess())
            return "redirect:/";
        else{
            model.addAttribute("msg", result.getMsg());
            return "forward:login/sign-up";
        }
    }

    @GetMapping()
    public String mainPage(
            @CookieValue(name = "token", required = false) String token,
            Model model) throws UnsupportedEncodingException {
        String url_ = "restaurant/";
        System.out.println(token);
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDto> dtoList = new ArrayList<>();

        for (Category category : categoryList) {
            CategoryDto dto = CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .url("page/restaurant/" + category.getId() + "/0")
                    .build();
            LOGGER.info("url = {}", dto.getUrl());
            dtoList.add(dto);
        }

        model.addAttribute("categories", dtoList);
        model.addAttribute("token", token);
        return "main/main";

    }

    @GetMapping("/page/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, response, authentication); // 로그아웃 처리
        deleteTokenCookie(response); // 쿠키 삭제
        return "redirect:/"; // 로그아웃 후 리다이렉트할 경로
    }

    private void deleteTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @GetMapping("/search")
    public void search(HttpServletRequest req, HttpServletResponse res,
                       Model model) throws IOException {
        String key = req.getParameter("key");
        if (key.equals("r_name")) {
            res.sendRedirect("/page/restaurant/search/" + URLEncoder.encode(req.getParameter("keyword")) + "/0");
        }

    }
}
