package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long > {
}
