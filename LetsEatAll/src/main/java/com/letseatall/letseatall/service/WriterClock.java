package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WriterClock {
    private final Logger LOGGER = LoggerFactory.getLogger(WriterClock.class);
    private final ReviewRepository reviewRepository;
    public boolean check(Menu menu, User writer){
        LOGGER.info("[WriterClock] 최근 남긴 ");
        Review review = reviewRepository.findByWriterIdAndMenuIdOrderByUpdatedAt(writer.getId(), menu.getId()).orElse(null);
        if(review!= null){
            LOGGER.info("[WriterClock] 리뷰 불러오기 완료 : {}", review);
            LocalDateTime updateTime = review.getUpdatedAt();
            Duration diff = Duration.between(updateTime, LocalDateTime.now());

            return diff.toHours() >= 24;
        }
        return true;
    }
}
