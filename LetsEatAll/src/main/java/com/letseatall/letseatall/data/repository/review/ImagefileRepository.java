package com.letseatall.letseatall.data.repository.review;

import com.letseatall.letseatall.data.Entity.Review.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImagefileRepository extends JpaRepository<ImageFile, Long> {
    Optional<ImageFile> findByReviewId(Long id);
}
