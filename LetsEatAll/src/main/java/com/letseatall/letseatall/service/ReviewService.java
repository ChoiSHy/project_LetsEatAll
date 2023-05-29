package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;

public interface ReviewService {
    ReviewResponseDto saveReview(ReviewDto reviewDto);
    ReviewResponseDto getReview(Long id);
    ReviewResponseDto modifyReview(ReviewModifyDto rmd);
    Long deleteReview(Long id);


}
