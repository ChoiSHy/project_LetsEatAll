package com.letseatall.letseatall.data.repository.Menu;

import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.repository.custom.MenuBulkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuBulkRepository {
    @Query("select m from Menu as m where m.franchise.id = ?1 and m.restaurant.id = null ")
    List<Menu> findAllByFranchiseId(Long fid);

    @Query("select m from Menu m where m.restaurant.id = ?1")
    List<Menu> findAllByRestaurantId(Long rid);

    @Query("select m from Menu m where m.restaurant.id in :ids")
    List<Menu> findAllByRestaurantId(List<Long> ids);

    @Query("select m.id from Menu m where m.franchise.id = ?1")
    List<Long> findIdAllByFranchiseId(Long id);

    List<Menu> findAllByRestaurant_FranchiseId(Long fid);
    List<Menu> findAllByNameLike(String name);

    Page<Menu> findAllByRestaurantIdOrderByScore(Long rest_id, Pageable pageable);
    int countAllByRestaurantId(Long id);

    Page<Menu> findByScoreIsGreaterThan(double score, Pageable pageable);

}
