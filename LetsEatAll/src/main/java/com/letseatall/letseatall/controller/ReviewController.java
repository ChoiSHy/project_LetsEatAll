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
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable("id") Long id) {
        ReviewResponseDto responseDto = reviewService.getReview(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/create")
    public ResponseEntity saveReview(@RequestPart ReviewDto reviewDto,
                                     @RequestPart List<MultipartFile> files) throws IOException {
        ReviewResponseDto responseDto = reviewService.saveReview(reviewDto, files);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/modify")
    public ResponseEntity<ReviewResponseDto> modifyReview(@RequestPart ReviewModifyDto rmd,
                                                          @RequestPart List<MultipartFile> files) throws IOException {
        try{
        ReviewResponseDto responseDto = reviewService.modifyReview(rmd, files);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);}
        catch (ResponseStatusException e){
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long id) {
        try {
            Long del_id = reviewService.deleteReview(id);
            return ResponseEntity.status(HttpStatus.OK).body(del_id + "게시글 삭제되었습니다.");
        }
        catch(ResponseStatusException e){
            throw e;
        }
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

    @GetMapping("/all")
    public ResponseEntity getAllReviews() {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviews();
        return ResponseEntity.ok().body(responseDtoList);
    }

    @PostMapping("/image")
    public ResponseEntity uploadReviewImg(
            @RequestParam Long rid,
            @RequestParam MultipartFile file
    ) throws IOException {
        System.out.println("itemName = " + rid);

        if (!file.isEmpty()) {
            String fullPath = imgPath + file.getOriginalFilename();
            LOGGER.info("[uploadReviewImg] 저장할 이미지 위치 : {}", fullPath);
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

    @GetMapping("/reviewList/user/me")
    public ResponseEntity findAllOfMyReviews() {
        List<ReviewResponseDto> responseDtoList = reviewService.findAllReviewsWrittenByYou();
        return ResponseEntity.ok().body(responseDtoList);
    }

    @GetMapping("reviewList/user/{user_id}")
    public ResponseEntity findAllReviewsOfUser(@PathVariable("user_id") long user_id) {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviewsWrittenByUser(user_id);
        return ResponseEntity.ok().body(responseDtoList);
    }

}
