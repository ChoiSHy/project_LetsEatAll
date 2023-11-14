package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Review.LikeHistory;
import com.letseatall.letseatall.data.Entity.Review.LikeHistoryKey;
import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.service.ReviewService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewServiceImplTest {
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private MenuRepository menuRepository = Mockito.mock(MenuRepository.class);
    private ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
    private ImagefileRepository imagefileRepository = Mockito.mock(ImagefileRepository.class);
    private LikeHistoryRepository historyRepository = Mockito.mock(LikeHistoryRepository.class);
    private ReviewService reviewService;


    @BeforeEach
    public void setUpTest() {
        reviewService = new ReviewServiceImpl(reviewRepository, menuRepository, userRepository, imagefileRepository, historyRepository);
    }

    @Test
    void isDuplicatedTest() {
    }

}