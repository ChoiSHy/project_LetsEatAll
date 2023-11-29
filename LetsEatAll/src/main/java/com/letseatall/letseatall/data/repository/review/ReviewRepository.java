package com.letseatall.letseatall.data.repository.review;

import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.menu.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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

    int countAllByWriterId(Long id);
    int countAllByMenuId(Long menu_id);
    @Query(value="select count(rv) from Review rv left join rv.menu m where m.restaurant.id=?1")
    int countReviewsByRestaurantId(Long rest_id);

    Optional<Review> findByWriterIdAndMenuIdOrderByUpdatedAt(long writerId, long menuId);
}
