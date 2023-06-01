package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;

public interface ReviewService {
    ReviewResponseDto saveReview(ReviewDto reviewDto);      // 리뷰 작성
    ReviewResponseDto getReview(Long id);                   // 리뷰 조회
    ReviewResponseDto getReviewsForRestaurant(Long id);                   // 리뷰 조회(해당 레스토랑의 모든 리뷰 조회)
    ReviewResponseDto getReviewsForUser(Long id);                   // 리뷰 조회(해당 사용자가 작성한 모든 리뷰 조회)
    ReviewResponseDto updateReview(Long id, ReviewDto reviewDto); // 리뷰 수정
    void deleteReview(Long id);                             // 리뷰 삭제
}
