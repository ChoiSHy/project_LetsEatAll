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
        LOGGER.info("[saveMenu] : 데이터 주입 시작");
        Menu menu = new Menu();
        menu.setName(menuDto.getName());
        menu.setPrice(menuDto.getPrice());
        menu.setScore(0);
        menu.setRestaurant(foundRest);
        menu.setCategory(category);
        Menu savedMenu = menuRepository.save(menu);
        LOGGER.info("[saveMenu] : 데이터 DB 저장 성공 -> savedMenu = {}", savedMenu.getName());

        MenuResponseDto menuResponseDto = makeDto(savedMenu);
        return menuResponseDto;
    }

    @Override
    public MenuResponseDto saveFranchiseMenu(MenuDto menuDto) {
        Long fid = menuDto.getRid();
        String name = menuDto.getName();
        int price = menuDto.getPrice();
        int cid = menuDto.getCategory();
        LOGGER.info("[saveFranchiseMenu] : 데이터 추출 완료 : fid: {}, name: {}, price: {}, category: {}", fid,name,price,cid);

        Category category = null;
        Franchise franchise = null;
        Menu newMenu = new Menu();
        newMenu.setName(name);
        newMenu.setPrice(price);
        newMenu.setScore(0);

        Optional<Category> opCtg = categoryRepository.findById(cid);
        if (opCtg.isPresent()){
            category = opCtg.get();
            LOGGER.info("[saveFranchiseMenu] : category 불러오기: {}", category);
        }
        Optional<Franchise> opFrc = franchiseRepository.findById(fid);
        if(opFrc.isPresent()){
            franchise = opFrc.get();
            LOGGER.info("[saveFranchiseMenu] : franchise 불러오기: {}", franchise);
        }
        else return null;
        newMenu.setCategory(category);
        newMenu.setFranchise(franchise);

        Menu savedMenu = menuRepository.save(newMenu);
        MenuResponseDto retDto = makeDto(savedMenu);
        return retDto;
    }

    @Override
    /* 메뉴 정보 요청 */
    public MenuResponseDto getMenu(Long id) {
        LOGGER.info("[getMenu] : id = {}, 가져오기", id);
        Menu foundMenu = menuRepository.findById(id).get();
        LOGGER.info("[getMenu] : menu = {}", foundMenu);

        MenuResponseDto responseDto = makeDto(foundMenu);
        if(foundMenu.getRestaurant() != null) responseDto.setRid(foundMenu.getRestaurant().getId());
        else responseDto.setRid(foundMenu.getFranchise().getId());
        return responseDto;
    }

    @Override
    public boolean changeMenuPrice(IntChangeDto changeDto) {
        LOGGER.info("[changeMenuPrice] : 가격 수정 시작");
        Optional<Menu> foundMenu = menuRepository.findById(changeDto.getId());
        Menu fMenu = null;
        if (foundMenu.isPresent()) {
            LOGGER.info("[changeMenuPrice] : 메뉴 불러오기 성공");
            fMenu = foundMenu.get();
            fMenu.setPrice(changeDto.getValue());
            LOGGER.info("[changeMenuPrice] : 메뉴 수정 시작");
        }
        Menu chagedMenu = menuRepository.save(fMenu);
        
        if (chagedMenu.getPrice() != changeDto.getValue()){
            LOGGER.info("[changeMenuPrice] : 메뉴 불러오기 실패");
            return false;}
        LOGGER.info("[changeMenuPrice] : 메뉴 수정 성공 = {}", chagedMenu);
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
            MenuResponseDto responseDto = makeDto(m);
            responseDtoList.add(responseDto);
        });
        return responseDtoList;
    }

    public List<MenuResponseDto> getAllMenu(int start, int size) {
        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        menuRepository.findAll(PageRequest.of(start, size)).forEach(m -> {
            MenuResponseDto responseDto = makeDto(m);
            responseDtoList.add(responseDto);
        });
        return responseDtoList;
    }

    public List<MenuResponseDto> getListFranchiseMenu(Long fid) {
        List<MenuResponseDto> resDtoList = new ArrayList<>();
        LOGGER.info("[getListFranchiseMenu] : 탐색 시작");
        menuRepository.findAllByFranchiseId(fid).forEach(m->{
            resDtoList.add(makeDto(m));
        });
        LOGGER.info("[getListFranchiseMenu] : 탐색 종료");
        return resDtoList;
    }

    @Override
    public List<MenuResponseDto> getAllFranchiseMenu(Long fid) {
        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        menuRepository.findAllByRestaurant_FranchiseId(fid).forEach(m ->{
            responseDtoList.add(makeDto(m));
        });
        return responseDtoList;
    }
    private MenuResponseDto makeDto(Menu menu){
        LOGGER.info("[makeDto] DTO 생성 시작 : {}", menu);
        MenuResponseDto mrd = MenuResponseDto.builder()
                .rid(menu.getRestaurant().getId())
                .r_name(menu.getRestaurant().getName())
                .price(menu.getPrice())
                .category(menu.getCategory().getName())
                .name(menu.getName())
                .score(0)
                .build();
        if(menu.getReviewList().size()!= 0)
            mrd.setScore(menu.getScore() / menu.getReviewList().size());

        LOGGER.info("[makeDto] DTO 생성 완료");
        return mrd;
    }

    public void sum(){
        List<Menu> mlist= new ArrayList<>();
        menuRepository.findAll().forEach(menu -> {
            menu.sumScore();
            mlist.add(menu);
        });
        menuRepository.saveAllAndFlush(mlist);
    }
}
