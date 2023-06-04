package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Franchise;
import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.repository.custom.MenuBulkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuBulkRepository {
    @Query("select m from Menu as m where m.franchise.id = ?1 and m.restaurant.id = null ")
    List<Menu> findAllByFranchiseId(Long fid);

    @Query("select m from Menu m where m.restaurant.id = ?1")
    List<Menu> findAllByRestaurantId(Long rid);

    @Query("select m from Menu m where m.restaurant.id in :ids")
    List<Menu> findAllByRestaurantId(List<Long> ids);

    @Query("select m.id from Menu m where m.franchise.id = ?1")
    List<Long> findIdAllByFranchiseId(Long id);

}
