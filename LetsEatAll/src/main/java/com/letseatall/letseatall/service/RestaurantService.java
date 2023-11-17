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
    List<RestaurantResponseDto> getAll(int start, int size);
    List<RestaurantResponseDto> getAll();
    List<RestaurantResponseDto> findAllInCategory(int category, int start);
    List<RestaurantResponseDto> searchName(String name, int start) throws UnsupportedEncodingException;

    List<RestaurantResponseDto> findByCategoryOrderByName(int category, boolean reverse);
    List<RestaurantResponseDto> findByCategoryOrderByScore(int category, boolean reverse);
    List<RestaurantResponseDto> findByCategoryOrderByName(String category, boolean reverse) throws UnsupportedEncodingException;
    List<RestaurantResponseDto> findByCategoryOrderByScore(String category, boolean reverse) throws UnsupportedEncodingException;

    List<RestaurantResponseDto> findByRestaurantNameOrderByName(String name, boolean reverse) throws UnsupportedEncodingException;
    List<RestaurantResponseDto> findByRestaurantNameOrderByScore(String name, boolean reverse) throws UnsupportedEncodingException;
    void sumScore();
}
