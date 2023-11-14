package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r where r.menu.id = ?1")
    List<Review> findAllByMenu(Long mid);

    @Query("select r from Review r where r.menu.restaurant.id = ?1")
    List<Review> findAllByRestaurant(Long rid);
    @Query("select r.id from Review r where r.menu.restaurant.id = ?1")
    List<Long> findIdAllByRestaurant(Long rid);
    @Query("select r from Review r where r.menu.franchise.id = ?1")
    List<Review> findAllByFranchise(Long id);
    @Query("select r.id from Review r where r.menu.franchise.id = ?1")
    List<Long> findIdAllByFranchise(Long id);

    List<Review> findAllByWriterId(Long id);
}
