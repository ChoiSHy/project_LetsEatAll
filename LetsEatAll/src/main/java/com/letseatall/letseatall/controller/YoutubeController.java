package com.letseatall.letseatall.controller;

import com.example.demo.Service.YouTubeService; // 패키지 경로에 맞게 이름 수정 필요
import com.example.demo.dto.CaptionDto; // 패키지 경로에 맞게 이름 수정 필요
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CaptionController {

    @Autowired
    private YouTubeService youTubeService;

    @RequestMapping(value="subtitlesPage",method = RequestMethod.GET)
    public String gotoPutPage() {
        return "subtitlesPage";
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
            return "captionPage";
    }
}
