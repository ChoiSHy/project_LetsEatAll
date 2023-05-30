package com.letseatall.letseatall.service.Data;

import com.letseatall.letseatall.data.Entity.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Getter
public class DataFactory {
    List<Category> categories;
    List<Franchise> franchises;
    List<Restaurant> restaurants;
    List<Menu> menus;
    List<Review> reviews;
    List<User> users;
    List<Login> logins;

    public DataFactory() {
        categories = new ArrayList<>();
        franchises = new ArrayList<>();
        restaurants = new ArrayList<>();
        menus = new ArrayList<>();
        reviews = new ArrayList<>();
        users = new ArrayList<>();
        logins = new ArrayList<>();

        /* Category */
        categories.add(Category.builder().name("족발").build());
        categories.add(Category.builder().name("보쌈").build());
        categories.add(Category.builder().name("찜").build());
        categories.add(Category.builder().name("탕").build());
        categories.add(Category.builder().name("찌개").build());
        categories.add(Category.builder().name("돈까스").build());
        categories.add(Category.builder().name("회").build());
        categories.add(Category.builder().name("일식").build());
        categories.add(Category.builder().name("피자").build());
        categories.add(Category.builder().name("구이").build());
        categories.add(Category.builder().name("양식").build());
        categories.add(Category.builder().name("치킨").build());
        categories.add(Category.builder().name("중식").build());
        categories.add(Category.builder().name("아시안").build());
        categories.add(Category.builder().name("백반").build());
        categories.add(Category.builder().name("국수").build());
        categories.add(Category.builder().name("분식").build());
        categories.add(Category.builder().name("카페/디저트").build());
        categories.add(Category.builder().name("햄버거").build());

        /* Franchise */
        franchises.add(Franchise.builder().name("VIPS").category(categories.get(11)).build());

        /* User */
        users.add(User.builder()
                .birthDate(LocalDate.of(1999, 2, 28))
                .name("ChoiShy")
                .score(50)
                .build());

        /* Login */
        logins.add(Login.builder()
                .id("tjdgus4697")
                .pw("1234")
                .user(users.get(0)).build());

        /* Menus */
        menus.add(Menu.builder()
                .name("빕스 1997 스테이크 2인세트")
                .price(52000)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .name("스테이크 싱글플래터 1인 세트")
                .price(23000)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .name("얌새우 알리오올리오 파스타")
                .price(17900)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .name("쉬림프 로제 파스타")
                .price(18900)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .name("시그니처 훈제 연어 샐러드")
                .price(15500)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        restaurants.add(Restaurant.builder()
                .name("도남김밥")
                .addr("도남중앙로")
                .score(0)
                .category(categories.get(16))
                .build());

        /* Menu */
        menus.add(Menu.builder()
                .name("김밥")
                .price(3000)
                .score(0)
                .restaurant(restaurants.get(0))
                .build());
        menus.add(Menu.builder()
                .name("라면")
                .price(5000)
                .score(40)
                .restaurant(restaurants.get(0))
                .build());
    }
}
