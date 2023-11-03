package com.letseatall.letseatall.service.Data;

import com.letseatall.letseatall.data.Entity.*;
import com.letseatall.letseatall.data.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OneToManyTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    FranchiseRepository franchiseRepository;
    @BeforeAll
    private void setData(){
        List<Category> categories = new ArrayList<>();
        List<Menu> menus = new ArrayList<>();
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
        menus.add(m1);

        Menu m2 = new Menu("스테이크 싱글플래터 1인 세트",23000,2,categories.get(10));
        m2.setFranchise(vips);
        menus.add(m2);

        Menu m3 = new Menu("얌새우 알리오올리오 파스타",17900,6,categories.get(10));
        m3.setFranchise(vips);
        menus.add(m3);

        Menu m4 = new Menu("쉬림프 로제 파스타", 18900, 0,categories.get(10));
        m4.setFranchise(vips);
        menus.add(m4);

        Menu m5 = new Menu("시그니처 훈제 연어 샐러드", 15500,7,categories.get(10));
        m5.setFranchise(vips);
        menus.add(m5);

        System.out.println(vips);
        franchiseRepository.save(vips);
    }
    @Test
    @Transactional
    public void saveReviewTest() {
        /* user */
        //User writer = userRepository.getByUid("tjdgus4697");
        //System.out.println("writer: "+writer.toString());
        User writer = new User();
        writer.setName("최성현");
        writer.setBirthDate(LocalDate.of(1999, 02, 28));
        writer.setUid("tjdgus4697");
        writer.setPassword("1234");
        writer.setScore(50);

        /* restaurant */
        Restaurant r1 = new Restaurant();
        r1.setName("봉's 파스타");
        r1.setScore(0);
        r1.setAddr("대구 중앙로 어딘가");
        r1.setCategory(categoryRepository.getById(11));
        Restaurant r2 = new Restaurant();
        r2.setName("도남김밥");
        r2.setScore(7);
        r2.setAddr("도남동");
        r2.setCategory(categoryRepository.getById(17));

        /* menu */
        Menu m1 = new Menu();
        m1.setName("크림 파스타");
        m1.setPrice(12000);
        m1.setScore(5);
        Menu m2 = new Menu();
        m2.setName("토마토 파스타");
        m2.setPrice(11000);
        m2.setScore(7);

        /* review */
        Review newReview1 = new Review();
        newReview1.setTitle("파스타 후기");
        newReview1.setContent("존맛임. 꼭 먹으셈!");
        newReview1.setScore(10);
        newReview1.setRecCnt(0);

        Review newReview2 = new Review();
        newReview2.setTitle("파스타 후기2");
        newReview2.setContent("노맛임.");
        newReview2.setScore(1);
        newReview2.setRecCnt(0);

        /* relationship */
        m1.setRestaurant(r1);
        m2.setRestaurant(r1);

        newReview1.setMenu(m1);
        newReview2.setMenu(m1);

        newReview1.setWriter(writer);
        newReview2.setWriter(writer);

        /* save */
        restaurantRepository.save(r1);
        restaurantRepository.save(r2);
        userRepository.save(writer);

        /* result */
        List<Review> reviews = reviewRepository.findAll();
        List<Menu> menus = menuRepository.findAll();
        List<User> users = userRepository.findAll();
        List<Restaurant> rests = restaurantRepository.findAll();

        System.out.println("[review]");
        reviews.forEach(r -> System.out.println(r));
        System.out.println("[user]");
        users.forEach(u-> System.out.println(u));
        System.out.println("[restaurant]");
        rests.forEach(r-> System.out.println(r));
        System.out.println("[menu]");
        menus.forEach( m -> System.out.println(m) );

    }
    @Test
    public void BeforeEachTest(){
        Category category = categoryRepository.findById(1).get();
        Franchise franchise = franchiseRepository.findById(1L).get();
        menuRepository.findAll().forEach(m-> System.out.println(m));
        System.out.println(category);
        System.out.println(franchise);
    }
}
