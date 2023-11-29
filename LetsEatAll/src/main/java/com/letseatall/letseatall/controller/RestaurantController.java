package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.RestaurantService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ListResourceBundle;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final Logger LOGGER = LoggerFactory.getLogger(RestaurantService.class);
    @Autowired
    public RestaurantController(RestaurantService restaurantService){
        this.restaurantService=restaurantService;
    }
    @GetMapping("/restaurant")
    @ApiOperation(value= "음식점 정보", notes="id를 가진 음식점 정보 전달")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(Long id){
        LOGGER.info("[getRestaurant] 음식점 정보 검색 id : {}",id);
        RestaurantResponseDto responseDto = restaurantService.getRestaurant(id);
        if(responseDto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @GetMapping("/franchise")
    @ApiOperation(value= "프랜차이즈 정보", notes="id를 가진 프랜차이즈 정보 전달")
    public ResponseEntity<FranchiseResponseDto> getFranchise(Long id){
        FranchiseResponseDto responseDto = restaurantService.getFranchise(id);
        if(responseDto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @PostMapping("/restaurant/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value= "음식점 정보 저장", notes="음식점 정보를 저장한다.")
    public ResponseEntity<RestaurantResponseDto> saveRestaurant(@RequestBody RestaurantDto restaurantDto){
        RestaurantResponseDto responseDto = restaurantService.saveRestaurant(restaurantDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @DeleteMapping("/restaurant/remove")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value= "음식점 삭제", notes="음식점 정보를 삭제.")
    public ResponseEntity<String> deleteRestaurant(Long id){
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료되었습니다.");
    }


    @PostMapping("/franchise/save")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value= "프랜차이즈 정보 저장", notes="프랜차이즈 정보를 저장한다.")
    public ResponseEntity<FranchiseResponseDto> saveFranchise(@RequestBody FranchiseDto franchiseDto){
        FranchiseResponseDto savedDto = restaurantService.saveFranchise(franchiseDto);
        return ResponseEntity.status(HttpStatus.OK).body(savedDto);
    }
    @DeleteMapping("/franchise/delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value= "프랜차이즈 삭제", notes="프랜차이즈 정보를 삭제한다.")
    public ResponseEntity<String> deleteFranchise(Long id){
        restaurantService.deleteFranchise(id);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 되었습니다.");
    }

    @GetMapping("/restaurant/list/{start}")
    @ApiOperation(value= "음식점 리스트", notes="음식점 리스트를 반환한다. start = 페이지 번호")
    public ResponseEntity<List<RestaurantResponseDto> > getRestaurantList(@PathVariable int start){
        List<RestaurantResponseDto> responseDtoList = restaurantService.getAll(start, 10);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }
    @GetMapping("/restaurant/list")
    @ApiOperation(value= "음식점 리스트", notes="음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantList(){
        List<RestaurantResponseDto> responseDtoList = restaurantService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }
    @GetMapping("/restaurant/category/{cate_id}")
    @ApiOperation(value= "카테고리 id를 통한 음식점 검색", notes="cate_id에 해당하는 카테고리에 포함된 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategory(@PathVariable("cate_id") int cate_id){

        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategory(cate_id);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/restaurant/category/{cate_id}/name-order")
    @ApiOperation(value="카테고리 id를 통한 음식점 검색", notes="cate_id에 해당하는 카테고리에 포함된 음식점 리스트를 반환한다. 이름순")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryOrderByName(
            @PathVariable("cate_id") int cate_id){
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategoryOrderByName(cate_id, false);
        return ResponseEntity.ok(responseDtoList);
    }
    @GetMapping("/restaurant/category/{cate_id}/name-order/reverse")
    @ApiOperation(value="카테고리 id를 통한 음식점 검색", notes="cate_id에 해당하는 카테고리에 포함된 음식점 리스트를 반환한다. 이름 역순")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryOrderByNameRev(
            @PathVariable("cate_id") int cate_id){
        return ResponseEntity.ok(restaurantService.findByCategoryOrderByName(cate_id, true));
    }
    @GetMapping("/restaurant/category/{cate_id}/score-order")
    @ApiOperation(value="카테고리 id를 통한 음식점 검색", notes="cate_id에 해당하는 카테고리에 포함된 음식점 리스트를 반환한다. 점수순")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryOrderByScore(
            @PathVariable int cate_id){
        return ResponseEntity.ok(restaurantService.findByCategoryOrderByScore(cate_id,false));
    }
    @GetMapping("/restaurant/category/{cate_id}/score-order/reverse")
    @ApiOperation(value="카테고리 id를 통한 음식점 검색", notes="cate_id에 해당하는 카테고리에 포함된 음식점 리스트를 반환한다. 점수 역순")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryOrderByScoreReverse(
            @PathVariable int cate_id){
        return ResponseEntity.ok(restaurantService.findByCategoryOrderByScore(cate_id,true));
    }
    @GetMapping("/restaurant/category/n/{cate_name}")
    @ApiOperation(value= "카테고리 이름을 통한 음식점 검색", notes="cate_name을 포함하는 카테고리에 속한 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryName(@PathVariable("cate_name") String name) throws UnsupportedEncodingException {
        LOGGER.info("[searchByCategoryName] cate_name = {}", name);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategory(name);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/restaurant/category/n/{cate_name}/name-order")
    @ApiOperation(value= "카테고리 이름을 통한 음식점 검색", notes="cate_name을 포함하는 카테고리에 속한 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryNameOrderByName(@PathVariable("cate_name") String name) throws UnsupportedEncodingException {
        LOGGER.info("[searchByCategoryNameOrderByName] cate_name = {}", name);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategoryOrderByName(name, false);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/restaurant/category/n/{cate_name}/name-order/reverse")
    @ApiOperation(value= "카테고리 이름을 통한 음식점 검색", notes="cate_name을 포함하는 카테고리에 속한 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryNameOrderByNameRev(@PathVariable("cate_name") String name) throws UnsupportedEncodingException {
        LOGGER.info("[searchByCategoryNameOrderByNameRev] cate_name = {}", name);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategoryOrderByName(name, true);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/restaurant/category/n/{cate_name}/score-order")
    @ApiOperation(value= "카테고리 이름을 통한 음식점 검색", notes="cate_name을 포함하는 카테고리에 속한 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryNameOrderByScore(@PathVariable("cate_name") String name) throws UnsupportedEncodingException {
        LOGGER.info("[searchByCategoryNameOrderByScore] cate_name = {}", name);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategoryOrderByScore(name, false);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/restaurant/category/n/{cate_name}/score-order/reverse")
    @ApiOperation(value= "카테고리 이름을 통한 음식점 검색", notes="cate_name을 포함하는 카테고리에 속한 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryNameOrderByScoreRev(@PathVariable("cate_name") String name) throws UnsupportedEncodingException {
        LOGGER.info("[searchByCategoryName] cate_name = {}", name);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategoryOrderByScore(name,true);
        return ResponseEntity.ok().body(responseDtoList);
    }

    @GetMapping("/search/restaurant/{name}")
    @ApiOperation(value= "이름을 통한 음식점 검색", notes="name을 포함한 이름을 가진 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByRestaurantName(@PathVariable("name") String rname) throws UnsupportedEncodingException {
        LOGGER.info("[searchByRestaurantName] rname = {}",rname);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByRestaurantName(rname);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/search/restaurant/{name}/name-order")
    @ApiOperation(value= "이름을 통한 음식점 검색", notes="name을 포함한 이름을 가진 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByRestaurantNameOrderByName(@PathVariable("name") String rname) throws UnsupportedEncodingException {
        LOGGER.info("[searchByRestaurantName] rname = {}",rname);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByRestaurantNameOrderByName(rname, false);
        return ResponseEntity.ok().body(responseDtoList);
    }@GetMapping("/search/restaurant/{name}/name-order/reverse")
    @ApiOperation(value= "이름을 통한 음식점 검색", notes="name을 포함한 이름을 가진 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByRestaurantNameOrderByNameRev(@PathVariable("name") String rname) throws UnsupportedEncodingException {
        LOGGER.info("[searchByRestaurantName] rname = {}",rname);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByRestaurantNameOrderByName(rname,true);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/search/restaurant/name/{name}/score-order")
    @ApiOperation(value= "이름을 통한 음식점 검색", notes="name을 포함한 이름을 가진 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByRestaurantNameOrderByScore(@PathVariable("name") String rname) throws UnsupportedEncodingException {
        LOGGER.info("[searchByRestaurantName] rname = {}",rname);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByRestaurantNameOrderByScore(rname,false);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/search/restaurant/name/{name}/score-order/reverse")
    @ApiOperation(value= "이름을 통한 음식점 검색", notes="name을 포함한 이름을 가진 음식점 리스트를 반환한다.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByRestaurantNameOrerByScoreRev(@PathVariable("name") String rname) throws UnsupportedEncodingException {
        LOGGER.info("[searchByRestaurantName] rname = {}",rname);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByRestaurantNameOrderByScore(rname, true);
        return ResponseEntity.ok().body(responseDtoList);
    }

    @GetMapping("/search/restaurant-menu/name/{name}")
    @ApiOperation(value= "메뉴 이름을 통한 음식점 검색", notes="name라는 메뉴를 가진 음식점 리스트를 반환한다. 메뉴이름에 포함되어 있으면 해당됨.")
    public ResponseEntity<List<RestaurantResponseDto>> searchByMenuName(@PathVariable("name") String mname) throws UnsupportedEncodingException {
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByMenuName(mname);
        return ResponseEntity.ok().body(responseDtoList);
    }
}
