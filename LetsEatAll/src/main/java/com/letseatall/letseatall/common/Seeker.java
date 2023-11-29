package com.letseatall.letseatall.common;

import com.letseatall.letseatall.data.Entity.Review.Review;
import com.letseatall.letseatall.data.dto.User.BadRequestException;
import com.letseatall.letseatall.data.dto.common.SeekerRequestDto;
import com.letseatall.letseatall.data.dto.common.SeekerResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;

@Component
public class Seeker {
    private final static Logger LOGGER = LoggerFactory.getLogger(Seeker.class);
    @Value("${seeker.address}")
    private String url = "";


    public boolean predict(Review review) {
        RestTemplate restTemplate = new RestTemplate();
        SeekerRequestDto requestDto = new SeekerRequestDto(review.getContent());
        LOGGER.info("[Seeker - predict] start");
        SeekerResponseDto responseDto;
        try {
            LOGGER.info("[Seeker - predict] send to {}",url);
            responseDto = restTemplate.postForObject(
                    url,
                    requestDto,
                    SeekerResponseDto.class
            );

        }catch (RestClientException e){
            throw e;
        }
        LOGGER.info("[Seeker - predict] predict = {}", responseDto.isSuccess());
        return responseDto.isSuccess();
    }
}
