package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RestaurantResponseDto> getRestaurant(Long id){
        RestaurantResponseDto responseDto = restaurantService.getRestaurant(id);
        if(responseDto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @GetMapping("/franchise")
    public ResponseEntity<FranchiseResponseDto> getFranchise(Long id){
        FranchiseResponseDto responseDto = restaurantService.getFranchise(id);
        if(responseDto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @PostMapping("/restaurant/save")
    public ResponseEntity<RestaurantResponseDto> saveRestaurant(@RequestBody RestaurantDto restaurantDto){
        RestaurantResponseDto responseDto = restaurantService.saveRestaurant(restaurantDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @DeleteMapping("/restaurant/remove")
    public ResponseEntity<String> deleteRestaurant(Long id){
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료되었습니다.");
    }


    @PostMapping("/franchise/save")
    public ResponseEntity<FranchiseResponseDto> saveFranchise(@RequestBody FranchiseDto franchiseDto){
        FranchiseResponseDto savedDto = restaurantService.saveFranchise(franchiseDto);
        return ResponseEntity.status(HttpStatus.OK).body(savedDto);
    }
    @DeleteMapping("/franchise/delete")
    public ResponseEntity<String> deleteFranchise(Long id){
        restaurantService.deleteFranchise(id);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 되었습니다.");
    }

    @GetMapping("/restaurant/list/{start}")
    public ResponseEntity<List<RestaurantResponseDto> > getRestaurantList(@PathVariable int start){
        List<RestaurantResponseDto> responseDtoList = restaurantService.getAll(start, 10);
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }
    @GetMapping("/restaurant/list")
    public ResponseEntity<List<RestaurantResponseDto>> getRestaurantList(){
        List<RestaurantResponseDto> responseDtoList = restaurantService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }
    @GetMapping("/restaurant/category/{cate_id}")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategory(@PathVariable("cate_id") int cate_id){

        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategory(cate_id);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/restaurant/category/n/{cate_name}")
    public ResponseEntity<List<RestaurantResponseDto>> searchByCategoryName(@PathVariable("cate_name") String name) throws UnsupportedEncodingException {
        LOGGER.info("[searchByCategoryName] cate_name = {}", name);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByCategory(name);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/search/restaurant/{name}")
    public ResponseEntity<List<RestaurantResponseDto>> searchByRestaurantName(@PathVariable("name") String rname) throws UnsupportedEncodingException {
        LOGGER.info("[searchByRestaurantName] rname = {}",rname);
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByRestaurantName(rname);
        return ResponseEntity.ok().body(responseDtoList);
    }
    @GetMapping("/search/restaurant/menu/{name}")
    public ResponseEntity<List<RestaurantResponseDto>> searchByMenuName(@PathVariable("name") String mname) throws UnsupportedEncodingException {
        List<RestaurantResponseDto> responseDtoList = restaurantService.findByMenuName(mname);
        return ResponseEntity.ok().body(responseDtoList);
    }
}
