package com.letseatall.letseatall.data.repository.custom;

import com.letseatall.letseatall.data.Entity.menu.Menu;

import java.util.List;

public interface MenuBulkRepository {
    void saveAll(List<Menu> menus);
}
