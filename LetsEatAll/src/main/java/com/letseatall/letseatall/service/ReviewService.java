package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.Entity.Review;
import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface ReviewService {
    //ReviewResponseDto saveReview(Long mid,String title,String content,int score,MultipartFile file) throws IOException;
    ReviewResponseDto saveReview(ReviewDto reviewDto, List<MultipartFile> files) throws IOException;
    ReviewResponseDto getReview(Long id);
    ReviewResponseDto modifyReview(ReviewModifyDto rmd, List<MultipartFile> files) throws IOException;
    Long deleteReview(Long id);
    List<ReviewResponseDto> getAllReviewsInMenu(Long mid);
    List<ReviewResponseDto> getAllReviewsInRestaurant(Long rid);
    List<ReviewResponseDto> getAllReviews();
    List<ReviewResponseDto> getAllReviewsInFranchise(Long fid);
    List<ReviewResponseDto> getReviewsForUser(Long uid);
    ResponseEntity downloadImg(Long id) throws IOException;
}
