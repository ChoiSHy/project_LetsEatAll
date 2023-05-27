package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.*;
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
    ReviewRepository reviewRepository;
    YoutubeRepository youtubeRepository;
    UserRepository UserRepository;

    @Autowired
    public MenuServiceImpl(
            RestaurantRepository restaurantRepository
            , MenuRepository menuRepository,
            ReviewRepository reviewRepository,
            YoutubeRepository youtubeRepository,
            UserRepository UserRepository)
    {
        this.restaurantRepository=restaurantRepository;
        this.menuRepository = menuRepository;
        this.reviewRepository = reviewRepository;
        this.youtubeRepository = youtubeRepository;
        this.UserRepository = UserRepository;
    }

    @Override
    /* 메뉴 정보 저장 */
    public MenuResponseDto saveMenu(MenuDto menuDto) {
        Restaurant foundRest = restaurantRepository.findById(menuDto.getRid()).get();
        Menu menu = Menu.builder()
                .name(menuDto.getName())
                .price(menuDto.getPrice())
                .category(menuDto.getCategory())
                .score(0)
                .restaurant(foundRest)
                .build();
        Menu savedMenu = menuRepository.save(menu);

        MenuResponseDto menuResponseDto = MenuResponseDto.builder()
                .rid(savedMenu.getRestaurant().getId())
                .name(savedMenu.getName())
                .price(savedMenu.getPrice())
                .score(savedMenu.getScore())
                .category(savedMenu.getCategory())
                .Yurl(null)
                .Ysum(null)
                .build();
        return menuResponseDto;
    }

    /* 리뷰 정보 불러오기 */
    private List<ReviewElement> getReviewElements(Long id) {
        List<Review> reviews = reviewRepository.findAllByMid(id);
        List<ReviewElement> reviewElements = new ArrayList<>();
        for (Review review : reviews) {
            User User = UserRepository.findById(review.getUid()).get();

            ReviewElement element = ReviewElement.builder()
                    .id(review.getId())
                    .title(review.getTitle())
                    .content(review.getContent())
                    .img(review.getPid())
                    .score(review.getScore())
                    .count(review.getRecCnt())
                    .uid(review.getUid())
                    .writer(User.getName())
                    .build();
            reviewElements.add(element);
        }
        return reviewElements;
    }

    @Override
    public MenuResponseDto getMenu(Long id) {
        Menu foundMenu = menuRepository.findById(id).get();
        if (foundMenu == null)
            return null;

        List<ReviewElement> elements = getReviewElements(foundMenu.getId());
        Youtube youtube = youtubeRepository.findByMid(foundMenu.getId());

        MenuResponseDto menuResponseDto = MenuResponseDto.builder()
                .name(foundMenu.getName())
                .price(foundMenu.getPrice())
                .category(foundMenu.getCategory())
                .Yurl(youtube.getUrl())
                .Ysum(youtube.getContent())
                .build();
        return menuResponseDto;
    }

    @Override
    public boolean changeMenuPrice(Long id, int price) {
        Menu foundMenu = menuRepository.findById(id).get();
        foundMenu.setPrice(price);
        Menu chagedMenu = menuRepository.save(foundMenu);

        if (chagedMenu.getPrice() != price)
            return false;
        return true;
    }

    @Override
    public MenuElement getMenuElement(Long id) {
        return null;
    }

    @Override
    public void deleteMenu(Long id) {

    }
}
