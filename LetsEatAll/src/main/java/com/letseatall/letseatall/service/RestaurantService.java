package com.letseatall.letseatall.service;

import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface RestaurantService {
    RestaurantResponseDto saveRestaurant(RestaurantDto restaurantDto);
    RestaurantResponseDto getRestaurant(Long id);

    FranchiseResponseDto saveFranchise(FranchiseDto franchiseDto);
    FranchiseResponseDto getFranchise(Long id);
    void deleteRestaurant(Long id);
    void deleteFranchise(Long id);

    List<RestaurantResponseDto> findByCategory(int cate_id);
    List<RestaurantResponseDto> findByCategory(String cate_name) throws UnsupportedEncodingException;
    List<RestaurantResponseDto> findByRestaurantName(String name) throws UnsupportedEncodingException;
    List<RestaurantResponseDto> findByMenuName(String menuName) throws UnsupportedEncodingException;
    public List<RestaurantResponseDto> getAll(int start, int size);
    public List<RestaurantResponseDto> getAll();
    public List<RestaurantResponseDto> findAllInCategory(int category, int start);
    public List<RestaurantResponseDto> searchName(String name, int start);

    void sumScore();
}
