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
        int i = 0;
        /* Category */
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

        /* Franchise */
        Long j=0L;
        franchises.add(Franchise.builder().id(++j).name("VIPS").category(categories.get(11)).build());

        /* User */
        j=0L;
        users.add(User.builder()
                .id(++j)
                .birthDate(LocalDate.of(1999, 2, 28))
                .name("ChoiShy")
                .score(50)
                .build());

        /* Login */
        logins.add(Login.builder()
                .id("tjdgus4697")
                .pw("1234")
                .user(users.get(0)).build());


        /* Restaurant */
        j=0L;
        restaurants.add(Restaurant.builder()
                .id(++j)
                .name("도남김밥")
                .addr("도남중앙로")
                .score(0)
                .category(categories.get(16))
                .build());

        /* Menu */
        j=0L;
        menus.add(Menu.builder()
                .id(++j)
                .name("빕스 1997 스테이크 2인세트")
                .price(52000)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .id(++j)
                .name("스테이크 싱글플래터 1인 세트")
                .price(23000)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .id(++j)
                .name("얌새우 알리오올리오 파스타")
                .price(17900)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .id(++j)
                .name("쉬림프 로제 파스타")
                .price(18900)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .id(++j)
                .name("시그니처 훈제 연어 샐러드")
                .price(15500)
                .score(0)
                .category(categories.get(10))
                .franchise(franchises.get(0))
                .build());
        menus.add(Menu.builder()
                .id(++j)
                .name("김밥")
                .price(3000)
                .score(0)
                .restaurant(restaurants.get(0))
                .build());
        menus.add(Menu.builder()
                .id(++j)
                .name("라면")
                .price(5000)
                .score(40)
                .restaurant(restaurants.get(0))
                .build());
    }
}
