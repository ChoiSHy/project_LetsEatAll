package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
