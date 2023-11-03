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

    @Override
    public YoutubeDto saveYoutubeCaption(YoutubeDto youtubeDto) {   // YouTube 자막 정보를 저장
        // YoutubeDto를 Youtube Entity로 변환
        Youtube youtube = new Youtube(youtubeDto.getUrl(), youtubeDto.getContent());

        // Youtube Entity를 저장
        Youtube savedYoutube = youtubeRepository.save(youtube);

        // 저장된 Youtube Entity의 정보를 YoutubeDto로 변환하여 반환
        return YoutubeDto.builder()
                .url(savedYoutube.getUrl())
                .content(savedYoutube.getContent())
                .mid(savedYoutube.getMenu().getId())
                .build();
    }

    @Override
    public YoutubeDto updateYoutubeCaption(Long id, YoutubeDto youtubeDto) { // YouTube 자막 정보를 업데이트
        Optional<Youtube> optionalYoutube = youtubeRepository.findById(id);

        if (optionalYoutube.isPresent()) {
            Youtube youtube = optionalYoutube.get();

            // YoutubeDto에서 업데이트할 필드를 가져와서 업데이트
            youtube.setUrl(youtubeDto.getUrl());
            youtube.setContent(youtubeDto.getContent());

            // YouTube 정보 업데이트
            Youtube updatedYoutube = youtubeRepository.save(youtube);

            // 업데이트된 정보를 YoutubeDto로 변환하여 반환
            return YoutubeDto.builder()
                    .url(updatedYoutube.getUrl())
                    .content(updatedYoutube.getContent())
                    .mid(updatedYoutube.getMenu().getId())
                    .build();
        } else {
            // 지정된 ID에 해당하는 YouTube 정보가 없을 경우 null 반환 또는 예외 처리
            return null;
        }
    }

    @Override
    public void deleteYoutubeCaption(Long id) { // YouTube 자막 정보를 삭제
        // 지정된 ID로 YouTube 정보를 찾아서 삭제
        youtubeRepository.deleteById(id);
    }

    @Override
    public YoutubeDto getYoutubeCaptionById(Long id) { // 지정된 ID로 YouTube 자막 정보를 조회
        Optional<Youtube> optionalYoutube = youtubeRepository.findById(id);

        if (optionalYoutube.isPresent()) {
            Youtube youtube = optionalYoutube.get();

            // YouTube 정보를 YoutubeDto로 변환하여 반환
            return YoutubeDto.builder()
                    .url(youtube.getUrl())
                    .content(youtube.getContent())
                    .mid(youtube.getMenu().getId())
                    .build();
        } else {
            // 지정된 ID에 해당하는 YouTube 정보가 없을 경우 null 반환 또는 예외 처리
            return null;
        }
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
