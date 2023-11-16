package com.letseatall.letseatall.controller;

import com.letseatall.letseatall.data.dto.Youtube.CaptionDto;
import com.letseatall.letseatall.service.YoutubeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CaptionController {

    @Autowired
    private YoutubeService youTubeService;
    private final Logger log= LoggerFactory.getLogger(CaptionController.class);

    @RequestMapping(value = "youtube/summary", method = RequestMethod.POST)
    public ResponseEntity<CaptionDto> summaryVideo(@RequestParam String videoUrl){
        if(!videoUrl.contains("http://")){
            videoUrl = "https://www.youtube.com/watch?"+videoUrl;
        }
        log.info("[summaryVideo] url = {}", videoUrl);

        log.info("[summaryVideo] 특징 분석 중...");

        // videoUrl에서 비디오 ID 추출
        String firstUrl = youTubeService.getUrl(videoUrl); // 메소드 이름 변경을 제안
        String decodeUrl = youTubeService.returnUrl(firstUrl); // 메소드 이름 변경을 제안
        String captionXml = youTubeService.getUrl(decodeUrl);
        String caption = youTubeService.makeCaption(captionXml);
        String summary = youTubeService.summarizeCaption(caption);
        log.info("[summaryVideo] 특징 분석 완료");
        // CaptionDto 객체 생성 및 설정
        CaptionDto captionDto = new CaptionDto(videoUrl, summary);

        log.info("[summaryVideo] 분석 결과 반환");
        return ResponseEntity.ok(captionDto);
    }
    @RequestMapping(value="subtitlesPage",method = RequestMethod.GET)
    public String gotoPutPage() {
        return "subtitlesPage.html";
    }

    @PostMapping("/getSubtitles")
    public String getSubtitles(@RequestParam String videoUrl, Model model) {

            // videoUrl에서 비디오 ID 추출
            String firstUrl = youTubeService.getUrl(videoUrl); // 메소드 이름 변경을 제안
            String decodeUrl = youTubeService.returnUrl(firstUrl); // 메소드 이름 변경을 제안
            String captionXml = youTubeService.getUrl(decodeUrl);
            String caption = youTubeService.makeCaption(captionXml);
            String summary = youTubeService.summarizeCaption(caption);
            System.out.println("특징 분석: " + summary);

            // CaptionDto 객체 생성 및 설정
            CaptionDto captionDto = new CaptionDto(videoUrl, summary);

            // 모델에 CaptionDto 객체 추가
            model.addAttribute("captionDto", captionDto);

            // 자막을 보여줄 View의 이름
            return "captionPage.html";
    }
}
