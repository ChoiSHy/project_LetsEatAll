package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.*;
import com.letseatall.letseatall.data.dto.IntChangeDto;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final Logger LOGGER = LoggerFactory.getLogger(MenuServiceImpl.class);

    @Autowired
    public MenuServiceImpl(RestaurantRepository restaurantRepository,
                           MenuRepository menuRepository,
                           YoutubeRepository youtubeRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           FranchiseRepository franchiseRepository,
                           ReviewRepository reviewRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.youtubeRepository = youtubeRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.franchiseRepository = franchiseRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    /* 메뉴 정보 저장 */
    public MenuResponseDto saveMenu(MenuDto menuDto) {
        LOGGER.info("[saveMenu] : saveMenu 시작 menu = {}", menuDto);
        Restaurant foundRest = restaurantRepository.findById(menuDto.getRid()).get();
        LOGGER.info("[saveMenu] : 검색된 음식점 = {}", foundRest.getName());
        Category category = categoryRepository.findById(menuDto.getCategory()).get();
        LOGGER.info("[saveMenu] : 카테고리 = {}", category.getName());
        Franchise franchise = null;
        if (foundRest.getFranchise() != null) {
            franchise = foundRest.getFranchise();
            LOGGER.info("[saveMenu] : 프렌차이즈 = {}", franchise.getName());
        }
        LOGGER.info("[saveMenu] : 데이터 주입 시작");
        Menu menu = new Menu();
        menu.setName(menuDto.getName());
        menu.setPrice(menuDto.getPrice());
        menu.setScore(0);
        menu.setRestaurant(foundRest);
        menu.setCategory(category);
        if (franchise!= null)
            menu.setFranchise(franchise);
        Menu savedMenu = menuRepository.save(menu);
        LOGGER.info("[saveMenu] : 데이터 DB 저장 성공 -> savedMenu = {}", savedMenu.getName());

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
        LOGGER.info("[getMenu] : id = {}, 가져오기", id);
        Menu foundMenu = menuRepository.findById(id).get();
        LOGGER.info("[getMenu] : menu = {}", foundMenu);


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
        Optional<Menu> foundMenu = menuRepository.findById(changeDto.getId());
        Menu fMenu = null;
        if (foundMenu.isPresent()) {
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
        List<Long> ids = new ArrayList<>();
        Optional<Menu> oMenu = menuRepository.findById(id);
        if (oMenu.isPresent()) {
            oMenu.get().getReviewList().forEach(review -> {
                ids.add(review.getId());
            });
        }
        reviewRepository.deleteAllByIdInBatch(ids);
        menuRepository.deleteById(id);
    }

    @Override
    public List<MenuResponseDto> getAllMenu(Long rid) {
        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        menuRepository.findAllByRestaurantId(rid).forEach(m -> {
            MenuResponseDto responseDto = MenuResponseDto.builder()
                    .rid(m.getRestaurant().getId())
                    .name(m.getName())
                    .price(m.getPrice())
                    .score(m.getScore())
                    .build();
            if (m.getCategory() != null)
                responseDto.setCategory(m.getCategory().getName());
            responseDtoList.add(responseDto);
        });
        return responseDtoList;
    }

    public List<MenuResponseDto> getAllMenu(int start, int size) {
        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        menuRepository.findAll(PageRequest.of(start, size)).forEach(m -> {
            MenuResponseDto responseDto = MenuResponseDto.builder()
                    .rid(m.getRestaurant().getId())
                    .name(m.getName())
                    .price(m.getPrice())
                    .score(m.getScore())
                    .build();
            if (m.getCategory() != null)
                responseDto.setCategory(m.getCategory().getName());
            responseDtoList.add(responseDto);
        });
        return responseDtoList;
    }
}
