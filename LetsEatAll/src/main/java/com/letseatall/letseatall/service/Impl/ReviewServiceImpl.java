package com.letseatall.letseatall.service.Impl;

import ch.qos.logback.core.rolling.helper.FileStoreUtil;
import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.image.ImageFile;
import com.letseatall.letseatall.data.dto.Review.ReviewDto;
import com.letseatall.letseatall.data.dto.Review.ReviewModifyDto;
import com.letseatall.letseatall.data.dto.Review.ReviewResponseDto;
import com.letseatall.letseatall.data.repository.ImagefileRepository;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.ReviewRepository;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Value("${spring.img.path}")
    private String imgPath;
    private final ReviewRepository reviewRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final ImagefileRepository imgRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             MenuRepository menuRepository,
                             UserRepository userRepository,
                             ImagefileRepository imgRepository) {
        this.reviewRepository = reviewRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.imgRepository = imgRepository;
    }

 /*   public ReviewResponseDto saveReview(Long mid,
                                        String title,
                                        String content,
                                        int score,
                                        MultipartFile file) throws IOException {
        Menu menu = null;
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getByUid(userDetails.getUsername());
        LOGGER.info("[saveReview] 작성자: {}", user.getName());

        Optional<Menu> oMenu = menuRepository.findById(mid);
        LOGGER.info("[saveReview] 메뉴 불러오기 mid = {}", mid);
        if (oMenu.isPresent()) {
            menu = oMenu.get();
            LOGGER.info("[saveReview] 불러온 메뉴: {}", menu);
        }
        LOGGER.info("[saveReview] Review 객체 생성 시작");
        ImageFile img = new ImageFile();
        img.setUpload_file_name(file.getOriginalFilename());

        Review newReview = new Review();
        newReview.setTitle(title);
        newReview.setContent(content);
        newReview.setScore(score);
        newReview.setRecCnt(0);
        newReview.setMenu(menu);
        newReview.setWriter(user);

        img.setReview(newReview);
        LOGGER.info("[saveReview] Review 객체 생성 완료: {}", newReview);

        Review savedReview = imgRepository.save(img).getReview();

        LOGGER.info("[saveReview] Review 저장 완료");

        if (!file.isEmpty()) {
            String fullPath = imgPath + img.getStore_file_name()+".jpg";
            LOGGER.info("[uploadReviewImg] 저장할 이미지 위치 : {}", fullPath);
            file.transferTo(new File(fullPath));
            LOGGER.info("[uploadReviewImg] 이미지 저장 완료");
        }
        return getReviewResponseDto(savedReview);
    }*/

    public ReviewResponseDto saveReview(ReviewDto reviewDto, List<MultipartFile> files) throws IOException {
        Menu menu = null;
        LOGGER.info("[saveReview] 시작");
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getByUid(userDetails.getUsername());
        LOGGER.info("[saveReview] 작성자: {}", user.getName());

        Optional<Menu> oMenu = menuRepository.findById(reviewDto.getMid());
        LOGGER.info("[saveReview] 메뉴 불러오기 mid = {}", reviewDto.getMid());
        if (oMenu.isPresent()) {
            menu = oMenu.get();
            LOGGER.info("[saveReview] 불러온 메뉴: {}", menu);
        }
        LOGGER.info("[saveReview] Review 객체 생성 시작");

        List<ImageFile> imgList = new ArrayList();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                ImageFile img = new ImageFile();
                img.setUploadedFileName(file.getOriginalFilename());
                img.setStoredName();
                imgList.add(img);
                String fullPath = imgPath + img.getStoredFileName() + ".jpg";
                LOGGER.info("[uploadReviewImg] 저장할 이미지 위치 : {}", fullPath);
                file.transferTo(new File(fullPath));
                LOGGER.info("[uploadReviewImg] 이미지 저장 완료");
            }
        }
        Review newReview = new Review();
        newReview.setTitle(reviewDto.getTitle());
        newReview.setContent(reviewDto.getContent());
        newReview.setScore(reviewDto.getScore());
        newReview.setRecCnt(0);
        newReview.setMenu(menu);
        newReview.setWriter(user);

        imgList.forEach(img -> {
            img.setReview(newReview);
        });

        LOGGER.info("[saveReview] Review 객체 생성 완료: {}", newReview);

        Review savedReview = reviewRepository.save(newReview);
        LOGGER.info("[saveReview] Review imgList");
        savedReview.getImgList().forEach(img ->{
            LOGGER.info("[saveReview] img = {}",img);
        });
        LOGGER.info("[saveReview] Review 저장 완료");
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
    public List<ReviewResponseDto> getReviewsForUser(Long id) {
        List<Review> reviews = reviewRepository.findAllByWriterId(id);

        List<ReviewResponseDto> responseDtos = new ArrayList<>();
        /* 병합 과정에서 포함 못함. 추후에 작성*/

        // 변환된 ReviewResponseDto 리스트를 반환합니다.
        return responseDtos;
    }

    @Transactional
    public ReviewResponseDto modifyReview(ReviewModifyDto rmd, List<MultipartFile> files) throws IOException {
        LOGGER.info("[modifyReview] 수정 시작");
        Optional<Review> oReview = reviewRepository.findById(rmd.getId());
        Review review = oReview.orElse(null);
        if (review!=null) {
            LOGGER.info("[modifyReview] 수정할 정보 불러오기 성공. {}", review.toString());
            review.setTitle(rmd.getTitle());
            review.setContent(rmd.getContent());
            review.setScore(rmd.getScore());
            review.getMenu();
            review.getWriter();

            LOGGER.info("[modifyReview] 이미지 정보 수정 시작");
            List<ImageFile> imageFileList = review.getImgList();

            for (MultipartFile file : files){
                if(!file.isEmpty()){
                    if (!imgRepository.existsByUploadedFileName(file.getOriginalFilename())){
                        ImageFile img = new ImageFile();
                        img.setUploadedFileName(file.getOriginalFilename());
                        img.setStoredName();
                        review.addImg(img);
                        String fullPath = imgPath + img.getStoredFileName() + ".jpg";
                        LOGGER.info("[uploadReviewImg] 저장할 이미지 위치 : {}", fullPath);
                        file.transferTo(new File(fullPath));
                        imageFileList.add(img);

                        LOGGER.info("[uploadReviewImg] 이미지 저장 완료");
                    }
                }
            }
            Review modifiedReview = reviewRepository.save(review);

            return getReviewResponseDto(modifiedReview);
        }

        return null;
    }

    @Override
    public Long deleteReview(Long id) {
        Optional<Review> oReview = reviewRepository.findById(id);
        Review review = oReview.orElse(null);

        for (ImageFile img : review.getImgList()) {
            review.removeImg(img);
        }
        reviewRepository.deleteById(id);
        return id;
    }

    private ReviewResponseDto getReviewResponseDto(Review review) {
        if (review == null) return null;
        Menu menu = review.getMenu();
        User user = review.getWriter();
        ReviewResponseDto rrd = ReviewResponseDto.builder()
                .review_id(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .score(review.getScore())
                .rec_count(review.getRecCnt()).build();
        if (menu != null) {
            rrd.setMenu_id(menu.getId());
            rrd.setMenu_name(menu.getName());
        }
        if (user != null) {
            rrd.setWriter(user.getName());
            rrd.setUser_id(user.getId());
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

    public ResponseEntity downloadImg(Long id) throws MalformedURLException {
        Optional<ImageFile> findFile = imgRepository.findByReviewId(id);
        ImageFile file = findFile.orElse(null);
        if (file == null) return null;

        String storedName = file.getStoredFileName();
        String uploadedName = file.getUploadedFileName();

        String contentDisposition = "attachment; filename=\"" + uploadedName + "\"";
        UrlResource resource = new UrlResource("file:" + imgPath + storedName + ".jpg");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}