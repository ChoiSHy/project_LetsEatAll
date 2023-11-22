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

import java.util.List;

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
}
