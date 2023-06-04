package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.dto.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    private MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService){
        this.menuService= menuService;
    }

    @GetMapping()
    public ResponseEntity<MenuResponseDto> getMenu(Long id){
        MenuResponseDto responseDto = menuService.getMenu(id);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping()
    public ResponseEntity<MenuResponseDto> saveMenu(@RequestBody MenuDto menuDto){
        MenuResponseDto responseDto = menuService.saveMenu(menuDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @PutMapping()
    public ResponseEntity<String> changeMenuPrice(@RequestBody IntChangeDto intChangeDto){
        boolean res = menuService.changeMenuPrice(intChangeDto);
        if(!res)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수정 실패하였습니다.");
        return ResponseEntity.status(HttpStatus.OK).body("수정 성공하였습니다.");
    }
    @DeleteMapping()
    public ResponseEntity<String> delete(Long id){
        menuService.deleteMenu(id);
        return ResponseEntity.status(HttpStatus.OK).body("삭제되었습니다.");
    }

    @GetMapping("/restaurant/all")
    public ResponseEntity<List<MenuResponseDto>> getAllMenus(Long rid){
        List<MenuResponseDto> rList = menuService.getAllMenu(rid);
        return ResponseEntity.status(HttpStatus.OK).body(rList);
    }

}
