package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Preference;
import com.letseatall.letseatall.data.Entity.PreferenceKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferenceRepository extends JpaRepository<Preference,PreferenceKey > {
    List<Preference> findAllByUserId(long uid);
    boolean existsByUserIdAndCategoryId(long userId, int categoryId);
    Optional<Preference> findByUserIdAndCategoryId(long uid, int cid);

    List<Preference> findAllByUserIdOrderByScore(long uid);
}
