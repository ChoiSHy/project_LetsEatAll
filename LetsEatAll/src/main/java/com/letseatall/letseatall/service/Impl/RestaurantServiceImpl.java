package com.letseatall.letseatall.service.Impl;

import com.amazonaws.services.kms.model.NotFoundException;
import com.letseatall.letseatall.data.Entity.*;
import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.Entity.menu.MenuImageFile;
import com.letseatall.letseatall.data.dto.Menu.MenuListDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseDto;
import com.letseatall.letseatall.data.dto.Restaurant.FranchiseResponseDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantDto;
import com.letseatall.letseatall.data.dto.Restaurant.RestaurantResponseDto;
import com.letseatall.letseatall.data.repository.*;
import com.letseatall.letseatall.data.repository.Menu.MenuImageFileRepository;
import com.letseatall.letseatall.data.repository.Menu.MenuRepository;
import com.letseatall.letseatall.data.repository.review.ImagefileRepository;
import com.letseatall.letseatall.data.repository.review.ReviewRepository;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    final RestaurantRepository restaurantRepository;
    final FranchiseRepository franchiseRepository;
    final CategoryRepository categoryRepository;
    final MenuRepository menuRepository;
    final ReviewRepository reviewRepository;
    final MenuImageFileRepository imageFileRepository;
    final ImagefileRepository imgRepository;
    final MenuService menuService;
    private final Logger LOGGER = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    @Autowired
    /* 생성자 */
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository,
                                 FranchiseRepository franchiseRepository,
                                 CategoryRepository categoryRepository,
                                 MenuRepository menuRepository,
                                 ReviewRepository reviewRepository,
                                 MenuImageFileRepository imageRepository,
                                 ImagefileRepository imgRepository,
                                 MenuService menuService) {
        this.restaurantRepository = restaurantRepository;
        this.franchiseRepository = franchiseRepository;
        this.categoryRepository = categoryRepository;
        this.menuRepository = menuRepository;
        this.reviewRepository = reviewRepository;
        this.imageFileRepository = imageRepository;
        this.imgRepository = imgRepository;
        this.menuService = menuService;
    }

    @Override
    /* 음식점 정보 저장 */
    public RestaurantResponseDto saveRestaurant(RestaurantDto restaurantDto) {
        LOGGER.info("[saveRestaurant] : request = {}", restaurantDto);
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

        RestaurantResponseDto responseDto = makeDto(savedRestaurant);
        return responseDto;
    }

    private void addFranchiseMenus(Franchise franchise, Restaurant restaurant) {
        List<Menu> fMenus = franchise.getMenus();
        List<Menu> saveMenus = new ArrayList<>();
        LOGGER.info("[addFranchiseMenus] 시작");
        for (Menu fMenu : fMenus) {
            Menu newMenu = new Menu(fMenu.getName(), fMenu.getPrice(), fMenu.getScore(), fMenu.getCategory());
            newMenu.setRestaurant(restaurant);
            newMenu.setInfo(fMenu.getInfo());
            if (fMenu.getUrl() != null) {
                newMenu.setUrl(fMenu.getUrl());
                LOGGER.info("[addFranchiseMenus] url 처리");
            }
            if (fMenu.getImg() != null) {
                MenuImageFile fimg = imageFileRepository.findByMenuId(fMenu.getId()).orElseThrow(() -> new NotFoundException("대상 찾지 못함"));
                MenuImageFile img = new MenuImageFile();
                img.setUrl(fimg.getUrl());
                img.setStoredName(fimg.getStoredName());
                newMenu.setImg(img);
                LOGGER.info("[addFranchiseMenus] 이미지 처리");

            }
            saveMenus.add(newMenu);
        }
        restaurantRepository.save(restaurant);
    }

    @Override
    /* 음식점 정보 요청 */
    public RestaurantResponseDto getRestaurant(Long id) {
        Optional<Restaurant> foundOptional = restaurantRepository.findById(id);
        RestaurantResponseDto rrd = null;
        if (foundOptional.isPresent()) {
            Restaurant found = foundOptional.get();
            LOGGER.info("[getRestaurant] 식당 검색 결과: {}", found.getName());
            rrd = makeDto(found);
            List<MenuListDto> menuList = new ArrayList<>();
            LOGGER.info("[getRestaurant] 식당 검색 메뉴 정보 검색");

            menuRepository.findAllByRestaurantId(id).forEach(menu -> {
                if (menu != null) {
                    MenuListDto mrd = MenuListDto.builder()
                            .menu_id(menu.getId())
                            .menu_name(menu.getName())
                            .menu_price(menu.getPrice())
                            .menu_category(menu.getCategory().getName())
                            .build();
                    if (menu.getScore() != 0)
                        mrd.setMenu_score(menu.getScore() / menu.getReviewList().size());
                    else mrd.setMenu_score(0);
                    if (menu.getImg() != null) {
                        mrd.setImg_url(menu.getImg().getUrl());
                    }
                    menuList.add(mrd);
                }
            });
            rrd.setMenuDtoList(menuList);
            LOGGER.info("[getRestaurant] 메뉴 정보 주입 완료");

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
        if (savedFranchise.getCategory() != null) {
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

        if (!mids.isEmpty()) {
            mids.forEach(mid -> menuService.deleteMenu(mid));
        }
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
            RestaurantResponseDto responseDto = makeDto(r);
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
    public List<RestaurantResponseDto> searchName(String keyword, int start) throws UnsupportedEncodingException {
        String name = URLEncoder.encode(keyword, "utf8");
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        List<Restaurant> restaurantList = restaurantRepository.findAllByNameContainingIgnoreCase(name, PageRequest.of(start, 10)).getContent();
        for (Restaurant restaurant : restaurantList) {
            RestaurantResponseDto dto = makeDto(restaurant);
            responseDtoList.add(dto);
            System.out.println(restaurant);
        }

        return responseDtoList;
    }

    public List<RestaurantResponseDto> findByCategory(int cate_id) {
        LOGGER.info("[findByCategory] : 카테고리에 해당하는 음식점 리스트 불러오기 시작");
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        restaurantRepository.findAllByCategoryId(cate_id).forEach(restaurant -> {
            RestaurantResponseDto rrd = makeDto(restaurant);
            responseDtoList.add(rrd);
        });
        LOGGER.info("[findByCategory] : 카테고리에 해당하는 음식점 리스트 불러오기 완료");
        return responseDtoList;
    }

    private RestaurantResponseDto makeDto(Restaurant restaurant) {
        LOGGER.info("[makeDto] DTO 생성 시작: {}", restaurant.getName());
        RestaurantResponseDto dto = RestaurantResponseDto.builder()
                .restaurant_id(restaurant.getId())
                .restaurant_name(restaurant.getName())
                .restaurant_addr(restaurant.getAddr())
                .restaurant_score(restaurant.getScore())
                .build();
        if (restaurant.getCategory() != null)
            dto.setRestaurant_category(restaurant.getCategory().getName());
        if (restaurant.getFranchise() != null)
            dto.setFranchise(restaurant.getFranchise().getName());
        dto.setMenuDtoList(new ArrayList<>());
        LOGGER.info("[makeDto] DTO 생성 완료");
        return dto;
    }

    @Override
    public List<RestaurantResponseDto> findByCategory(String cate_name) throws UnsupportedEncodingException {
        String decode_name = URLDecoder.decode(cate_name, "UTF-8");
        LOGGER.info("[findByCategory] 찾을 카테고리: {}", decode_name);
        Optional<Category> option = categoryRepository.findByNameLike("%" + decode_name + "%");
        Category category = option.orElse(null);
        if (category != null) {
            LOGGER.info("[findByCategory] 카테고리 발견: id={}, name={}", category.getId(), category.getName());
            List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
            restaurantRepository.findAllByCategoryId(category.getId()).forEach(restaurant -> {
                RestaurantResponseDto rrd = makeDto(restaurant);
                responseDtoList.add(rrd);
            });
            LOGGER.info("[findByCategory] : 카테고리에 해당하는 음식점 리스트 불러오기 완료");
            return responseDtoList;
        } else {
            LOGGER.info("[findByCategory] 카테고리 찾을 수 없음...: {}", decode_name);
            return null;
        }
    }

    @Override
    public List<RestaurantResponseDto> findByRestaurantName(String enc_name) throws UnsupportedEncodingException {
        String dec_name = URLDecoder.decode(enc_name, "UTF-8");
        LOGGER.info("[findByRestaurantName] 찾을 음식점: {}", dec_name);
        List<Restaurant> restaurants = restaurantRepository.findAllByNameLike("%" + dec_name + "%");
        LOGGER.info("[findByRestaurantName] 찾은 대상 수: {}", restaurants.size());
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        restaurants.forEach(restaurant -> {
            responseDtoList.add(makeDto(restaurant));
        });
        LOGGER.info("[findByRestaurantName] 리스트 완성");
        return responseDtoList;
    }

    @Override
    public List<RestaurantResponseDto> findByMenuName(String menuName) throws UnsupportedEncodingException {
        String dec_menuName = URLDecoder.decode(menuName, "UTF-8");
        LOGGER.info("[findByMenuName] 찾을 음식 이름: {}", dec_menuName);
        List<Menu> menus = menuRepository.findAllByNameLike("%" + dec_menuName + "%");
        LOGGER.info("[findByMenuName] 찾은 대상 수: {}", menus.size());

        Map<Long, RestaurantResponseDto> map = new HashMap<>();
        LOGGER.info("[findByMenuName] 음식점 리스트 채우기");
        for (Menu menu : menus) {
            LOGGER.info("[findByMenuName] 찾은 메뉴: {}", menu.getName());
            Restaurant restaurant = menu.getRestaurant();
            if (restaurant != null)
                if (!map.containsKey(restaurant.getId()))
                    map.put(restaurant.getId(), makeDto(restaurant));

        }
        LOGGER.info("[findByMenuName] map에 dto 채우기 완료");

        return new ArrayList<>(map.values());
    }

    @Override
    public List<RestaurantResponseDto> findByCategoryOrderByName(int category, boolean reverse) {
        List<Restaurant> restaurants = reverse ?
                restaurantRepository.findAllByCategoryIdOrderByNameDesc(category) :
                restaurantRepository.findAllByCategoryIdOrderByNameAsc(category);
        List<RestaurantResponseDto> responseDtoList = makeRRDList(restaurants);
        return responseDtoList;
    }

    @Override
    public List<RestaurantResponseDto> findByCategoryOrderByScore(int category, boolean reverse) {
        List<Restaurant> restaurants = reverse ?
                restaurantRepository.findAllByCategoryIdOrderByScoreDesc(category):
                restaurantRepository.findAllByCategoryIdOrderByScoreAsc(category);
        return makeRRDList(restaurants);
    }

    private List<RestaurantResponseDto> makeRRDList(List<Restaurant> restaurants){
        List<RestaurantResponseDto> rrds=new ArrayList<>();
        restaurants.forEach(restaurant -> {
            RestaurantResponseDto rrd= makeDto(restaurant);
            rrds.add(rrd);
        });
        return rrds;
    }

    @Override
    public List<RestaurantResponseDto> findByCategoryOrderByName(String categoryName, boolean reverse) throws UnsupportedEncodingException {
        String decode_name = URLDecoder.decode(categoryName, "UTF-8");
        LOGGER.info("[findByCategoryOrderByName] 찾을 카테고리: {}", decode_name);
        Optional<Category> option = categoryRepository.findByNameLike("%" + decode_name + "%");
        Category category = option.orElse(null);
        if (category != null) {
            LOGGER.info("[findByCategoryOrderByName] 카테고리 발견: id={}, name={}", category.getId(), category.getName());
            List<RestaurantResponseDto> responseDtoList = makeRRDList(
                    reverse ?
                            restaurantRepository.findAllByCategoryIdOrderByNameDesc(category.getId()):
                            restaurantRepository.findAllByCategoryIdOrderByNameAsc(category.getId())
            );
            LOGGER.info("[findByCategoryOrderByName] : 카테고리에 해당하는 음식점 리스트 불러오기 완료");
            return responseDtoList;
        } else {
            LOGGER.info("[findByCategoryOrderByName] 카테고리 찾을 수 없음...: {}", decode_name);
            return null;
        }
    }

    @Override
    public List<RestaurantResponseDto> findByCategoryOrderByScore(String categoryName, boolean reverse) throws UnsupportedEncodingException {
        String decode_name = URLDecoder.decode(categoryName, "UTF-8");
        LOGGER.info("[findByCategoryOrderByScore] 찾을 카테고리: {}", decode_name);
        Optional<Category> option = categoryRepository.findByNameLike("%" + decode_name + "%");
        Category category = option.orElse(null);
        if (category != null) {
            LOGGER.info("[findByCategoryOrderByScore] 카테고리 발견: id={}, name={}", category.getId(), category.getName());
            List<RestaurantResponseDto> responseDtoList = makeRRDList(
                    reverse ?
                            restaurantRepository.findAllByCategoryIdOrderByScoreDesc(category.getId()):
                            restaurantRepository.findAllByCategoryIdOrderByScoreAsc(category.getId())
            );
            LOGGER.info("[findByCategoryOrderByScore] : 카테고리에 해당하는 음식점 리스트 불러오기 완료");
            return responseDtoList;
        } else {
            LOGGER.info("[findByCategoryOrderByScore] 카테고리 찾을 수 없음...: {}", decode_name);
            return null;
        }
    }

    @Override
    public List<RestaurantResponseDto> findByRestaurantNameOrderByName(String name, boolean reverse) throws UnsupportedEncodingException {
        String dec_name = URLDecoder.decode(name, "UTF-8");
        LOGGER.info("[findByRestaurantNameOrderByName] 찾을 음식점: {}", dec_name);
        List<Restaurant> restaurants = reverse ?
                restaurantRepository.findAllByNameLikeOrderByNameDesc("%" + dec_name + "%"):
                restaurantRepository.findAllByNameLikeOrderByNameAsc("%"+dec_name+"%");
        LOGGER.info("[findByRestaurantNameOrderByName] 찾은 대상 수: {}", restaurants.size());
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        restaurants.forEach(restaurant -> {
            responseDtoList.add(makeDto(restaurant));
        });
        LOGGER.info("[findByRestaurantNameOrderByName] 리스트 완성");
        return responseDtoList;
    }

    @Override
    public List<RestaurantResponseDto> findByRestaurantNameOrderByScore(String name, boolean reverse) throws UnsupportedEncodingException {
        String dec_name = URLDecoder.decode(name, "UTF-8");
        LOGGER.info("[findByRestaurantNameOrderByScore] 찾을 음식점: {}", dec_name);
        List<Restaurant> restaurants = reverse ?
                restaurantRepository.findAllByNameLikeOrderByScoreDesc("%" + dec_name + "%"):
                restaurantRepository.findAllByNameLikeOrderByScoreAsc("%"+dec_name+"%");
        LOGGER.info("[findByRestaurantNameOrderByScore] 찾은 대상 수: {}", restaurants.size());
        List<RestaurantResponseDto> responseDtoList = new ArrayList<>();
        restaurants.forEach(restaurant -> {
            responseDtoList.add(makeDto(restaurant));
        });
        LOGGER.info("[findByRestaurantNameOrderByScore] 리스트 완성");
        return responseDtoList;
    }



    public void sumScore() {
    }
}
