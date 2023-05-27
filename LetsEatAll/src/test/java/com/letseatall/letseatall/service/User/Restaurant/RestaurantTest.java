package com.letseatall.letseatall.service.User.Restaurant;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Restaurant;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.RestaurantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class RestaurantTest {
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    MenuRepository menuRepository;

    @Test
    @DisplayName("식당 등록 및 메뉴 등록")
    public void test1() {
        Restaurant restaurant1 = Restaurant.builder()
                .name("백식당")
                .score(0)
                .addr("대구광역시 북구 구리로")
                .category(11)
                .fid(0L)
                .build();
        Restaurant savedRest = restaurantRepository.save(restaurant1);
        System.out.println("[Restaurant]: "+savedRest);

        Menu menu1 = Menu.builder()
                .name("떡볶이")
                .price(3000)
                .category(11)
                .restaurant(restaurant1)
                .build();
        Menu menu2 = Menu.builder()
                .name("순대")
                .price(5000)
                .category(11)
                .restaurant(restaurant1)
                .build();
        Menu menu3 = Menu.builder()
                .name("오뎅")
                .price(500)
                .category(11)
                .restaurant(restaurant1)
                .build();

        Menu savedMenu1 = menuRepository.save(menu1);
        Menu savedMenu2 = menuRepository.save(menu2);
        Menu savedMenu3 = menuRepository.save(menu3);

        System.out.println("<< save complete >>");
        System.out.println(savedMenu1);
        System.out.println(savedMenu2);
        System.out.println(savedMenu3);

        Restaurant foundRest = restaurantRepository.findById(restaurant1.getId()).get();
        System.out.println("[from db]");
        for(Menu menu : foundRest.getMenus())
            System.out.println(menu);
    }
}
