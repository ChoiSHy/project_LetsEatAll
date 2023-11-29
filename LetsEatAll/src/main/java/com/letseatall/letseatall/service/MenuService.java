package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.dto.Menu.MenuListDto;
import com.letseatall.letseatall.data.dto.Menu.MenuModifyDto;
import com.letseatall.letseatall.data.dto.common.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MenuService {
    MenuResponseDto saveMenu(MenuDto menuDto, MultipartFile file) throws IOException;

    MenuResponseDto saveFranchiseMenu(MenuDto menuDto, MultipartFile file) throws IOException;

    MenuResponseDto getMenu(Long id) throws IOException;

    boolean changeMenuPrice(IntChangeDto intChangeDto);

    void deleteMenu(Long id);

    List<MenuResponseDto> getAllMenu(Long rid);

    List<MenuResponseDto> getAllMenu(int start, int size);

    List<MenuResponseDto> getListFranchiseMenu(Long fid);

    List<MenuResponseDto> getAllFranchiseMenu(Long fid);

    void sum();
    void uploadMenuImage(long menu_id, MultipartFile file);
    MenuResponseDto modify(MenuModifyDto menuDto, MultipartFile file) throws IOException;

    MenuResponseDto makeDto(Menu menu) throws IOException;
    MenuListDto makeListDto(Menu menu);
}
