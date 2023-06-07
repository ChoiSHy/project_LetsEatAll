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
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

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
        Menu menu = null;
        User user = null;
        Optional<Menu> oMenu = menuRepository.findById(reviewDto.getMid());
        Optional<User> oUser = userRepository.findById(reviewDto.getUid());
        if (oMenu.isPresent())
            menu=oMenu.get();
        if (oUser.isPresent())
            user=oUser. get();

        Review newReview = new Review();
        newReview.setTitle(reviewDto.getTitle());
        newReview.setContent(reviewDto.getContent());
        newReview.setScore(reviewDto.getScore());
        newReview.setRecCnt(0);
        newReview.setMenu(menu);
        newReview.setWriter(user);
        newReview.setPid(reviewDto.getImg());

        Review savedReview = reviewRepository.save(newReview);
        return getReviewResponseDto(savedReview);
    }

    // 리뷰 조회
    @Override
    public ReviewResponseDto getReview(Long id) {
        Optional<Review> oReview = reviewRepository.findById(id);
        if (oReview.isPresent()) {
            Review review = oReview.get();

            return getReviewResponseDto(review);
        }
        return null;
    }
    // 리뷰 조회(해당 메뉴의 모든 리뷰 조회)
    @Override
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

    @Override
    public List<ReviewResponseDto> getReviewsForUser(Long id) {
        List<Review> reviews = reviewRepository.findAllByWriterId(id);

        List<ReviewResponseDto> responseDtos = new ArrayList<>();
        /* 병합 과정에서 포함 못함. 추후에 작성*/

        // 변환된 ReviewResponseDto 리스트를 반환합니다.
        return responseDtos;
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