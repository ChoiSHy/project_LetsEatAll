package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuElement;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;

public interface MenuService {
    MenuResponseDto saveMenu(MenuDto menuDto);
    MenuResponseDto getMenu(Long id);
    boolean changeMenuPrice(Long id, int price);
    MenuElement getMenuElement(Long id);
    void deleteMenu(Long id);
}
