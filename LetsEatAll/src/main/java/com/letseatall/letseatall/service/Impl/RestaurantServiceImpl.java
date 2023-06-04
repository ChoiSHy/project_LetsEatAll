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

        Franchise franchise = Franchise.builder()
                .name(franchiseDto.getName())
                .category(franchiseDto.getCategory())
                .build();

        Franchise savedFranchise = franchiseRepository.save(franchise);

        FranchiseResponseDto franchiseResponseDto = FranchiseResponseDto.builder()
                .name(savedFranchise.getName())
                .category(savedFranchise.getCategory())
                .build();

        return franchiseResponseDto;
    }

    @Override
    /* 프랜차이즈 정보 요청 */
    public FranchiseResponseDto getFranchise(Long id) {

        Franchise foundFranchise = franchiseRepository.findById(id).get();
        if(foundFranchise == null)
            return null;

        FranchiseResponseDto franchiseResponseDto = FranchiseResponseDto.builder()
                .name(foundFranchise.getName())
                .category(foundFranchise.getCategory())
                .build();

        return franchiseResponseDto;
    }

    @Override
    /* 음식점 정보 삭제 */
    public void deleteRestaurant(Long id) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(id);
        if (optionalRestaurant.isPresent()) {
            restaurantRepository.deleteById(id);
            System.out.println("음식점" + id + "삭제 완료");
        } else {
            System.out.println("음식점" + id + "를 찾을 수 없음");
            throw new RuntimeException("음식점" + id + "를 찾을 수 없음");
        }
    }

    @Override
    /* 프랜차이즈 정보 삭제 */
    public void deleteFranchise(Long id) {
        Optional<Franchise> optionalFranchise = franchiseRepository.findById(id);
        if (optionalFranchise.isPresent()) {
            franchiseRepository.deleteById(id);
            System.out.println("프랜차이즈" + id + "삭제 완료");
        } else {
            System.out.println("프랜차이즈" + id + "를 찾을 수 없음");
            throw new RuntimeException("프랜차이즈" + id + "를 찾을 수 없음");
        }
    }
}
