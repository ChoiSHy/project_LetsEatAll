package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;
    @Autowired
    public RestaurantController(RestaurantService restaurantService){
        this.restaurantService=restaurantService;}

    @GetMapping("/restaurant")
    public ResponseEntity<RestaurantResponseDto> getRestaurant(Long id){
        RestaurantResponseDto responseDto = restaurantService.getRestaurant(id);
        if(responseDto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @PostMapping("/restaurant/add")
    public ResponseEntity<RestaurantResponseDto> saveRestaurant(@RequestBody RestaurantDto restaurantDto){
        RestaurantResponseDto responseDto = restaurantService.saveRestaurant(restaurantDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
