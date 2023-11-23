package com.letseatall.letseatall.service.Impl;

import com.amazonaws.services.kms.model.NotFoundException;
import com.letseatall.letseatall.data.Entity.Category;
import com.letseatall.letseatall.data.Entity.Preference;
import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.Entity.User;
import com.letseatall.letseatall.data.repository.CategoryRepository;
import com.letseatall.letseatall.data.repository.PreferenceRepository;
import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.data.repository.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PreferenceService {
    private final Logger LOGGER = LoggerFactory.getLogger(PreferenceService.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PreferenceRepository preferenceRepository;
    public void recordUserPrefer(int score, int categoryId, long userId){
        LOGGER.info("[recordUserPrefer] 사용자의 선호도 테이블 수정");
        Preference pref = preferenceRepository.findByUserIdAndCategoryId(userId, categoryId)
                .orElse(null);
        LOGGER.info("[recordUserPrefer] pref = {}", pref);
        if(pref != null){
            pref.setScore(pref.getScore()+score);
            preferenceRepository.save(pref);
            LOGGER.info("[recordUserPrefer] 저장 완료");
        }
        else{
            LOGGER.info("[recordUserPrefer] pref == null -> 새로운 테이블 생성");
            User user = userRepository.findById(userId).orElseThrow();
            Category category = categoryRepository.findById(categoryId).orElseThrow();
            LOGGER.info("[recordUserPrefer] private key 요소 불러오기 성공");
            pref = new Preference();
            pref.setUserId(user.getId());
            pref.setCategoryId(category.getId());
            pref.setUser(user);
            pref.setCategory(category);
            pref.setScore(pref.getScore() + score);
            LOGGER.info("[recordUserPrefer] pref 저장 시도");
            preferenceRepository.save(pref);
            LOGGER.info("[recordUserPrefer] pref 저장 완료");
        }
    }

    public boolean isExistTable(long userId, int categoryId){
        return preferenceRepository.existsByUserIdAndCategoryId(userId, categoryId);
    }

    public void create_prefer_tables(String uid){
        try {
            User user1 = userRepository.getByUid(uid);
            LOGGER.info("[create-prefer-tables] 회원의 선호도 테이블 생성중...");
            List<Category> plist = categoryRepository.findAll();
            System.out.println(plist);
            for (Category category : categoryRepository.findAll()) {
                LOGGER.info("[create-prefer-tables] category = {}", category);
                Preference prefer = new Preference();
                LOGGER.info("[create-prefer-tables] prefer 생성");
                prefer.setUserId(user1.getId());
                prefer.setCategoryId(category.getId());
                LOGGER.info("id 주입 완료");
                prefer.setUser(user1);
                LOGGER.info("객체 주입 완료");
                LOGGER.info("[create-prefer-tables] Prefer = {}", prefer.toString());
                preferenceRepository.save(prefer);
            }
        } catch (RuntimeException e) {
            LOGGER.info("[signUp] 테이블 저장실패");
            throw e;
        }
        LOGGER.info("[signUp] 테이블 저장완료");
    }

    public Map<String, Integer> getPreferenceOfUser(long user_id){
        LOGGER.info("[getPreferenceOfUser]");
        Map<String, Integer> map = new HashMap<>();

        categoryRepository.findAll().forEach( c -> {
            map.put(c.getName(), 0);
        });
        LOGGER.info("[getPreferenceOfUser] map 초기화");
        
        preferenceRepository.findAllByUserId(user_id).forEach(preference -> {
            map.put(preference.getCategory().getName(), preference.getScore());
        });
        LOGGER.info("[getPreferenceOfUser] map 데이터 주입 완료");
        
        return map;
    }

    public String[] getTop3OfUser(long user_id){
        LOGGER.info("[getTop3OfUser]");
        Map<String, Integer> map = new HashMap<>();

        categoryRepository.findAll().forEach( c -> {
            map.put(c.getName(), 0);
        });
        LOGGER.info("[getPreferenceOfUser] map 초기화");

        preferenceRepository.findAllByUserIdOrderByScore(user_id).forEach(preference -> {
            map.put(preference.getCategory().getName(), preference.getScore());
        });
        LOGGER.info("[getPreferenceOfUser] map 데이터 주입 완료");

        List<Map.Entry<String, Integer>>entries = new LinkedList<>(map.entrySet());
        entries.sort(Map.Entry.comparingByValue((o1, o2) -> o2-o1));
        String[] res= new String[3];
        for (int i = 0; i< 3; i++){
            res[i]=entries.get(i).getKey();
        }
        return res;
    }
}
