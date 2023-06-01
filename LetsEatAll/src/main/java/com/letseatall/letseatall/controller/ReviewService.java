package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    // 리뷰 조회
    @GetMapping("/reviewId")
    public ResponseEntity<ReviewResponseDto> getReview(Long reviewId){
        ReviewResponseDto responseDto = reviewService.getReview(reviewId);
        if(responseDto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    
    // 리뷰 조회(해당 레스토랑의 모든 리뷰 조회)
    @GetMapping("/{restaurantId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForRestaurant(@PathVariable Long restaurantId) {
        List<ReviewResponseDto> reviews = (List<ReviewResponseDto>) reviewService.getReviewsForRestaurant(restaurantId);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }

    // 리뷰 조회(해당 사용자가 작성한 모든 리뷰 조회)
    @GetMapping("/{userId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForUser(@PathVariable Long userId) {
        List<ReviewResponseDto> reviews = (List<ReviewResponseDto>) reviewService.getReviewsForUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }

    // 리뷰 등록
    @PostMapping()
    public ResponseEntity<ReviewResponseDto> saveReview(@RequestBody ReviewDto reviewDto) {
        ReviewResponseDto createdReview = reviewService.saveReview(reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long reviewId, @RequestBody ReviewDto reviewDto) {
        ReviewResponseDto updatedReview = reviewService.updateReview(reviewId, reviewDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedReview);
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
