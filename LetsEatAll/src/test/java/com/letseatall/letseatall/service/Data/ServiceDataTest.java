package com.letseatall.letseatall.service.Data;

import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.RestaurantService;
import com.letseatall.letseatall.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ServiceDataTest {
    /* Service */
    @Autowired
    RestaurantService restaurantService;
    @Autowired
    MenuService menuService;
    @Autowired
    ReviewService reviewService;

    /* Repository */
    @Autowired CategoryRepository categoryRepository;
    @Autowired FranchiseRepository franchiseRepository;
    @Autowired RestaurantRepository restaurantRepository;
    @Autowired MenuRepository menuRepository;
    @Autowired ReviewRepository reviewRepository;
    @Autowired UserRepository userRepository;
    @Autowired LoginRepository loginRepository;

    @Test
    @DisplayName("get test")
    public void getTest(){
        DataFactory dataFactory = new DataFactory();
        categoryRepository.saveAll(dataFactory.getCategories());
        franchiseRepository.saveAll(dataFactory.getFranchises());
        restaurantRepository.saveAll(dataFactory.getRestaurants());
        menuRepository.saveAll(dataFactory.getMenus());
        userRepository.saveAll(dataFactory.getUsers());
        loginRepository.saveAll(dataFactory.getLogins());

        RestaurantResponseDto responseDto = restaurantService.getRestaurant(1L);
        System.out.println(responseDto);
    }

}
