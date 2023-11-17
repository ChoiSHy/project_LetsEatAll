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
    List<Restaurant> findAllByCategoryId(int id);
    List<Restaurant> findAllByNameLike(String name);
    List<Restaurant> findAllByCategoryIdOrderByNameAsc(int id);
    List<Restaurant> findAllByCategoryIdOrderByNameDesc(int id);
    List<Restaurant> findAllByCategoryIdOrderByScoreDesc(int id);
    List<Restaurant> findAllByCategoryIdOrderByScoreAsc(int id);
    List<Restaurant> findAllByNameLikeOrderByNameAsc(String name);
    List<Restaurant> findAllByNameLikeOrderByNameDesc(String name);
    List<Restaurant> findAllByNameLikeOrderByScoreDesc(String name);
    List<Restaurant> findAllByNameLikeOrderByScoreAsc(String name);

    Page<Restaurant> findAllByCategoryId(int id, Pageable pageable);
    Page<Restaurant> findAllByNameContainingIgnoreCase(String name, Pageable pageable);


    Page<Restaurant> findAllByCategoryIdOrderByScore(int id, Pageable pageable);
    Page<Restaurant> findAllByNameLikeOrderByScoreAsc(String name, Pageable pageable);


}
