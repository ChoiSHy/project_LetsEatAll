package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.*;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    RestaurantRepository restaurantRepository;
    FranchiseRepository franchiseRepository;
    CategoryRepository categoryRepository;
    MenuRepository menuRepository;
    ReviewRepository reviewRepository;

    @Autowired
    /* 생성자 */
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 FranchiseRepository franchiseRepository,
                                 CategoryRepository categoryRepository,
                                 MenuRepository menuRepository,
                                 ReviewRepository reviewRepository) {
        this.restaurantRepository = restaurantRepository;
        this.franchiseRepository = franchiseRepository;
        this.categoryRepository = categoryRepository;
        this.menuRepository = menuRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    /* 음식점 정보 저장 */
    public RestaurantResponseDto saveRestaurant(RestaurantDto restaurantDto) {
        Optional<Franchise> franchise = franchiseRepository.findById(restaurantDto.getFid());
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDto.getName());
        restaurant.setAddr(restaurantDto.getAddr());
        restaurant.setScore(0);

        if (franchise.isPresent()) {
            Franchise f = franchise.get();
            restaurant.setFranchise(f);
            restaurant.setCategory(f.getCategory());
            addFranchiseMenus(f, restaurant);
        } else {
            Optional<Category> category = categoryRepository.findById(restaurantDto.getCategory());
            if (category.isPresent())
                restaurant.setCategory(category.get());
        }

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        RestaurantResponseDto responseDto = RestaurantResponseDto.builder()
                .id(savedRestaurant.getId())
                .name(savedRestaurant.getName())
                .addr(savedRestaurant.getAddr())
                .score(savedRestaurant.getScore())
                .build();
        if (savedRestaurant.getCategory() != null)
            responseDto.setCategory(savedRestaurant.getCategory().getName());
        if (savedRestaurant.getFranchise() != null) {
            responseDto.setFranchise(savedRestaurant.getFranchise().getName());
        }
        return responseDto;
    }

    private void addFranchiseMenus(Franchise franchise, Restaurant restaurant) {
        List<Menu> fMenus = franchise.getMenus();
        List<Menu> saveMenus = new ArrayList<>();
        for (Menu fMenu : fMenus) {
            Menu newMenu = new Menu(fMenu.getName(), fMenu.getPrice(), fMenu.getScore(), fMenu.getCategory());
            newMenu.setRestaurant(restaurant);
            saveMenus.add(newMenu);
        }
        restaurantRepository.save(restaurant);
    }

    @Override
    /* 음식점 정보 요청 */
    public RestaurantResponseDto getRestaurant(Long id) {
        Optional<Restaurant> foundOptional = restaurantRepository.findById(id);
        if (foundOptional.isPresent()) {
            Restaurant found = foundOptional.get();
            RestaurantResponseDto rrd = RestaurantResponseDto.builder()
                    .id(found.getId())
                    .name(found.getName())
                    .addr(found.getAddr())
                    .build();
            if (found.getCategory() != null)
                rrd.setCategory(found.getCategory().getName());
            if (found.getFranchise() != null)
                rrd.setFranchise(found.getFranchise().getName());

            return rrd;
        }
        return null;
    }

    @Override
    /* 프랜차이즈 정보 요청 */
    public FranchiseResponseDto getFranchise(Long id) {
        Optional<Franchise> foundOptional = franchiseRepository.findById(id);
        if (foundOptional.isPresent()) {
            Franchise found = foundOptional.get();
            System.out.println(found);
            FranchiseResponseDto responseDto = FranchiseResponseDto.builder()
                    .id(found.getId())
                    .name(found.getName())
                    .build();
            if (found.getCategory() != null)
                responseDto.setCategory(found.getCategory().getName());
            System.out.println(responseDto);
            return responseDto;
        }
        return null;
    }

    @Override
    /* 프랜차이즈 정보 저장 */
    public FranchiseResponseDto saveFranchise(FranchiseDto franchiseDto) {
        Optional<Category> category = categoryRepository.findById(franchiseDto.getCategory());
        Franchise franchise = Franchise.builder()
                .name(franchiseDto.getName())
                .build();
        if (category.isPresent())
            franchise.setCategory(category.get());

        Franchise savedFranchise = franchiseRepository.save(franchise);

        FranchiseResponseDto frd = FranchiseResponseDto.builder()
                .id(savedFranchise.getId())
                .name(savedFranchise.getName())
                .build();
        if (savedFranchise.getCategory() != null)
            frd.setCategory(savedFranchise.getCategory().getName());
        return frd;
    }


    @Override
    /* 음식점 정보 삭제 */
    public void deleteRestaurant(Long id) {
        List<Long> rvids = new ArrayList<>();
        List<Long> mids = new ArrayList<>();
        Optional<Restaurant> oRestaurant = restaurantRepository.findById(id);

        if (oRestaurant.isPresent()) {
            Restaurant restaurant = oRestaurant.get();
            for (Menu menu : restaurant.getMenus()) {
                for (Review review : menu.getReviewList())
                    rvids.add(review.getId());
                mids.add(menu.getId());
            }
        }
        if(!rvids.isEmpty())
            reviewRepository.deleteAllByIdInBatch(rvids);
        if(!mids.isEmpty())
            menuRepository.deleteAllByIdInBatch(mids);
        restaurantRepository.deleteById(id);
    }

    @Override
    /* 프랜차이즈 정보 삭제 */
    public void deleteFranchise(Long id) {
        franchiseRepository.deleteById(id);
    }
}
