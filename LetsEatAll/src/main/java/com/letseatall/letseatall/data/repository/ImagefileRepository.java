package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.image.ImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImagefileRepository extends JpaRepository<ImageFile, Long> {
    Optional<ImageFile> findByReviewId(Long id);
    boolean existsByUploadedFileName(String uploadedFileName);
}
