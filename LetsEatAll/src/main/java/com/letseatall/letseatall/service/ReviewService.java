package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    ReviewResponseDto saveReview(ReviewDto reviewDto, List<MultipartFile> files) throws IOException;
    ReviewResponseDto getReview(Long id);
    ReviewResponseDto modifyReview(ReviewModifyDto rmd, List<MultipartFile> files) throws IOException;
    Long deleteReview(Long id);
    List<ReviewResponseDto> getAllReviewsInMenu(Long mid);
    List<ReviewResponseDto> getAllReviewsInRestaurant(Long rid);
    List<ReviewResponseDto> getAllReviews();
    List<ReviewResponseDto> getAllReviewsInFranchise(Long fid);
    List<ReviewResponseDto> getAllReviewsWrittenByUser(Long uid);
    List<ReviewResponseDto> findAllReviewsWrittenByYou();
    ResponseEntity downloadImg(Long id) throws IOException;

    ReviewResponseDto likeReview(long id, int score);
    void uploadReviewImage(long review_id, MultipartFile file);
}
