package com.letseatall.letseatall.controller;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CaptionListResponse;
import com.google.api.services.youtube.model.Caption;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class YoutubeController {

    private static final String ACCESS_TOKEN = "유튜브 API 키";

    @GetMapping("/getYouTubeCaptions")
    public ModelAndView getYouTubeCaptions(@RequestParam String videoId) {
        try {
            // YouTube API 연결
            YouTube youtube = getYouTubeService();

            // 비디오의 자막 목록을 가져오기 위한 API 요청
            YouTube.Captions.List captionRequest = youtube.captions().list("snippet", videoId);
            CaptionListResponse captionListResponse = captionRequest.execute();

            List<String> captions = new ArrayList<>();

            for (Caption caption : captionListResponse.getItems()) {
                String captionContent = downloadCaptionContent(youtube,caption.getId()); // 자막 다운
                String summarizedCaption = summarizeTextWithGPT3(captionContent); // 자막 요약
                captions.add(summarizedCaption);
            }

            return new ModelAndView("captionsView", "captions", captions);

        } catch (IOException e) {
            e.printStackTrace();
            return new ModelAndView("errorView", "error", e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // YouTube 서비스 인스턴스를 가져오는 메소드
    private YouTube getYouTubeService() throws IOException {
        GoogleCredential credential = new GoogleCredential().setAccessToken(ACCESS_TOKEN);
        return new YouTube.Builder(credential.getTransport(), credential.getJsonFactory(), credential)
                .setApplicationName("어플이름")
                .build();
    }

    // 실제 자막 콘텐츠를 다운로드하는 로직을 구현해야 하는 부분
    private String downloadCaptionContent(YouTube youtubeService, String captionId) throws IOException {
        // 자막 다운로드 URL을 얻기 위한 요청 생성
        YouTube.Captions.Download captionDownloadRequest = youtubeService.captions().download(captionId);
        captionDownloadRequest.setTfmt("srt"); // or whatever format you need

        // 요청 실행 및 스트림으로 자막 다운로드
        try {
            com.google.api.client.http.HttpResponse response = captionDownloadRequest.executeMedia();
            return response.parseAsString(); // 자막 콘텐츠를 String으로 변환
        } catch (GoogleJsonResponseException e) {
            System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return ""; // 에러 발생 시 빈 문자열 반환
    }

    // GPT-3 API를 사용하여 텍스트를 요약하는 메소드
    private String summarizeTextWithGPT3(String text) throws IOException, InterruptedException {
        String apiKey = "오픈AI 키"; // 실제 API 키로 대체해야 함
        String apiUrl = "https://api.openai.com/v1/engines/davinci-codex/completions";

        // 요청 바디 생성
        JSONObject data = new JSONObject();
        data.put("prompt", text);
        data.put("max_tokens", 50); // 요약을 위한 최대 토큰 수
        data.put("temperature", 0.5); // 생성의 결정성을 제어 (0.0 - 1.0)

        // HttpClient를 사용하여 POST 요청 생성 및 전송
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(data.toString()))
                .build();

        // 요청 실행 및 응답 받기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // 성공적인 응답 처리
            JSONObject jsonResponse = new JSONObject(response.body());
            String summarizedText = jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text");
            return summarizedText;
        } else {
            // 에러 처리
            System.err.println("API 요청 실패: " + response.statusCode());
            return ""; // 에러 발생 시 빈 문자열 반환
        }
    }
}
