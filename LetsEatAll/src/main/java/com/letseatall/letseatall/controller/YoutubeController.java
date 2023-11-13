package com.letseatall.letseatall.controller;

import com.example.demo.Service.YouTubeService;
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
            System.out.println("요약된 가사: " + summary);

            // 모델에 데이터 추가 (필요한 경우)
            model.addAttribute("decodedUrl", decodeUrl);

            // 자막을 보여줄 View의 이름
            return "captionPage";
    }
}
