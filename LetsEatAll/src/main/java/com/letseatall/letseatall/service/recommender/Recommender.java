package com.letseatall.letseatall.service.recommender;


import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.dto.Menu.MenuListDto;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.dto.User.BadRequestException;
import com.letseatall.letseatall.data.repository.Menu.MenuRepository;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.data.repository.review.ReviewRepository;
import com.letseatall.letseatall.service.MenuService;
import com.letseatall.letseatall.service.awsS3.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class Recommender {
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final MenuService menuService;
    private final ReviewRepository reviewRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(Recommender.class);

    public List<MenuListDto> run(long user_id) throws IOException {
        LOGGER.info("[추천 시스템] 시작");
        HashMap<Long, Double> similarities = new HashMap<>();
        HashMap<Long, Set<Long>> menuMap = new HashMap<>();
        User targetUser = userRepository.getById(user_id);
        if (targetUser == null) {
            throw new BadRequestException("사용자 찾을 수 없음");
        }
        Set<Long> target_menuSet = get_menuSet(targetUser);
        LOGGER.info("[추천 시스템] 불러온 사용자 = {}", targetUser);
        List<User> userList = userRepository.findAll();
        LOGGER.info("[추천 시스템] 모든 사용자 불러오기 완료");

        LOGGER.info("[Recommender] 유사도 계산");
        if(target_menuSet.size()==0){
            LOGGER.info("[Recommender] 사용자의 이용 내역이 없어 비교 불가. 9점 이상의 메뉴 전달");
            Page<Menu> menus = menuRepository.findByScoreIsGreaterThan(9, PageRequest.of(0,10));
            List<MenuListDto> retList = new ArrayList<>();

            menus.forEach(menu->{
                    retList.add(menuService.makeListDto(menu));
            });
            return retList;
        }

        for (User user : userList) {
            if (user.equals(targetUser)) continue;
            Set<Long> menuSet = get_menuSet(user);
            double similarity = get_similarity(target_menuSet, menuSet);
            LOGGER.info("[추천 시스템] user: {}, similar: {}", user.getId(), similarity);

            similarities.put(user.getId(), similarity);
            menuMap.put(user.getId(), menuSet);
        }
        LOGGER.info("[추천 시스템] 유사도 계산 완료");

        List<Map.Entry<Long, Double>> entryList = new LinkedList<>(similarities.entrySet());
        entryList.sort((o1, o2) ->
                (int) (o2.getValue() - o1.getValue())
        );
        LOGGER.info("[추천 시스템] 유사도 순으로 정렬 완료");
        System.out.println(entryList);
        List<MenuListDto> retList = new ArrayList<>();

        int uidx=0;
        while (retList.size() < 10 && uidx < entryList.size()){
            for( long menu_id : new ArrayList<>(menuMap.get(entryList.get(uidx++).getKey()))){
                if (target_menuSet.contains(menu_id))
                    continue;
                Menu menu = menuRepository.findById(menu_id).orElse(null);
                retList.add(menuService.makeListDto(menu));
            }

        }
        LOGGER.info("[추천 시스템] 전달 객체 리스트 생성 완료");
        LOGGER.info("[추천 시스템] 추천 메뉴 선정 완료");

        return retList;
    }

    private Set<Long> get_menuSet(User user) {
        Set<Long> menuSet = new LinkedHashSet<>();
        for (Review review : user.getReviewList()) {
            Menu menu = review.getMenu();
            int score = review.getScore();
            if (score >= 5) {
                long menu_id = menu.getId();
                menuSet.add(menu_id);
            }
        }
        return menuSet;
    }

    private double get_similarity(Set<Long> A, Set<Long> B) {
        Set<Long> union = new HashSet<>(A);
        Set<Long> inter = new HashSet<>(A);

        union.addAll(B);
        inter.retainAll(B);

        return (double) inter.size() / (double) union.size();
    }
}
