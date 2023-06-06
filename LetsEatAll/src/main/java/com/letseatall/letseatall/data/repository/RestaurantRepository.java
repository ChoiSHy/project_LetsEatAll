package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Franchise;
import com.letseatall.letseatall.data.Entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("select r from Restaurant as r where r.franchise.id = ?1")
    List<Restaurant> findAllByFranchiseId(Long id);
    @Query("select r.id from Restaurant r where r.franchise.id = ?1")
    List<Long> findIdAllByFranchiseId(Long id);

    Page<Restaurant> findAllByCategoryId(int id, Pageable pageable);
    Page<Restaurant> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
