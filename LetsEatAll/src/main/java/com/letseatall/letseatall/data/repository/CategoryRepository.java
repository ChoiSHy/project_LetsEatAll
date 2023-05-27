package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
