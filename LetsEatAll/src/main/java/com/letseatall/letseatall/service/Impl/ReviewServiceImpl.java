package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.ReviewRepository;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             MenuRepository menuRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ReviewResponseDto saveReview(ReviewDto reviewDto) {
        Menu menu = menuRepository.findById(reviewDto.getMid()).get();
        if(menu == null)
            return null;
        User user = userRepository.findById(reviewDto.getUid()).get();
        if(user == null)
            return null;

        // Create a new Review entity from the ReviewDto
        Review review = Review.builder()
                .uid(reviewDto.getUid())
                .writer(reviewDto.getWriter())
                .mid(reviewDto.getMid())
                .menu(reviewDto.getMenu())
                .title(reviewDto.getTitle())
                .content(reviewDto.getContent())
                .score(reviewDto.getScore())
                .pid(reviewDto.getImg())
                .recCnt(0)
                .build();
        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDto.builder()
                .uid(savedReview.getUid())
                .mid(savedReview.getMid())
                .title(savedReview.getTitle())
                .content(savedReview.getContent())
                .score(savedReview.getScore())
                .img(savedReview.getPid())
                .count(savedReview.getRecCnt())
                .build();
    }

    // 리뷰 조회
    @Override
    public ReviewResponseDto getReview(Long id) {
        Review review = reviewRepository.getById(id);
        ReviewResponseDto responseReview = ReviewResponseDto.builder()
                .uid(review.getUid())
                .writer(review.getWriter())
                .mid(review.getMid())
                .menu(review.getMenu())
                .title(review.getTitle())
                .content(review.getContent())
                .score(review.getScore())
                .img(review.getPid())
                .count(review.getRecCnt())
                .build();
        return responseReview;
    }

    // 리뷰 조회(해당 메뉴의 모든 리뷰 조회)
    @Override
    public List<ReviewResponseDto> getReviewsForMenu(Long id) {
        List<Review> reviews = reviewRepository.findAllByMid(id);

        List<ReviewResponseDto> responseDtos = new ArrayList<>();
        for (Review review : reviews) {
            ReviewResponseDto responseDto = new ReviewResponseDto();
            responseDto.setId(review.getId());
            responseDto.setUid(review.getUid());
            responseDto.setWriter(review.getWriter());
            responseDto.setMid(review.getMid());
            responseDto.setMenu(review.getMenu());
            responseDto.setTitle(review.getTitle());
            responseDto.setContent(review.getContent());
            responseDto.setImg(review.getPid());
            responseDto.setScore(review.getScore());
            responseDto.setCount(review.getRecCnt());

            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    // 리뷰 조회(해당 사용자가 작성한 모든 리뷰 조회)
    @Override
    public List<ReviewResponseDto> getReviewsForUser(Long id) {
        List<Review> reviews = reviewRepository.findAllByUid(id);

        List<ReviewResponseDto> responseDtos = new ArrayList<>();
        for (Review review : reviews) {
            ReviewResponseDto responseDto = new ReviewResponseDto();
            responseDto.setId(review.getId());
            responseDto.setUid(review.getUid());
            responseDto.setWriter(review.getWriter());
            responseDto.setMid(review.getMid());
            responseDto.setMenu(review.getMenu());
            responseDto.setTitle(review.getTitle());
            responseDto.setContent(review.getContent());
            responseDto.setImg(review.getPid());
            responseDto.setScore(review.getScore());
            responseDto.setCount(review.getRecCnt());

            responseDtos.add(responseDto);
        }

        // 변환된 ReviewResponseDto 리스트를 반환합니다.
        return responseDtos;
    }

    //리뷰 수정
    @Override
    public ReviewResponseDto updateReview(Long id, ReviewDto reviewDto) {
        Review review = reviewRepository.findById(id).get();
        if(review == null)
            return null;

        // 리뷰 내용 수정
        review.setTitle(reviewDto.getTitle());
        review.setContent(reviewDto.getContent());
        review.setScore(reviewDto.getScore());
        review.setPid(reviewDto.getImg());

        Review updatedReview = reviewRepository.save(review);

        User user = userRepository.findById(updatedReview.getUid()).get();
        if(user == null)
            return null;
        Menu menu = menuRepository.findById(updatedReview.getMid()).get();
        if(menu == null)
            return null;

        ReviewResponseDto responseDto = new ReviewResponseDto();
        responseDto.setId(updatedReview.getId());
        responseDto.setUid(updatedReview.getUid());
        responseDto.setWriter(updatedReview.getWriter());
        responseDto.setMid(updatedReview.getMid());
        responseDto.setMenu(updatedReview.getMenu());
        responseDto.setTitle(updatedReview.getTitle());
        responseDto.setContent(updatedReview.getContent());
        responseDto.setImg(updatedReview.getPid());
        responseDto.setScore(updatedReview.getScore());
        responseDto.setCount(updatedReview.getRecCnt());

        return responseDto;
    }

    // 리뷰 삭제
    @Override
    public void deleteReview(Long id) {
        Optional<Review> optionalReview = reviewRepository.findById(id);

        if (optionalReview.isPresent()) {
            reviewRepository.deleteById(id);
            System.out.println("리뷰" + id + "삭제 완료");
        } else {
            System.out.println("리뷰" + id + "찾을 수 없음");
            throw new RuntimeException("Review not found with id " + id);
        }
    }
}
