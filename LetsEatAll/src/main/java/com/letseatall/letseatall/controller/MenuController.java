package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
public class MenuController {
    MenuService menuService;

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

}
