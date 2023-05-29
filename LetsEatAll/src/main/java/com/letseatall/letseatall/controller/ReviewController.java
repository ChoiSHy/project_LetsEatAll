package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping()
    public ResponseEntity<ReviewResponseDto> getReview(Long id) {
        ReviewResponseDto responseDto = reviewService.getReview(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity<ReviewResponseDto> saveReview(@RequestBody ReviewDto reviewDto) {
        ReviewResponseDto responseDto = reviewService.saveReview(reviewDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/modify")
    public ResponseEntity<ReviewResponseDto> modifyReview(@RequestBody ReviewModifyDto rmd) {
        ReviewResponseDto responseDto = reviewService.modifyReview(rmd);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReview(Long id) {
        Long del_id = reviewService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.OK).body(del_id + "게시글 삭제되었습니다.");
    }
}
