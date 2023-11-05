package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long > {
    User getByUid(String uid);
    boolean existsByUid(String uid);
    void deleteByUid(String uid);
    Long getIdByUid(String uid);

}
