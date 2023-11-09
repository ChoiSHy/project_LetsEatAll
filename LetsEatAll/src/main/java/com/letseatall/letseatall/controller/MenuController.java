package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.dto.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.service.MenuService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {
    private final Logger log = LoggerFactory.getLogger(MenuController.class);
    private MenuService menuService;

    @Autowired
    public MenuController(MenuService menuService){
        this.menuService= menuService;
    }

    @GetMapping()
    public ResponseEntity<MenuResponseDto> getMenu(Long id){
        log.info("[getMenu] : getMenu 시작");
        MenuResponseDto responseDto = menuService.getMenu(id);
        log.info("[getMenu] : result: {}", responseDto.getName());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-AUTH-TOKEN", value = "로그인 성공 후 받은 access_token",
            required = true, dataType = "String", paramType = "header")
    })
    @PostMapping()
    public ResponseEntity<MenuResponseDto> saveMenu(@RequestBody MenuDto menuDto){
        long cur_time = System.currentTimeMillis();
        MenuResponseDto responseDto = menuService.saveMenu(menuDto);
        log.info("[saveMenu] Response Time: {}ms", System.currentTimeMillis() - cur_time);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @PostMapping("/franchise/save")
    public ResponseEntity saveFranchiseMenu(@RequestBody MenuDto menuDto){
        MenuResponseDto menuResponseDto = menuService.saveFranchiseMenu(menuDto);
        return ResponseEntity.ok(menuResponseDto);
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
    @GetMapping("/franchise/list")
    public ResponseEntity<List<MenuResponseDto>> getListFranchiseMenus(Long fid){
        List<MenuResponseDto> mlist = menuService.getListFranchiseMenu(fid);
        return ResponseEntity.ok(mlist);
    }
    @GetMapping("/franchise/all")
    public ResponseEntity<List<MenuResponseDto>> getAllFranchiseMenus_on(Long fid){
        List<MenuResponseDto> mlist = menuService.getAllFranchiseMenu(fid);
        return ResponseEntity.ok(mlist);
    }

}
