package com.letseatall.letseatall.controller.page;

import com.letseatall.letseatall.controller.UserController;
import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.dto.Category.CategoryDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.dto.User.BadRequestException;
import com.letseatall.letseatall.data.dto.User.SignInRequestDto;
import com.letseatall.letseatall.data.dto.User.SignInResultDto;
import com.letseatall.letseatall.data.repository.CategoryRepository;
import com.letseatall.letseatall.service.LoginService;
import com.letseatall.letseatall.service.RestaurantService;
import com.letseatall.letseatall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/page")
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
                          LoginService loginService){
        this.restaurantService=restaurantService;
        this.categoryRepository = categoryRepository;
        this.userService=userService;
        this.loginService= loginService;
    }
    @GetMapping("/login")
    public String loginPage(Model model){
        return "/login/login";
    }
    @PostMapping("/login")
    public String tryLogin(HttpServletRequest req, HttpServletResponse res){
        String id = req.getParameter("id");
        String pw = req.getParameter("password");
        LOGGER.info("[tryLogin] id={}, pw={}",id,pw);
        try {
            SignInResultDto result = loginService.signIn(id, pw);
            Cookie token = new Cookie("X-AUTH-TOKEN", result.getToken());
            res.addCookie(token);
            return "redirect:/page/main";
        }catch (RuntimeException e){
            throw e;
        }


    }
    @GetMapping("/sign-up")
    public String sign_up(Model model){
        return "login/sign-up";
    }

    @GetMapping()
    public String mainPage(@CookieValue(name="X-AUTH-TOKEN", required=false) String token,
            Model model) throws UnsupportedEncodingException {
        String url_ = "restaurant/";
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDto> dtoList = new ArrayList<>();

        for (Category category : categoryList) {
            CategoryDto dto = CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .url("restaurant/"+category.getId()+"/0" )
                    .build();
            LOGGER.info("url = {}",dto.getUrl());
            dtoList.add(dto);
        }
        model.addAttribute("categories", dtoList);
        return "main/main";
    }


    @GetMapping("/review/create/{mid}")
    public String reviewWriter(@PathVariable(value = "mid") Long mid, Model model){
        model.addAttribute("menu_id" , mid);
        return "redirect:ReviewWriter";
    }

}
