package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.*;

@RestController
@RequestMapping("/review")
public class ReviewController {
    @Value("${spring.img.path}")
    private String imgPath;
    private final ReviewService reviewService;

    private final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

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
    public ResponseEntity<ReviewResponseDto> saveReview(
            @RequestParam Long mid,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam int score,
            @RequestParam MultipartFile file) throws IOException{
        ReviewResponseDto responseDto = reviewService.saveReview(mid, title, content, score, file);
        return ResponseEntity.ok(responseDto);
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

    @GetMapping("/menu/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsAboutMenu(Long mid) {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviewsInMenu(mid);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @GetMapping("/restaurant/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsAboutRestaurant(Long rid) {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviewsInRestaurant(rid);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @PostMapping("/image")
    public ResponseEntity uploadReviewImg(
            @RequestParam Long rid,
            @RequestParam MultipartFile file
    ) throws IOException {
        System.out.println("itemName = " + rid);

        if (!file.isEmpty()) {
            String fullPath = imgPath + file.getOriginalFilename();
            LOGGER.info("[uploadReviewImg] 저장할 이미지 위치 : {}",fullPath);
            file.transferTo(new File(fullPath));
            LOGGER.info("[uploadReviewImg] 이미지 저장 완료");
        }
        return ResponseEntity.ok("저장");
    }

    @GetMapping("/image/{reviewId}/attach")
    public ResponseEntity downloadReviewImg(
            @PathVariable("reviewId") Long id) throws IOException {
        return reviewService.downloadImg(id);
    }

}
