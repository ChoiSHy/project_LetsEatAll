package com.letseatall.letseatall.controller.page;

import com.letseatall.letseatall.config.security.JwtAuthenticationFilter;
import com.letseatall.letseatall.config.security.JwtTokenProvider;
import com.letseatall.letseatall.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/page/recommend")
public class RecommendPageController {

    private final MenuService menuService;

    @GetMapping("/")
    public String getRecommend(@CookieValue(name = "token") String token,
                               Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);

        return "/";
    }
}
