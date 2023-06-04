package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByMid(Long mid);
    List<Review> findAllByUid(Long uid);
}
