package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Menu.MenuModifyDto;
import com.letseatall.letseatall.data.dto.common.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.service.MenuService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @ApiOperation(value="메뉴 정보 조회", notes="전달받은 메뉴 id를 통해 메뉴 정보를 조회한다.")
    public ResponseEntity<MenuResponseDto> getMenu(Long id) throws IOException {
        log.info("[getMenu] : getMenu 시작");
        MenuResponseDto responseDto = menuService.getMenu(id);
        log.info("[getMenu] : result: {}", responseDto.getName());

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @ApiImplicitParams({
            @ApiImplicitParam(name="X-AUTH-TOKEN", value = "로그인 성공 후 받은 access_token",
            required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value="메뉴 저장", notes="전달된 정보를 토대로 메뉴 데이터를 추가한다.")
    @PostMapping()
    public ResponseEntity<MenuResponseDto> saveMenu(@RequestPart MenuDto menuDto, @RequestPart MultipartFile file) throws IOException {
        long cur_time = System.currentTimeMillis();
        MenuResponseDto responseDto = menuService.saveMenu(menuDto, file);
        log.info("[saveMenu] Response Time: {}ms", System.currentTimeMillis() - cur_time);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @PostMapping("/franchise/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value="메뉴 저장 (프랜차이즈)", notes="프랜차이즈의 공통 메뉴를 추가한다.")
    public ResponseEntity saveFranchiseMenu(@RequestPart MenuDto menuDto,
                                            @RequestPart MultipartFile file) throws IOException {
        MenuResponseDto menuResponseDto = menuService.saveFranchiseMenu(menuDto, file);
        return ResponseEntity.ok(menuResponseDto);
    }
    @PutMapping()
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value="메뉴 수정", notes="메뉴의 가격을 수정한다.")
    public ResponseEntity<String> changeMenuPrice(@RequestBody IntChangeDto intChangeDto){
        boolean res = menuService.changeMenuPrice(intChangeDto);
        if(!res)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수정 실패하였습니다.");
        return ResponseEntity.status(HttpStatus.OK).body("수정 성공하였습니다.");
    }
    @DeleteMapping()
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value="메뉴 삭제", notes="메뉴를 삭제한다.")
    public ResponseEntity<String> delete(Long id){
        menuService.deleteMenu(id);
        return ResponseEntity.status(HttpStatus.OK).body("삭제되었습니다.");
    }

    @GetMapping("/restaurant/all")
    @ApiOperation(value="메뉴 리스트", notes="음식점의 메뉴 리스트를 불러온다.")
    public ResponseEntity<List<MenuResponseDto>> getAllMenus(Long rid){
        List<MenuResponseDto> rList = menuService.getAllMenu(rid);
        return ResponseEntity.status(HttpStatus.OK).body(rList);
    }
    @GetMapping("/franchise/list")
    @ApiOperation(value="메뉴 리스트(프랜차이즈)", notes="프랜차이즈의 공통 메뉴 리스트를 불러온다.")
    public ResponseEntity<List<MenuResponseDto>> getAllFranchiseMenus(Long fid){
        List<MenuResponseDto> mlist = menuService.getListFranchiseMenu(fid);
        return ResponseEntity.ok(mlist);
    }
    @GetMapping("/franchise/all")
    @ApiOperation(value="메뉴 리스트(프랜차이즈점)", notes="프랜차이즈 지점들의 메뉴들을 불러온다.")
    public ResponseEntity<List<MenuResponseDto>> getAllFranchiseRestaurantMenus_on(Long fid){
        List<MenuResponseDto> mlist = menuService.getAllFranchiseMenu(fid);
        return ResponseEntity.ok(mlist);
    }
    @GetMapping("/score/sum")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value="메뉴 평점 계산", notes="리뷰점수의 합을 메뉴점수로 저장한다.")
    public ResponseEntity sumScore(){
        menuService.sum();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/image/upload/{menu_id}")
    public ResponseEntity uploadImage(@PathVariable("menu_id") Long id, MultipartFile file){
        menuService.uploadMenuImage(id, file);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/modify")
    public ResponseEntity<MenuResponseDto> modify(@RequestPart MenuModifyDto modifyDto,
                                                  @RequestPart MultipartFile file) throws IOException {
        MenuResponseDto responseDto = menuService.modify(modifyDto, file);
        return ResponseEntity.ok(responseDto);
    }


}
