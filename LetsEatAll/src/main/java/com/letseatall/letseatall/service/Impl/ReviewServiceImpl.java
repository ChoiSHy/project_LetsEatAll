package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.ReviewRepository;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    ReviewRepository reviewRepository;
    MenuRepository menuRepository;
    UserRepository userRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             MenuRepository menuRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponseDto saveReview(ReviewDto reviewDto) {
        Optional<Menu> oMenu = menuRepository.findById(reviewDto.getMid());
        Optional<User> oUser = userRepository.findById(reviewDto.getUid());

        if (!oMenu.isPresent())
            return null;
        if (!oUser.isPresent())
            return null;
        Menu menu = oMenu.get();
        User user = oUser.get();

        Review newReview = Review.builder()
                .title(reviewDto.getTitle())
                .content(reviewDto.getContent())
                .score(reviewDto.getScore())
                .recCnt(0)
                .pid(reviewDto.getImg())
                .menu(menu)
                .writer(user)
                .build();
        Review savedReview = reviewRepository.save(newReview);
        return getReviewResponseDto(savedReview);

    }

    @Override
    public ReviewResponseDto getReview(Long id) {
        Optional<Review> oReview = reviewRepository.findById(id);
        if (oReview.isPresent()) {
            Review review = oReview.get();

            return getReviewResponseDto(review);
        }
        return null;
    }

    @Override
    @Transactional
    public ReviewResponseDto modifyReview(ReviewModifyDto rmd) {
        Optional<Review> oReview = reviewRepository.findById(rmd.getId());
        if (oReview.isPresent()) {
            Review review = oReview.get();

            review.setTitle(rmd.getTitle());
            review.setContent(rmd.getContent());
            review.setScore(rmd.getScore());
            review.setPid(rmd.getImg());
            review.getMenu();
            review.getWriter();

            Review modifiedReview = reviewRepository.save(review);

            return getReviewResponseDto(modifiedReview);
        }

        return null;
    }

    @Override
    public Long deleteReview(Long id) {
        reviewRepository.deleteById(id);
        return id;
    }

    private ReviewResponseDto getReviewResponseDto(Review review) {
        ReviewResponseDto rrd = ReviewResponseDto.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .img(review.getPid())
                .score(review.getScore())
                .count(review.getRecCnt())
                .build();
        Menu menu = review.getMenu();
        User writer = review.getWriter();
        if (menu != null) {
            rrd.setMenu(menu.getName());
            rrd.setMid(menu.getId());
        }
        if (writer != null) {
            rrd.setWriter(writer.getName());
            rrd.setUid(writer.getId());
        }
        return rrd;
    }

    @Transactional
    public List<ReviewResponseDto> getAllReviewsInMenu(Long mid) {
        List<Review> reviewList = reviewRepository.findAllByMenu(mid);
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        for (Review ent : reviewList) {
            ReviewResponseDto rrd = getReviewResponseDto(ent);
            responseDtoList.add(rrd);
        }
        return responseDtoList;
    }

    @Transactional
    public List<ReviewResponseDto> getAllReviewsInRestaurant(Long rid){
        List<Review> reviewList = reviewRepository.findAllByRestaurant(rid);
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        for (Review ent : reviewList){
            ReviewResponseDto rrd = getReviewResponseDto(ent);
            responseDtoList.add(rrd);
        }
        return responseDtoList;
    }
    @Transactional
    public List<ReviewResponseDto> getAllReviewsInFranchise(Long fid){
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        reviewRepository.findAllByFranchise(fid)
                .forEach(rev -> responseDtoList.add(
                        getReviewResponseDto(rev))
                );
        return responseDtoList;
    }
}
