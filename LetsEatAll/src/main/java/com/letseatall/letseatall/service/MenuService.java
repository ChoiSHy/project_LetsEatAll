package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuElement;
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
}
