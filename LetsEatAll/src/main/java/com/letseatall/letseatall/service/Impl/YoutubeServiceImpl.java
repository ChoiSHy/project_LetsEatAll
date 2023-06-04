package com.letseatall.letseatall.service.Impl;

import com.letseatall.letseatall.data.Entity.Menu;
import com.letseatall.letseatall.data.Entity.Youtube;
import com.letseatall.letseatall.data.dto.StringChangeDto;
import com.letseatall.letseatall.data.dto.Youtube.YoutubeDto;
import com.letseatall.letseatall.data.repository.MenuRepository;
import com.letseatall.letseatall.data.repository.YoutubeRepository;
import com.letseatall.letseatall.service.YoutubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class YoutubeServiceImpl implements YoutubeService {
    private YoutubeRepository youtubeRepository;
    private MenuRepository menuRepository;

    @Autowired
    public YoutubeServiceImpl(YoutubeRepository youtubeRepository,
                              MenuRepository menuRepository) {
        this.youtubeRepository = youtubeRepository;
        this.menuRepository = menuRepository;
    }

    /* 아직 적용 x */
    public YoutubeDto saveNew(YoutubeDto request) {
        Optional<Menu> optionalMenu = menuRepository.findById(request.getMid());
        if (optionalMenu.isPresent()) {
            Youtube newVideo = new Youtube(request.getUrl(), request.getContent());
            newVideo.setMenu(optionalMenu.get());
            Youtube savedVideo = youtubeRepository.save(newVideo);

            YoutubeDto responseDto = YoutubeDto.builder()
                    .url(savedVideo.getUrl())
                    .content(savedVideo.getContent())
                    .mid(savedVideo.getMenu().getId())
                    .build();
            return responseDto;
        }
        return null;
    }

    public List<YoutubeDto> getAllAboutMenu(Long mid) {
        Optional<Menu> optionalMenu = menuRepository.findById(mid);

        if (optionalMenu.isPresent()) {
            Menu menu = optionalMenu.get();
            List<YoutubeDto> responseList = new ArrayList<>();

            for (Youtube y : menu.getYoutubeList()) {
                responseList.add(YoutubeDto.builder()
                        .url(y.getUrl())
                        .content(y.getContent())
                        .mid(y.getMenu().getId())
                        .build());
            }
            return responseList;
        }
        return null;
    }

    public YoutubeDto modify(StringChangeDto changes){
        Optional<Youtube> optionalYoutube = youtubeRepository.findById(changes.getId());

        if(optionalYoutube.isPresent()){
            Youtube foundOne = optionalYoutube.get();
            foundOne.setContent(changes.getValue());
            Youtube savedOne = youtubeRepository.save(foundOne);

            return YoutubeDto.builder()
                    .url(savedOne.getUrl())
                    .content(savedOne.getContent())
                    .mid(savedOne.getMenu().getId())
                    .build();
        }
        return null;
    }

    public void deleteYoutube(Long id){
        youtubeRepository.deleteById(id);
    }
}
