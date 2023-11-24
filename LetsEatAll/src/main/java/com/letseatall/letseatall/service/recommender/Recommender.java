package com.letseatall.letseatall.service.recommender;


import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.Entity.menu.Menu;
import com.letseatall.letseatall.data.dto.Menu.MenuResponseDto;
import com.letseatall.letseatall.data.repository.Menu.MenuRepository;
import com.letseatall.letseatall.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Recommender {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenuRepository menuRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(Recommender.class);

    public List<MenuResponseDto> run(long user_id){
        LOGGER.info("[추천 시스템] 시작");
        HashMap<Long, Double> similarities= new HashMap<>();
        HashMap<Long, Set<Long>> menuMap = new HashMap<>();

        User targetUser = userRepository.getById(user_id);
        Set<Long> target_menuSet = get_menuSet(targetUser);
        LOGGER.info("[추천 시스템] 불러온 사용자 = {}", targetUser);

        List<User> userList = userRepository.findAll();
        LOGGER.info("[추천 시스템] 모든 사용자 불러오기 완료");

        for (User user : userList){
            Set<Long> menuSet = get_menuSet(user);
            double similarity = get_similarity(target_menuSet, menuSet);

            similarities.put(user_id, similarity);
            menuMap.put(user_id, menuSet);
        }
        LOGGER.info("[추천 시스템] 유사도 계산 완료");

        List<Map.Entry<Long, Double>> entryList = new LinkedList<>(similarities.entrySet());
        entryList.sort((o1, o2) ->
            (int) (o2.getValue()-o1.getValue())
        );
        LOGGER.info("[추천 시스템] 유사도 순으로 정렬 완료");
        
        List<MenuResponseDto> retList=new ArrayList<>();
        for (int i =0; i< 3 && retList.size() < 10; i++){
            for (long menu_id : menuMap.get(entryList.get(0))){
                Menu menu = menuRepository.findById(menu_id).orElse(null);
                if(menu!= null) {
                    MenuResponseDto menuResponseDto = MenuResponseDto.builder()
                            .rid(menu.getRestaurant().getId())
                            .r_name(menu.getRestaurant().getName())
                            .name(menu.getName())
                            .price(menu.getPrice())
                            .category(menu.getCategory().getName())
                            .score(menu.getScore())
                            .url(menu.getUrl())
                            .img_url(menu.getImg().getUrl())
                            .info(menu.getInfo())
                            .build();
                    retList.add(menuResponseDto);
                }

            }

        }
        LOGGER.info("[추천 시스템] 전달 객체 리스트 생성 완료");
        LOGGER.info("[추천 시스템] 추천 메뉴 선정 완료");
        
        return retList;
    }
    private Set<Long> get_menuSet(User user){
        Set<Long> menuSet = new LinkedHashSet<>();

        for (Review review : user.getReviewList()){
            int score = review.getScore();

            if(score >= 5 ){
                long menu_id = review.getMenu().getId();
                menuSet.add(menu_id);
            }
        }
        return menuSet;
    }

    private double get_similarity(Set<Long> A, Set<Long> B){
        Set<Long> union = new HashSet<>(A);
        Set<Long> inter = new HashSet<>(A);

        union.addAll(B);
        inter.retainAll(B);

        return (double) inter.size() / (double) union.size();
    }
}
