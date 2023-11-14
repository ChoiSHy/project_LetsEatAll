package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Review.LikeHistory;
import com.letseatall.letseatall.data.Entity.Review.LikeHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeHistoryRepository extends JpaRepository<LikeHistory, LikeHistoryKey> {
    Optional<LikeHistory> findByReviewIdAndUserId(long reviewId, long userId);

    boolean existsByReviewIdAndUserId(long reviewId, long userId);
}
