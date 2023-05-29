package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.Entity.Franchise;
import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Restaurant;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.repository.CategoryRepository;
import com.letseatall.letseatall.data.repository.FranchiseRepository;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.RestaurantRepository;
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

    @Autowired
    /* 생성자 */
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 FranchiseRepository franchiseRepository,
                                 CategoryRepository categoryRepository,
                                 MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.franchiseRepository = franchiseRepository;
        this.categoryRepository = categoryRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    /* 음식점 정보 저장 */
    public RestaurantResponseDto saveRestaurant(RestaurantDto restaurantDto) {
        Optional<Franchise> franchise = franchiseRepository.findById(restaurantDto.getFid());
        Restaurant restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .addr(restaurantDto.getAddr())
                .score(0)
                .build();
        if (franchise.isPresent()) {
            Franchise f = franchise.get();
            restaurant.setFranchise(f);
            restaurant.setCategory(f.getCategory());
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
            addFranchiseMenus(savedRestaurant.getFranchise().getId(), savedRestaurant.getId());
        }
        return responseDto;
    }

    private void addFranchiseMenus(Long id, Long rid) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(rid);
        List<Menu> foundMenus = menuRepository.findAllByFranchiseId(id);
        List<Menu> saveMenus = new ArrayList<>();
        if (restaurant.isPresent())
            for (Menu fMenu : foundMenus) {
                System.out.println(fMenu);
                Menu newMenu = Menu.builder()
                        .name(fMenu.getName())
                        .price(fMenu.getPrice())
                        .category(fMenu.getCategory())
                        .score(0)
                        .restaurant(restaurant.get())
                        .franchise(fMenu.getFranchise())
                        .build();
                saveMenus.add(newMenu);
            }
        menuRepository.saveAll(saveMenus);
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
        List<Menu> menus = menuRepository.findAllByRestaurantId(id);
        List<Long> ids = new ArrayList<>();
        System.out.println("menu delete start");

        for (Menu menu : menus)
            ids.add(menu.getId());

        menuRepository.deleteAllByIdInBatch(ids);
        System.out.println("menu delete end");
        restaurantRepository.deleteById(id);

    }

    @Override
    /* 프랜차이즈 정보 삭제 */
    public void deleteFranchise(Long id) {
        List<Long> rids = restaurantRepository.findIdAllByFranchiseId(id);
        List<Long> mids = menuRepository.findIdAllByFranchise_Id(id);

        menuRepository.deleteAllByIdInBatch(mids);
        restaurantRepository.deleteAllByIdInBatch(rids);
        franchiseRepository.deleteById(id);
    }
}
