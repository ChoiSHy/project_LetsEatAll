package com.letseatall.letseatall.controller.page;

import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/page/menu")
@RequiredArgsConstructor
public class menuPageController {
    private final MenuService menuService;
    private final ReviewService reviewService;

    @GetMapping()
    public String getMenu(@RequestParam long id, Model model) throws IOException {
        MenuResponseDto menu = menuService.getMenu(id);
        List<ReviewResponseDto> revList = reviewService.getAllReviewsInMenu(id);
        model.addAttribute("menu", menu);
        model.addAttribute("reviews", revList);
        return "menu/menuInfo";
    }
}
