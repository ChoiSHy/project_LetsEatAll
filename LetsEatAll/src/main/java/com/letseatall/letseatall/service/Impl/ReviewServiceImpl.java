package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.common.Seeker;
import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.Entity.Review.LikeHistory;
import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.Review.ImageFile;
import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.dto.User.BadRequestException;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.data.repository.Menu.MenuRepository;
import com.letseatall.letseatall.data.repository.review.ImagefileRepository;
import com.letseatall.letseatall.data.repository.review.ReviewRepository;
import com.letseatall.letseatall.service.ReviewService;
import com.letseatall.letseatall.service.WriterClock;
import com.letseatall.letseatall.service.awsS3.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    @Value("${spring.img.path}")
    private String imgPath;
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final ImagefileRepository imgRepository;
    private final LikeHistoryRepository historyRepository;
    private final S3UploadService s3UploadService;
    private final ScoreService scoreService;
    private final Seeker seeker;
    private final PreferenceService preferenceService;
    private final WriterClock clock;

    private final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);


    public ReviewResponseDto saveReview(ReviewDto reviewDto, MultipartFile file) throws IOException {
        Menu menu = null;
        int score = reviewDto.getScore();
        int cid = 0;
        long uid;
        LOGGER.info("[saveReview] 시작");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getByUid(userDetails.getUsername());
        uid = user.getId();
        LOGGER.info("[saveReview] 작성자: {}", user.getName());

        Optional<Menu> oMenu = menuRepository.findById(reviewDto.getMid());
        LOGGER.info("[saveReview] 메뉴 불러오기 mid = {}", reviewDto.getMid());
        if (oMenu.isPresent()) {
            menu = oMenu.get();
            LOGGER.info("[saveReview] 불러온 메뉴: {}", menu);
            cid = menu.getCategory().getId();
        }

        if (!clock.check(menu, user)){
            LOGGER.error("[WriterClock] 최근 작성 후 24시간이 지나지 않음");
            throw new BadRequestException("최근 리뷰 작성 후, 24시간이 지나야 합니다.");
        }
        LOGGER.info("[saveReview] Review 객체 생성 시작");
        ImageFile img = null;
        if (file != null && !file.isEmpty()) {
            img = new ImageFile();
            String[] res = uploadReviewImage(file);
            if (res != null) {
                img.setUrl(res[0]);
                img.setStoredName(res[1]);
            }
            LOGGER.info("[uploadReviewImg] 이미지 저장 완료");
        }

        Review newReview = new Review();
        newReview.setContent(reviewDto.getContent());
        newReview.setScore(reviewDto.getScore());
        newReview.setLike_cnt(0);
        newReview.setUnlike_cnt(0);
        newReview.setMenu(menu);
        newReview.setWriter(user);
        newReview.setImg(img);
        LOGGER.info("[saveReview] Review 객체 생성 완료: {}", newReview);
        try {
            if(!seeker.predict(newReview))
                throw new BadRequestException("혐오표현 감지!");
        } catch (RestClientException e) {
            throw e;
        } catch (BadRequestException e) {
            throw e;
        }
        Review savedReview = reviewRepository.save(newReview);
        LOGGER.info("[saveReview] Review 저장 완료");
        user.setScore(user.getScore()+5);
        userRepository.save(user);
        savedReview = scoreService.plusScore(savedReview);

        preferenceService.recordUserPrefer(score - 5, cid, uid);
        LOGGER.info("saveReview] 사용자의 선호도 조정");
        return getReviewResponseDto(savedReview);
    }

    // 리뷰 조회
    @Override
    public ReviewResponseDto getReview(Long id) {
        Optional<Review> oReview = reviewRepository.findById(id);
        if (oReview.isPresent()) {
            Review review = oReview.get();

            return getReviewResponseDto(review);
        }
        return null;
    }

    // 리뷰 조회(해당 메뉴의 모든 리뷰 조회)
    @Override
    @Transactional
    public List<ReviewResponseDto> getAllReviewsInMenu(Long mid) {
        List<Review> reviewList = reviewRepository.findAllByMenu(mid);
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        for (Review ent : reviewList) {
            ReviewResponseDto rrd = getReviewResponseDto(ent);
            responseDtoList.add(rrd);
        }
        return responseDtoList;
    }

    @Override
    public List<ReviewResponseDto> getAllReviews() {
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        for (Review review : reviewRepository.findAll()) {
            ReviewResponseDto rrd = getReviewResponseDto(review);
            responseDtoList.add(rrd);
        }
        return responseDtoList;
    }

    @Override
    public List<ReviewResponseDto> getAllReviewsWrittenByUser(Long user_id) {
        User user = userRepository.getById(user_id);
        LOGGER.info("[findAllReviewsWrittenByUser] 작성자 = {} ({})", user.getUsername(), user.getName());

        LOGGER.info("[findAllReviewsWrittenByUser] 작성자의 리뷰 리스트 불러오기 시작");
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        for (Review review : reviewRepository.findAllByWriterId(user.getId())) {
            ReviewResponseDto rrd = getReviewResponseDto(review);
            responseDtoList.add(rrd);
        }
        LOGGER.info("[findAllReviewsWrittenByUser] 작성자의 리뷰 리스트 불러오기 완료");
        return responseDtoList;
    }

    @Transactional
    public ReviewResponseDto modifyReview(ReviewModifyDto rmd, MultipartFile file) throws IOException {
        LOGGER.info("[modifyReview] 수정 시작");
        Optional<Review> oReview = reviewRepository.findById(rmd.getId());
        Review review = oReview.orElse(null);
        if (!isWriter(review.getWriter())) {
            LOGGER.info("[modifyReview] 수정 권한 없음");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (review != null) {
            LOGGER.info("[modifyReview] 수정할 정보 불러오기 성공. {}", review.toString());
            review.setContent(rmd.getContent());
            if (rmd.getScore() != review.getScore()) {
                Menu menu = review.getMenu();
                int review_size = reviewRepository.countAllByMenuId(menu.getId());
                double total = menu.getScore() * review_size + rmd.getScore();
                menu.setScore(total / (review_size + 1));
            }
            review.setScore(rmd.getScore());

            LOGGER.info("[modifyReview] 이미지 정보 수정 시작");

            if (file != null && !file.isEmpty()) {
                String[] res = uploadReviewImage(file);
                ImageFile img = new ImageFile();
                if (res != null) {
                    img.setUrl(res[0]);
                    img.setStoredName(res[1]);
                }
                LOGGER.info("[uploadReviewImg] 이미지 저장 완료");
            }
            Review modifiedReview = reviewRepository.save(review);
            return getReviewResponseDto(modifiedReview);
        }
        return null;
    }

    private boolean isWriter(User writer) {
        LOGGER.info("[isWriter] 삭제 권한 확인");
        UserDetails you = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOGGER.info("[isWriter] you : {}, writer : {}", you.getUsername(), writer.getUsername());
        return you.getUsername().equals(writer.getUid());
    }

    @Override
    public Long deleteReview(Long id) {
        Optional<Review> oReview = reviewRepository.findById(id);
        Review review = oReview.orElse(null);
        if (isWriter(review.getWriter())) {
            ImageFile img = review.getImg();
            review.setImg(null);
            reviewRepository.deleteById(id);
            LOGGER.info("[deleteReview] 삭제 완료");
            return id;
        }
        LOGGER.info("[deleteReview] 삭제 권한 없음");
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    private ReviewResponseDto getReviewResponseDto(Review review) {
        if (review == null) return null;
        Menu menu = review.getMenu();
        User user = review.getWriter();
        ReviewResponseDto rrd = ReviewResponseDto.builder()
                .review_id(review.getId())
                .content(review.getContent())
                .score(review.getScore())
                .like_count(review.getLike_cnt())
                .unlike_count(review.getUnlike_cnt())
                .updatedAt(review.getUpdatedAt())
                .build();
        if (menu != null) {
            rrd.setMenu_id(menu.getId());
            rrd.setMenu_name(menu.getName());
        }
        if (user != null) {
            rrd.setWriter(user.getName());
            rrd.setUser_id(user.getId());
        }
        ImageFile img = imgRepository.findByReviewId(review.getId()).orElse(null);
        if (img != null) {
            rrd.setImg_url(img.getUrl());
        }
        return rrd;
    }


    @Transactional
    public List<ReviewResponseDto> getAllReviewsInRestaurant(Long rid) {
        List<Review> reviewList = reviewRepository.findAllByRestaurant(rid);
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        for (Review ent : reviewList) {
            ReviewResponseDto rrd = getReviewResponseDto(ent);
            responseDtoList.add(rrd);
        }
        return responseDtoList;
    }

    @Transactional
    public List<ReviewResponseDto> getAllReviewsInFranchise(Long fid) {
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        reviewRepository.findAllByFranchise(fid)
                .forEach(rev -> responseDtoList.add(
                        getReviewResponseDto(rev))
                );
        return responseDtoList;
    }

    public List<ReviewResponseDto> findAllReviewsWrittenByYou() {
        LOGGER.info("[findAllReviewsWrittenByYou] 메서드 동작 시작");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getByUid(userDetails.getUsername());
        LOGGER.info("[findAllReviewsWrittenByYou] 작성자 = {} ({})", user.getUsername(), user.getName());

        LOGGER.info("[findAllReviewsWrittenByYou] 작성자의 리뷰 리스트 불러오기 시작");
        List<ReviewResponseDto> responseDtoList = new ArrayList<>();
        for (Review review : reviewRepository.findAllByWriterId(user.getId())) {
            ReviewResponseDto rrd = getReviewResponseDto(review);
            responseDtoList.add(rrd);
        }
        LOGGER.info("[findAllReviewsWrittenByYou] 작성자의 리뷰 리스트 불러오기 완료");
        return responseDtoList;
    }

    private User isDuplicated(long reviewId) {
        LOGGER.info("[isDuplicated] 중복 추천 여부 검사");
        try {
            UserDetails ud = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.getByUid(ud.getUsername());
            boolean res = historyRepository.existsByReviewIdAndUserId(reviewId, user.getId());
            if (res) {
                LOGGER.info("[isDuplicated] 중복 추천");
                return null;
            } else {
                LOGGER.info("[isDuplicated] 추천 가능");
                return user;
            }
        } catch (Exception e) {
            LOGGER.info("[isDuplicated] 토큰 정보 확인 불가");
            throw new RuntimeException("토큰 정보 확인 불가");
        }
    }

    public ReviewResponseDto likeReview(long reviewId, int score) {
        LOGGER.info("[likeReview] 좋아요 추가 시작");
        ReviewResponseDto reviewDto = null;
        User user = null;

        // 추천 가능
        if ((user = isDuplicated(reviewId)) != null) {
            Review review = reviewRepository.findById(reviewId).orElse(null);
            if (review != null) {
                LOGGER.info("[likeReview] 리뷰의 좋아요 개수 추가 작업");
                User writer = review.getWriter();
                if (score > 0) {
                    review.setLike_cnt(review.getLike_cnt() + score);
                } else {
                    review.setUnlike_cnt(review.getUnlike_cnt() - score);
                }
                writer.setScore(writer.getScore() + score);

                reviewRepository.save(review);
                LOGGER.info("[likeReview] 추가 완료. 기록 시작");
                LikeHistory history = LikeHistory.builder()
                        .reviewId(review.getId())
                        .userId(user.getId())
                        .review(review)
                        .user(user)
                        .build();
                historyRepository.save(history);
                LOGGER.info("[likeReview] 기록 완료. ");

                reviewDto = getReviewResponseDto(review);
            }
        }
        // 중복일 경우 불가능
        else {
            throw new RuntimeException("중복 추천은 불가합니다.");
        }
        return reviewDto;
    }

    @Transactional
    public String[] uploadReviewImage(MultipartFile file) {
        String[] result = null;
        if (file != null) {
            result = s3UploadService.uploadFileToS3(file, "Images");
        }
        return result;
    }

    public ResponseEntity getImg(String storedFileName) throws IOException {
        LOGGER.info("[ReviewService - getObject]");
        return new ResponseEntity(s3UploadService.getObject(storedFileName), HttpStatus.OK);
    }
}