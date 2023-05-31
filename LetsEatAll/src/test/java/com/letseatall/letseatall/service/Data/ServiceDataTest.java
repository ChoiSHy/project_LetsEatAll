package com.letseatall.letseatall.service.Data;

import com.letseatall.letseatall.data.Entity.Restaurant;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.RestaurantService;
import com.letseatall.letseatall.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
    //@Autowired CategoryRepository categoryRepository;
    @Autowired CategoryBulkRepository categoryRepository;
    @Autowired FranchiseRepository franchiseRepository;
    @Autowired RestaurantRepository restaurantRepository;
    @Autowired MenuRepository menuRepository;
    @Autowired MenuBulkRepository menuRepository2;
    @Autowired ReviewRepository reviewRepository;
    @Autowired UserRepository userRepository;
    @Autowired LoginRepository loginRepository;

    private void setData(){
        DataFactory dataFactory = new DataFactory();
        categoryRepository.saveAll(dataFactory.getCategories());
        franchiseRepository.saveAll(dataFactory.getFranchises());
        restaurantRepository.saveAll(dataFactory.getRestaurants());
        menuRepository2.saveAll(dataFactory.getMenus());
        userRepository.saveAll(dataFactory.getUsers());
        loginRepository.saveAll(dataFactory.getLogins());
    }
   /*@Test
    @DisplayName("get test")
    public void getTest(){
        setData();

        RestaurantResponseDto responseDto = restaurantService.getRestaurant(1L);
        System.out.println(responseDto);
    }*/
    @Test
    @DisplayName("remove test")
    public void removeTest(){
        /* Setting for test */
        setData();

        RestaurantDto restaurantDto = RestaurantDto.builder()
                .addr("계양로")
                .category(11)
                .fid(1L)
                .name("VIPS-경산점")
                .build();
        RestaurantResponseDto savedRestaurant = restaurantService.saveRestaurant(restaurantDto);
        //System.out.println("[저장된 음식점]");
        //System.out.println(savedRestaurant);

        /*
        List<MenuResponseDto> mlist = menuService.getAllMenu(savedRestaurant.getId());
        for(MenuResponseDto m : mlist)
            System.out.println(m);
        */

        ReviewDto newReview = ReviewDto.builder()
                .mid(8L)
                .uid(1L)
                .title("스테이크 후기")
                .content("맛있어요!!")
                .score(10)
                .build();
        ReviewResponseDto savedReview =reviewService.saveReview(newReview);
        newReview = ReviewDto.builder()
                .mid(8L)
                .uid(1L)
                .title("스테이크 2인")
                .content("푸짐하다!!!")
                .score(9)
                .build();
        reviewService.saveReview(newReview);
        newReview = ReviewDto.builder()
                .mid(11L)
                .uid(1L)
                .title("파스타!")
                .content("좀 별루?")
                .score(5)
                .build();
        reviewService.saveReview(newReview);
        newReview = ReviewDto.builder()
                .mid(6L)
                .uid(1L)
                .title("김밥")
                .content("여긴 오지마...")
                .score(1)
                .build();
        reviewService.saveReview(newReview);

        //reviewService.getAllReviewsInMenu(6L).forEach(obj -> System.out.println(obj));
        restaurantRepository.findAll().forEach(r-> System.out.println(r));
        System.out.println();
        menuRepository.findAll().forEach(m -> System.out.println(m));
        /* 삭제 */
        System.out.println("---------------------------------");
        restaurantService.deleteRestaurant(2L);
        restaurantRepository.findAll().forEach(r-> System.out.println(r));
        System.out.println();
        menuRepository.findAll().forEach(m-> System.out.println(m));

    }

}
