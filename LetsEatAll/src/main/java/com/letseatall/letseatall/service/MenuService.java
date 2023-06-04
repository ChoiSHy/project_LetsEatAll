package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuElement;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;

import java.util.List;

public interface MenuService {
    MenuResponseDto saveMenu(MenuDto menuDto);
    MenuResponseDto getMenu(Long id);
    boolean changeMenuPrice(IntChangeDto intChangeDto);
    void deleteMenu(Long id);

    List<MenuResponseDto> getAllMenu(Long rid);
    List<MenuResponseDto> getAllMenu(int start, int size);
}
