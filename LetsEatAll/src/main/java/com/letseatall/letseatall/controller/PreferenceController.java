package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.repository.UserRepository;
import com.letseatall.letseatall.service.Impl.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user/prefer")
@RequiredArgsConstructor
public class PreferenceController {
    private final Logger LOGGER = LoggerFactory.getLogger(PreferenceController.class);
    private final PreferenceService preferenceService;
    @GetMapping("/{user_id}")
    public ResponseEntity<Map<String, Integer>> getPreference(@PathVariable long user_id){
        LOGGER.info("[getPreference] 사용자의 선호도 정보 가져오기");
        Map map = preferenceService.getPreferenceOfUser(user_id);
        LOGGER.info("[getPreference] 사용자의 선호도 정보 불러오기 완료");
        return ResponseEntity.ok(map);
    }

    @GetMapping("/{user_id}/top-3")
    public ResponseEntity<String[] > getTop3(@PathVariable long user_id){
        LOGGER.info("[getTop3] 상위 3개의 분야 가져오기");
        String[] res = preferenceService.getTop3OfUser(user_id);
        LOGGER.info("[getTop3] 정보 불러오기 완료");
        return ResponseEntity.ok(res);
    }
}
