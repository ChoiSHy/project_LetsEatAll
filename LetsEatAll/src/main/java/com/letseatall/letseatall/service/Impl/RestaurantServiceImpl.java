package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.*;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final Logger LOGGER = LoggerFactory.getLogger(RestaurantServiceImpl.class);

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
        LOGGER.info("[saveRestaurant] : request = {}",restaurantDto);
        Optional<Franchise> franchise = franchiseRepository.findById(restaurantDto.getFid());
        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDto.getName());
        restaurant.setAddr(restaurantDto.getAddr());
        restaurant.setScore(0);

        if (franchise.isPresent()) {
            LOGGER.info("[saveRestaurant] : franchise 불러오기");
            Franchise f = franchise.get();
            restaurant.setFranchise(f);
            LOGGER.info("[saveRestaurant] : franchise 불러오기 완료 : {}", f.getName());
            restaurant.setCategory(f.getCategory());

            LOGGER.info("[saveRestaurant] : franchise 메뉴 불러오기");
            addFranchiseMenus(f, restaurant);
            LOGGER.info("[saveRestaurant] : franchise 메뉴 불러오기 완료");
        } else {
            Optional<Category> category = categoryRepository.findById(restaurantDto.getCategory());
            if (category.isPresent())
                restaurant.setCategory(category.get());
        }
        LOGGER.info("[saveRestaurant] : 결과 = {}", restaurant);

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
        LOGGER.info("[saveFranchise] : 프렌차이즈 저장 시작");
        Optional<Category> category = categoryRepository.findById(franchiseDto.getCategory());

        Franchise franchise = new Franchise();
        franchise.setName(franchiseDto.getName());
        if (category.isPresent()) {
            franchise.setCategory(category.get());
        }
        LOGGER.info("[saveFranchise] : 저장할 프렌차이즈 = {}", franchise);
        Franchise savedFranchise = franchiseRepository.save(franchise);

        FranchiseResponseDto frd = FranchiseResponseDto.builder()
                .id(savedFranchise.getId())
                .name(savedFranchise.getName())
                .build();
        if (savedFranchise.getCategory() != null){
            frd.setCategory(savedFranchise.getCategory().getName());
            LOGGER.info("[saveFranchise] : 저장 완료. 반환 데이터 = {}", frd);
        }
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
        if (!rvids.isEmpty())
            reviewRepository.deleteAllByIdInBatch(rvids);
        if (!mids.isEmpty())
            menuRepository.deleteAllByIdInBatch(mids);
        restaurantRepository.deleteById(id);
    }

    @Override
    /* 프랜차이즈 정보 삭제 */
    public void deleteFranchise(Long id) {
        franchiseRepository.deleteById(id);
    }

    @Override
    public List<RestaurantResponseDto> getAll() {
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        restaurantRepository.findAll().forEach(r -> {
            RestaurantResponseDto responseDto = makeDto(r);
            responseDtoList.add(responseDto);
        });
        return null;
    }

    public List<RestaurantResponseDto> getAll(int start, int size) {
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        restaurantRepository.findAll(PageRequest.of(start, size)).forEach(r -> {
            RestaurantResponseDto responseDto = RestaurantResponseDto.builder()
                    .id(r.getId())
                    .name(r.getName())
                    .addr(r.getAddr())
                    .score(r.getScore())
                    .franchise(r.getFranchise().getName())
                    .build();
            if (r.getCategory() != null)
                responseDto.setCategory(r.getCategory().getName());
            responseDtoList.add(responseDto);
        });
        return responseDtoList;
    }

    public List<RestaurantResponseDto> findAllInCategory(int category, int start) {
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        List<Restaurant> rlist = restaurantRepository.findAllByCategoryId(category, PageRequest.of(start, 10)).getContent();
        for (Restaurant r : rlist) {
            System.out.println(r);
            RestaurantResponseDto responseDto = makeDto(r);
            responseDtoList.add(responseDto);
        }
        responseDtoList.forEach(r -> System.out.println(r));
        return responseDtoList;
    }

    @Override
    public List<RestaurantResponseDto> searchName(String name, int start) {
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        List<Restaurant> restaurantList = restaurantRepository.findAllByNameContainingIgnoreCase(name, PageRequest.of(start, 10)).getContent();
        for (Restaurant restaurant : restaurantList) {
            RestaurantResponseDto dto = makeDto(restaurant);
            responseDtoList.add(dto);
            System.out.println(restaurant);
        }

        return responseDtoList;
    }
    private RestaurantResponseDto makeDto(Restaurant restaurant){
        RestaurantResponseDto dto = RestaurantResponseDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .addr(restaurant.getAddr())
                .score(restaurant.getScore())
                .build();
        if(restaurant.getCategory()!=null)
            dto.setCategory(restaurant.getCategory().getName());
        if(restaurant.getFranchise()!=null)
            dto.setFranchise(restaurant.getFranchise().getName());

        return dto;
    }
}
