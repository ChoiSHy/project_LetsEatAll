package com.letseatall.letseatall.data.repository.Menu;

import com.letseatall.letseatall.data.Entity.menu.MenuImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuImageFileRepository extends JpaRepository<MenuImageFile, Long> {
    Optional<MenuImageFile> findByMenuId(Long menu_id);
}
