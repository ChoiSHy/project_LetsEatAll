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
        LOGGER.info("[plusScore] menu = {}", menu);
        Restaurant restaurant = menu.getRestaurant();
        LOGGER.info("[plusScore] restaurant = {}", restaurant);
        long rest_id= restaurant.getId();
        int review_cnt1 = reviewRepository.countAllByMenuId(menu.getId());
        int review_cnt2 = reviewRepository.countReviewsByRestaurantId(rest_id);
        LOGGER.info("[plusScore] review_cnt = {}, menu_cnt = {}", review_cnt1, review_cnt2);

        double menu_score= menu.getScore() * (review_cnt1-1) + review.getScore();
        menu_score = menu_score/(review_cnt1 == 0 ? 1 : review_cnt1);
        double restaurant_score = restaurant.getScore() * (review_cnt2-1) + menu_score;
        restaurant_score = restaurant_score / (review_cnt2 == 0 ? 1: review_cnt2);
        menu.setScore(menu_score);
        restaurant.setScore(restaurant_score);
        LOGGER.info("[plusScore] 점수 수정 대입");

        menuRepository.save(menu);
        LOGGER.info("[plusScore] 메뉴 점수 조정 완료");
        
        restaurantRepository.save(restaurant);

        LOGGER.info("[plusScore] 식당 점수 조정 완료");
        LOGGER.info("[plusScore] 점수 조정 완료");

        return review;
    }
}
