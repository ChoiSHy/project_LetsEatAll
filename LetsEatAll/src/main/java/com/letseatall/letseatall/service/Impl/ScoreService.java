package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Restaurant;
import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.repository.Menu.MenuRepository;
import com.letseatall.letseatall.data.repository.RestaurantRepository;
import com.letseatall.letseatall.data.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(ScoreService.class);

    public Review plusScore(Review review){
        LOGGER.info("[plusScore] 점수 조정 시작");
        Menu menu = review.getMenu();
        Restaurant restaurant = menu.getRestaurant();

        int review_cnt = reviewRepository.countAllByMenuId(menu.getId());
        int menu_cnt = menuRepository.countAllByRestaurantId(restaurant.getId());
        LOGGER.info("[plusScore] review_cnt = {}, menu_cnt = {}", review_cnt, menu_cnt);

        double menu_score= menu.getScore() * review_cnt + review.getScore();
        double restaurant_score = restaurant.getScore() * menu_cnt - menu.getScore() + menu_score;

        menu.setScore(menu_score/(review_cnt == 0 ? 1 : review_cnt));
        restaurant.setScore(restaurant_score / (menu_cnt == 0 ? 1: menu_cnt));
        LOGGER.info("[plusScore] 점수 수정 대입");

        menuRepository.save(menu);
        LOGGER.info("[plusScore] 메뉴 점수 조정 완료");
        
        restaurantRepository.save(restaurant);

        LOGGER.info("[plusScore] 식당 점수 조정 완료");
        LOGGER.info("[plusScore] 점수 조정 완료");

        return review;
    }
}
