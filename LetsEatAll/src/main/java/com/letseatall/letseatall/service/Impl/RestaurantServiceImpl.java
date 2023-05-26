package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Restaurant;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.repository.FranchiseRepository;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.RestaurantRepository;
import com.letseatall.letseatall.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    RestaurantRepository restaurantRepository;
    FranchiseRepository franchiseRepository;
    MenuRepository menuRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 FranchiseRepository franchiseRepository,
                                 MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.franchiseRepository = franchiseRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public RestaurantResponseDto saveRestaurant(RestaurantDto restaurantDto) {
        Restaurant restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .addr(restaurantDto.getAddr())
                .category(restaurantDto.getCategory())
                .fid(restaurantDto.getFid())
                .build();

        Restaurant savedRestaurant;
        return null;
    }

    @Override
    public RestaurantResponseDto getResponse(Long id) {
        return null;
    }

    @Override
    public FranchiseResponseDto saveFranchise(FranchiseDto franchiseDto) {
        return null;
    }

    @Override
    public FranchiseResponseDto getFranchise(Long id) {
        return null;
    }

    @Override
    public void deleteRestaurant(Long id) {

    }

    @Override
    public void deleteFranchise(Long id) {

    }
}
