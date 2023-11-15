package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    ReviewResponseDto saveReview(ReviewDto reviewDto, MultipartFile file) throws IOException;
    ReviewResponseDto getReview(Long id);
    ReviewResponseDto modifyReview(ReviewModifyDto rmd, MultipartFile file) throws IOException;
    Long deleteReview(Long id);
    List<ReviewResponseDto> getAllReviewsInMenu(Long mid);
    List<ReviewResponseDto> getAllReviewsInRestaurant(Long rid);
    List<ReviewResponseDto> getAllReviews();
    List<ReviewResponseDto> getAllReviewsWrittenByUser(Long uid);
    List<ReviewResponseDto> findAllReviewsWrittenByYou();
    ReviewResponseDto likeReview(long id, int score);
}
