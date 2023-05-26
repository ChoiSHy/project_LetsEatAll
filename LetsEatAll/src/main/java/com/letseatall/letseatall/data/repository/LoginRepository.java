package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface LoginRepository extends JpaRepository<Login, String> {
    @Query("select l.id from Login l where l.uid = ?1")
    String findIdByUid(Long uid);
}
