package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.dto.User.BadRequestException;
import com.letseatall.letseatall.service.ReviewService;
import com.letseatall.letseatall.service.awsS3.S3UploadService;
import io.swagger.annotations.ApiOperation;
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
import java.rmi.RemoteException;
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
    @ApiOperation(value = "리뷰 정보 요청",
            notes = "id에 해당하는 리뷰 반환. id는 review의 고유 id")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable("id") Long id) {
        ReviewResponseDto responseDto = reviewService.getReview(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/create")
    @ApiOperation(value = "리뷰 저장 기능",
            notes = "reviewDto: json, file: img-file")
    public ResponseEntity saveReview(@RequestPart ReviewDto reviewDto,
                                     @RequestPart MultipartFile file) throws IOException {
        try {
            ReviewResponseDto responseDto = reviewService.saveReview(reviewDto, file);
            return ResponseEntity.ok(responseDto);
        }catch (RemoteException e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
        catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    @ApiOperation(value = "리뷰 수정", notes = "리뷰 수정 기능")
    public ResponseEntity<ReviewResponseDto> modifyReview(@RequestPart ReviewModifyDto rmd,
                                                          @RequestPart MultipartFile file) throws IOException {
        try {
            ReviewResponseDto responseDto = reviewService.modifyReview(rmd, file);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (ResponseStatusException e) {
            throw e;
        }
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "리뷰 삭제 기능")
    public ResponseEntity<String> deleteReview(@PathVariable("id") Long id) {
        try {
            Long del_id = reviewService.deleteReview(id);
            return ResponseEntity.status(HttpStatus.OK).body(del_id + "게시글 삭제되었습니다.");
        } catch (ResponseStatusException e) {
            throw e;
        }
    }

    @GetMapping("/menu/reviews")
    @ApiOperation(value = "메뉴의 리뷰들 불러오기", notes = "mid에 해당하는 menu에 관한 모든 리뷰 불러오기")
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsAboutMenu(@RequestParam Long mid) {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviewsInMenu(mid);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @GetMapping("/restaurant/reviews")
    @ApiOperation(value = "식당의 리뷰들 불러오기", notes = "rid에 해당하는 restaurant에 관한 모든 리뷰 불러오기")
    public ResponseEntity<List<ReviewResponseDto>> getAllReviewsAboutRestaurant(@RequestParam Long rid) {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviewsInRestaurant(rid);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @GetMapping("/all")
    @ApiOperation(value = "모든 리뷰를 불러오기")
    public ResponseEntity getAllReviews() {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviews();
        return ResponseEntity.ok().body(responseDtoList);
    }

    @GetMapping("/user/me")
    @ApiOperation(value = "자신의 리뷰 불러오기", notes = "자신이 작성한 모든 리뷰 불러오기")
    public ResponseEntity findAllOfMyReviews() {
        List<ReviewResponseDto> responseDtoList = reviewService.findAllReviewsWrittenByYou();
        return ResponseEntity.ok().body(responseDtoList);
    }

    @GetMapping("/user/{user_id}")
    @ApiOperation(value = "사용자의 리뷰 불러오기", notes = "사용자가 작성한 모든 리뷰 불러오기")
    public ResponseEntity findAllReviewsOfUser(@PathVariable("user_id") long user_id) {
        List<ReviewResponseDto> responseDtoList = reviewService.getAllReviewsWrittenByUser(user_id);
        return ResponseEntity.ok().body(responseDtoList);
    }

    @GetMapping("user/like/{review_id}")
    @ApiOperation(value = "좋아요 누르기", notes = "리뷰의 좋아요")
    public ResponseEntity likeReview(@PathVariable("review_id") long review_id) {
        LOGGER.info("[likeReview] 좋아요 누르기 review-id = {}", review_id);
        try {
            ReviewResponseDto responseDto = reviewService.likeReview(review_id, 1);
            LOGGER.info("[likeReview] 좋아요 처리 완료", review_id);

            return ResponseEntity.ok().body(responseDto);
        } catch (RuntimeException e) {
            LOGGER.info("[likeReview] 좋아요 중복 처리 불가", review_id);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("user/unlike/{review_id}")
    @ApiOperation(value = "싫어요 누르기")
    public ResponseEntity unlikeReview(@PathVariable("review_id") long review_id) {
        LOGGER.info("[likeReview] 좋아요 누르기 review-id = {}", review_id);
        try {
            ReviewResponseDto responseDto = reviewService.likeReview(review_id, -1);
            LOGGER.info("[likeReview] 좋아요 처리 완료", review_id);

            return ResponseEntity.ok().body(responseDto);
        } catch (RuntimeException e) {
            LOGGER.info("[likeReview] 좋아요 중복 처리 불가", review_id);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
