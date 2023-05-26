package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Review;
import com.letseatall.letseatall.data.Entity.Youtube;
import com.letseatall.letseatall.data.dto.Menu.MenuDto;
import com.letseatall.letseatall.data.dto.Menu.MenuElement;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.dto.Review.ReviewElement;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.ReviewRepository;
import com.letseatall.letseatall.data.repository.YoutubeRepository;
import com.letseatall.letseatall.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {
    MenuRepository menuRepository;
    ReviewRepository reviewRepository;
    YoutubeRepository youtubeRepository;
    UserRepository UserRepository;

    @Autowired
    public MenuServiceImpl(MenuRepository menuRepository,
                           ReviewRepository reviewRepository,
                           YoutubeRepository youtubeRepository,
                           UserRepository UserRepository) {
        this.menuRepository = menuRepository;
        this.reviewRepository = reviewRepository;
        this.youtubeRepository = youtubeRepository;
        this.UserRepository = UserRepository;
    }

    @Override
    public MenuResponseDto saveMenu(MenuDto menuDto) {
        Menu menu = Menu.builder()
                .name(menuDto.getName())
                .price(menuDto.getPrice())
                .category(menuDto.getCategory())
                .build();
        Menu savedMenu = menuRepository.save(menu);
        MenuResponseDto menuResponseDto = MenuResponseDto.builder()
                .rid(savedMenu.getRid())
                .name(savedMenu.getName())
                .price(savedMenu.getPrice())
                .category(savedMenu.getCategory())
                .reviews(null)
                .Yurl(null)
                .Ysum(null)
                .build();
        return menuResponseDto;
    }
    private List<ReviewElement> getReviewElements(Long id){
        List<Review> reviews = reviewRepository.findAllByMid(id);
        List<ReviewElement> reviewElements = new ArrayList<>();
        for (Review review : reviews){
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

        List<ReviewElement> elements= getReviewElements(foundMenu.getId());
        Youtube youtube = youtubeRepository.findByMid(foundMenu.getId());

        MenuResponseDto menuResponseDto = MenuResponseDto.builder()
                .rid(foundMenu.getRid())
                .name(foundMenu.getName())
                .price(foundMenu.getPrice())
                .category(foundMenu.getCategory())
                .reviews(elements)
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
