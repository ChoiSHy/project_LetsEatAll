package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.*;
import com.letseatall.letseatall.data.dto.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuElement;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewElement;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {
    RestaurantRepository restaurantRepository;
    MenuRepository menuRepository;
    YoutubeRepository youtubeRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    FranchiseRepository franchiseRepository;
    ReviewRepository reviewRepository;
    @Autowired
    public MenuServiceImpl(RestaurantRepository restaurantRepository,
                           MenuRepository menuRepository,
                           YoutubeRepository youtubeRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           FranchiseRepository franchiseRepository,
                           ReviewRepository reviewRepository)
    {
        this.restaurantRepository=restaurantRepository;
        this.menuRepository = menuRepository;
        this.youtubeRepository = youtubeRepository;
        this.userRepository = userRepository;
        this.categoryRepository=categoryRepository;
        this.franchiseRepository=franchiseRepository;
        this.reviewRepository=reviewRepository;
    }

    @Override
    /* 메뉴 정보 저장 */
    public MenuResponseDto saveMenu(MenuDto menuDto) {
        Restaurant foundRest = restaurantRepository.findById(menuDto.getRid()).get();
        Category category = categoryRepository.findById(menuDto.getCategory()).get();
        Franchise franchise=null;
        if (foundRest.getFranchise()!= null)
            franchise = franchiseRepository.findById( foundRest.getFranchise().getId() ).get();
        Menu menu = Menu.builder()
                .name(menuDto.getName())
                .price(menuDto.getPrice())
                .score(0)
                .restaurant(foundRest)
                .category(category)
                .franchise(franchise)
                .build();
        Menu savedMenu = menuRepository.save(menu);

        MenuResponseDto menuResponseDto = MenuResponseDto.builder()
                .rid(savedMenu.getRestaurant().getId())
                .name(savedMenu.getName())
                .price(savedMenu.getPrice())
                .score(savedMenu.getScore())
                .category(savedMenu.getCategory().getName())
                .build();
        return menuResponseDto;
    }
    @Override
    /* 메뉴 정보 요청 */
    public MenuResponseDto getMenu(Long id) {
        Menu foundMenu = menuRepository.findById(id).get();

        MenuResponseDto responseDto = MenuResponseDto.builder()
                .rid(foundMenu.getRestaurant().getId())
                .name(foundMenu.getName())
                .price(foundMenu.getPrice())
                .score(foundMenu.getScore())
                .category(foundMenu.getCategory().getName())
                .build();
        return responseDto;
    }

    @Override
    public boolean changeMenuPrice(IntChangeDto changeDto) {
        Optional<Menu> foundMenu= menuRepository.findById(changeDto.getId());
        Menu fMenu = null;
        if (foundMenu.isPresent()){
            fMenu = foundMenu.get();
            fMenu.setPrice(changeDto.getValue());
        }
        Menu chagedMenu = menuRepository.save(fMenu);

        if (chagedMenu.getPrice() != changeDto.getValue())
            return false;
        return true;
    }

    @Override
    public void deleteMenu(Long id) {
        List<Long> reviewList = new ArrayList<>();
        reviewRepository.findAllByMenu(id).forEach(r -> reviewList.add(r.getId()));
        reviewRepository.deleteAllByIdInBatch(reviewList);
        menuRepository.deleteById(id);
    }

    @Override
    public List<MenuResponseDto> getAllMenu(Long rid) {
        List<Menu> menuList = menuRepository.findAllByRestaurantId(rid);
        List<MenuResponseDto> responseDtoList=new ArrayList<>();

        for(Menu menu : menuList){
            MenuResponseDto responseDto = MenuResponseDto.builder()
                    .rid(rid)
                    .name(menu.getName())
                    .price(menu.getPrice())
                    .score(menu.getScore())
                    .build();
            Category category = menu.getCategory();
            if(category != null)
                responseDto.setCategory(category.getName());
            responseDtoList.add(responseDto);
        }
        return responseDtoList;
    }
}
