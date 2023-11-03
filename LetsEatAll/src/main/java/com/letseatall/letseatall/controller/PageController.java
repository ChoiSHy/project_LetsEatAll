package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.dto.Category.CategoryDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.dto.User.SignInResultDto;
import com.letseatall.letseatall.data.repository.CategoryRepository;
import com.letseatall.letseatall.service.RestaurantService;
import io.swagger.annotations.ApiParam;
import org.apache.coyote.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/page")
public class PageController {
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final RestaurantService restaurantService;
    private final CategoryRepository categoryRepository;
    @Autowired
    public PageController(RestaurantService restaurantService,
                          CategoryRepository categoryRepository){
        this.restaurantService=restaurantService;
        this.categoryRepository = categoryRepository;
    }
    @GetMapping("/login")
    public String index(Model model){
        return "/login/Login";
    }
    @GetMapping("/sign-up")
    public String sign_up(Model model){
        return "login/sign-up";
    }

    @GetMapping("/main")
    public String mainPage(Model model){
        String url_ = "http://localhost:8080/restaurant/";
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryDto> dtoList = new ArrayList<>();

        for (Category category : categoryList) {
            CategoryDto dto = CategoryDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .url(url_+category.getId()+"/0")
                    .build();
            dtoList.add(dto);
            System.out.println(dto);
        }
        model.addAttribute("categories", dtoList);
        return "/main/home";
    }
    @GetMapping("/restaurant/{category}/{start}")
    public String getCategoryRestaurant(@PathVariable int category, @PathVariable int start, Model model){
        List<RestaurantResponseDto> responseDtoList = restaurantService.findAllInCategory(category,start);
        model.addAttribute("rList", responseDtoList);
        return "restList";
    }
    @GetMapping("/restaurant/search/{word}/{start}")
    public String searchRestaurant(@PathVariable String word, @PathVariable int start, Model model){
        System.out.println(word+"-search");
        List<RestaurantResponseDto> responseDtoList = restaurantService.searchName(word, start);
        model.addAttribute("rList",responseDtoList);
        System.out.println("data response");
        return "redirect:restList";
    }

}
