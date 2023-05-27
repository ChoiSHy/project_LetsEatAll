package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Menu;
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
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    RestaurantRepository restaurantRepository;
    FranchiseRepository franchiseRepository;
    MenuRepository menuRepository;

    @Autowired
    /* 생성자 */
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 FranchiseRepository franchiseRepository,
                                 MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.franchiseRepository = franchiseRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    /* 음식점 정보 저장 */
    public RestaurantResponseDto saveRestaurant( RestaurantDto restaurantDto) {
        Restaurant restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .addr(restaurantDto.getAddr())
                .score(0)
                .category(restaurantDto.getCategory())
                .fid(restaurantDto.getFid())
                .build();

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return RestaurantResponseDto.builder()
                .name(savedRestaurant.getName())
                .addr(savedRestaurant.getAddr())
                .category(savedRestaurant.getCategory())
                .score(savedRestaurant.getScore())
                .fid(savedRestaurant.getFid())
                .build();
    }

    @Override
    /* 음식점 정보 요청 */
    public RestaurantResponseDto getRestaurant(Long id) {
        Restaurant found = restaurantRepository.findById(id).get();

        RestaurantResponseDto rrd = RestaurantResponseDto.builder()
                .name(found.getName())
                .addr(found.getAddr())
                .category(found.getCategory())
                .fid(found.getFid())
                .build();

        for (Menu menu : found.getMenus()){
            rrd.add(menu);
        }
        return rrd;
    }

    @Override
    /* 프랜차이즈 정보 저장 */
    public FranchiseResponseDto saveFranchise(FranchiseDto franchiseDto) {
        return null;
    }

    @Override
    /* 프랜차이즈 정보 요청 */
    public FranchiseResponseDto getFranchise(Long id) {
        return null;
    }

    @Override
    /* 음식점 정보 삭제 */
    public void deleteRestaurant(Long id) {

    }

    @Override
    /* 프랜차이즈 정보 삭제 */
    public void deleteFranchise(Long id) {

    }
}
