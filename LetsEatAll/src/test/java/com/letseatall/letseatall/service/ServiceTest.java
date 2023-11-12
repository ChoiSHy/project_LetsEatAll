package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.Entity.Franchise;
import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.repository.CategoryRepository;
import com.letseatall.letseatall.data.repository.FranchiseRepository;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceTest {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    FranchiseRepository franchiseRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RestaurantService restaurantService;
    @Autowired
    MenuService menuService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    UserService userService;

    @BeforeAll
    public void setup(){
        List<Category> categories = new ArrayList<>();
        /* Category */
        int i=0;
        categories.add(Category.builder().id(++i).name("족발").build());  //1
        categories.add(Category.builder().id(++i).name("보쌈").build());  //2
        categories.add(Category.builder().id(++i).name("찜").build());   //3
        categories.add(Category.builder().id(++i).name("탕").build());   //4
        categories.add(Category.builder().id(++i).name("찌개").build());  //5
        categories.add(Category.builder().id(++i).name("돈까스").build()); //6
        categories.add(Category.builder().id(++i).name("회").build());   //7
        categories.add(Category.builder().id(++i).name("일식").build());  //8
        categories.add(Category.builder().id(++i).name("피자").build());  //9
        categories.add(Category.builder().id(++i).name("구이").build());  //10
        categories.add(Category.builder().id(++i).name("양식").build());  //11
        categories.add(Category.builder().id(++i).name("치킨").build());  //12
        categories.add(Category.builder().id(++i).name("중식").build());  //13
        categories.add(Category.builder().id(++i).name("아시안").build()); //14
        categories.add(Category.builder().id(++i).name("백반").build());  //15
        categories.add(Category.builder().id(++i).name("국수").build());  //16
        categories.add(Category.builder().id(++i).name("분식").build());  //17
        categories.add(Category.builder().id(++i).name("카페/디저트").build());  //18
        categories.add(Category.builder().id(++i).name("햄버거").build()); //19

        categoryRepository.saveAll(categories);
        /* franchise */
        Franchise vips = new Franchise("VIPS", categories.get(10));

        /* menu */
        Menu m1 = new Menu("빕스 1997 스테이크 2인세트",52000,10,categories.get(10));
        m1.setFranchise(vips);
        Menu m2 = new Menu("스테이크 싱글플래터 1인 세트",23000,2,categories.get(10));
        m2.setFranchise(vips);
        Menu m3 = new Menu("얌새우 알리오올리오 파스타",17900,6,categories.get(10));
        m3.setFranchise(vips);
        Menu m4 = new Menu("쉬림프 로제 파스타", 18900, 0,categories.get(10));
        m4.setFranchise(vips);
        Menu m5 = new Menu("시그니처 훈제 연어 샐러드", 15500,7,categories.get(10));
        m5.setFranchise(vips);

        franchiseRepository.save(vips);

        User user = new User();
        user.setName("최성현");
        user.setBirthDate(LocalDate.of(1999,2,28));
        user.setScore(50);
        userRepository.save(user);
    }

    @Test
    @Transactional
    @DisplayName("프랜차이즈 음식점 저장")
    public void saveNewFranchiseChain(){

        /* VIPS 프랜차이즈를 가진 음식점 신규 저장 */
        RestaurantDto requestDto = RestaurantDto.builder()
                .name("VIPS-동성로점")
                .addr("대구 동성로")
                .category(11)
                .fid(1L)
                .build();
        RestaurantResponseDto responseDto = restaurantService.saveRestaurant(requestDto);
        /* 저장된 음식점 정보 출력 */
        System.out.println(responseDto);

        /* 현재 저장된 메뉴들 출력 */
        for (Menu menu : menuRepository.findAll()) {
            System.out.println("[Menu - "+menu.getId()+"]");
            System.out.println("\t"+menu.getName());
            System.out.println("\t"+menu.getPrice());
            System.out.println("\t"+menu.getScore());
            System.out.println("\t"+menu.getRestaurant());
            System.out.println("\t"+menu.getFranchise());
        }
        reviewService.saveReview(7L,"제목","내용입니다\n\n알겠죠?>?",
                        7,null);

        System.out.println(menuRepository.findById(7L).get().getReviewList());
        restaurantService.deleteFranchise(1L);
    }
}
