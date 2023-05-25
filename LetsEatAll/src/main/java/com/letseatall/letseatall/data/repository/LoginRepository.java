package com.letseatall.letseatall.data.repository;

import com.letseatall.letseatall.data.Entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, String> {
}
