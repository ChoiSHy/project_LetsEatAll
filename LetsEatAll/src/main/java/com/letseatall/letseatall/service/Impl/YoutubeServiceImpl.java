package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Youtube;
import com.letseatall.letseatall.data.dto.Youtube.YoutubeDto;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.YoutubeRepository;
import com.letseatall.letseatall.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YoutubeServiceImpl implements YoutubeService {
    private YoutubeRepository youtubeRepository;
    private MenuRepository menuRepository;

    @Autowired
    public YoutubeServiceImpl(YoutubeRepository youtubeRepository,
                              MenuRepository menuRepository){
        this.youtubeRepository=youtubeRepository;
        this.menuRepository=menuRepository;
    }

    /* 아직 적용 x */
    public YoutubeDto save(String url){
        Youtube newVideo = Youtube.builder()
                .url(url)
                .build();
        Youtube savedVideo = youtubeRepository.save(newVideo);
        YoutubeDto responseDto = YoutubeDto.builder()
                .url(savedVideo.getUrl())
                .build();
        return responseDto;
    }

}
