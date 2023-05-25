package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
