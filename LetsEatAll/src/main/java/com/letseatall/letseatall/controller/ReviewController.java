package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.service.ReviewService;
import com.letseatall.letseatall.service.awsS3.S3UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    public ReviewController(ReviewService reviewService){
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
        try {
            ReviewResponseDto responseDto = reviewService.modifyReview(rmd, files);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (ResponseStatusException e) {
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long id) {
        try {
            Long del_id = reviewService.deleteReview(id);
            return ResponseEntity.status(HttpStatus.OK).body(del_id + "게시글 삭제되었습니다.");
        } catch (ResponseStatusException e) {
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

    @PostMapping(value = "/image", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity uploadReviewImg(
            @RequestParam Long rid,
            @RequestParam MultipartFile file) throws IOException {
        LOGGER.info("[uploadReviewImg] itemName = " + rid);

        if (!file.isEmpty()) {
            reviewService.uploadReviewImage(rid, file);
            LOGGER.info("[uploadReviewImg] 이미지 저장 완료");
        }
        return ResponseEntity.ok("저장");
    }
    @GetMapping("/download/img/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) throws IOException {
        LOGGER.info("[download] 파일 다운로드 시작 : {}",fileName);
        return reviewService.getObject(fileName);
    }

    @GetMapping("/image/{reviewId}/attach")
    public ResponseEntity downloadReviewImg(
            @PathVariable("reviewId") Long id) throws IOException {
        return reviewService.downloadImg(id);
    }

    @GetMapping("/user/me")
    public ResponseEntity findAllOfMyReviews() {
        List<ReviewResponseDto> responseDtoList = reviewService.findAllReviewsWrittenByYou();
        return ResponseEntity.ok().body(responseDtoList);
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity findAllReviewsOfUser(@PathVariable("user_id") long user_id) {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviewsWrittenByUser(user_id);
        return ResponseEntity.ok().body(responseDtoList);
    }

    @GetMapping("user/like/{review_id}")
    public ResponseEntity likeReview(@PathVariable("review_id") long review_id) {
        LOGGER.info("[likeReview] 좋아요 누르기 review-id = {}", review_id);
        try {
            ReviewResponseDto responseDto = reviewService.likeReview(review_id, 1);
            LOGGER.info("[likeReview] 좋아요 처리 완료", review_id);
            
            return ResponseEntity.ok().body(responseDto);
        }catch (RuntimeException e){
            LOGGER.info("[likeReview] 좋아요 중복 처리 불가", review_id);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("user/unlike/{review_id}")
    public ResponseEntity unlikeReview(@PathVariable("review_id") long review_id) {
        LOGGER.info("[likeReview] 좋아요 누르기 review-id = {}", review_id);
        try {
            ReviewResponseDto responseDto = reviewService.likeReview(review_id, -1);
            LOGGER.info("[likeReview] 좋아요 처리 완료", review_id);

            return ResponseEntity.ok().body(responseDto);
        }catch (RuntimeException e){
            LOGGER.info("[likeReview] 좋아요 중복 처리 불가", review_id);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
