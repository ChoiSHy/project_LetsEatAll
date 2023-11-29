package com.letseatall.letseatall.controller.page;

import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
@RequestMapping("page/restaurant")
@RequiredArgsConstructor
public class RestaurantPageController {
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    @GetMapping("/{category}/{start}")
    public String getCategoryRestaurant(@PathVariable int category, @PathVariable int start, Model model){
        List<RestaurantResponseDto> responseDtoList = restaurantService.findAllInCategory(category,start);
        model.addAttribute("rList", responseDtoList);
        return "restaurant/restList";
    }
    @GetMapping()
    public String getRestaurant(@RequestParam long id, Model model){
        RestaurantResponseDto rrd = restaurantService.getRestaurant(id);
        model.addAttribute("restaurant", rrd);
        return "restaurant/restaurant";
    }

    @GetMapping("/search/{word}/{start}")
    public String searchRestaurant(@CookieValue(name = "token", required = false) String token, @PathVariable String word, @PathVariable int start, Model model) throws UnsupportedEncodingException {
        System.out.println(word+"-search");
        List<RestaurantResponseDto> responseDtoList = restaurantService.searchName(word, start);
        model.addAttribute("rList",responseDtoList);
        model.addAttribute("token", token);
        System.out.println("data response");
        return "restaurant/restList";
    }
}
