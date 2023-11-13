package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.repository.custom.CategoryBulkRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, CategoryBulkRepository
{
    Optional<Category> findByName(String name);
    Optional<Category> findByNameLike(String name);
}
