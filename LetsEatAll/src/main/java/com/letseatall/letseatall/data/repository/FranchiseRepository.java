package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FranchiseRepository extends JpaRepository<Franchise,Long> {
}
