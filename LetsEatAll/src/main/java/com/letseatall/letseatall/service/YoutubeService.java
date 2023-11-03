package com.letseatall.letseatall.service;

public interface YoutubeService {
  // YouTube 자막 정보를 저장
    YoutubeDto saveYoutubeCaption(YoutubeDto youtubeDto);

    // YouTube 자막 정보를 업데이트
    YoutubeDto updateYoutubeCaption(Long id, YoutubeDto youtubeDto);

    // YouTube 자막 정보를 삭제
    void deleteYoutubeCaption(Long id);

    // 특정 YouTube 자막 정보를 조회
    YoutubeDto getYoutubeCaptionById(Long id);
}
