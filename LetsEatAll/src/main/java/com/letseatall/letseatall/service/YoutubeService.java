package com.letseatall.letseatall.service; // 패키지 이름 경로에 맞게 수정 필요

import java.net.URL;
import com.google.api.services.youtube.YouTube;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.JSONObject;

@Service
public class YoutubeService {

    private YouTube youtube;
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions"; // OpenAI URL
    @Value("${openai.access.key}")
    private String API_KEY; // OpenAI API 키

    public String getUrl(String url) { // url을 통해 get 요청
        StringBuffer response = new StringBuffer();
        try {

            // URL 객체 생성
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // HTTP GET 요청 설정
            connection.setRequestMethod("GET");

            // 응답 내용 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 응답 내용 출력
            // System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public String returnUrl (String url) { // baseUrl 추출
        String htmlContent = url;

        String baseUrl = extractBaseUrl(htmlContent);
        System.out.println("Base URL: " + baseUrl);

        return baseUrl;
    }

    private static String extractBaseUrl(String content) { // baseUrl 추출
        // 정규 표현식으로 baseUrl을 찾습니다.
        Pattern pattern = Pattern.compile("\"captionTracks\":\\[\\{\"baseUrl\":\"(https[^\"]+)");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            // 유니코드 \u0026이 포함된 원래 문자열
            String originalUrl = matcher.group(1);
            System.out.println("Original URL (with unicode): " + originalUrl);

            // 유니코드 \u0026을 &로 변경
            String decodedUrl = originalUrl.replace("\\u0026", "&");
            System.out.println("Decoded URL: " + decodedUrl);

            return decodedUrl;
        }

        return ""; // baseUrl을 찾지 못한 경우
    }

    public String makeCaption(String xml) {
        String xmlContent = xml;
        String lyrics = "";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));

            NodeList textNodes = doc.getElementsByTagName("text");
            for (int i = 0; i < textNodes.getLength(); i++) {
                Element textElement = (Element) textNodes.item(i);
                lyrics += textElement.getTextContent() + "\n";
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return lyrics;
    }

    public String summarizeCaption(String text) { // chat gpt를 통해 특징 분석
        int maxTokens = 500; // 요청당 최대 토큰 수
        String customPrompt = "음식 리뷰를 분석해야해."
                + "아래 내용을보고 해당 음식의 특징을 분석해줘. " +
                "#분석 대상: \n" + text;

        System.out.println(customPrompt);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(BodyPublishers.ofString(new JSONObject()
                        .put("model", "gpt-3.5-turbo") // 사용할 모델 지정
                        .put("messages", new JSONArray()
                                .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
                                .put(new JSONObject().put("role", "user").put("content", customPrompt)))
                        .put("max_tokens", maxTokens)
                        .toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                String content = message.getString("content");
                return content; // content만 반환
            } else {
                return "응답이 없습니다.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "요약 중 오류 발생";
        }
    }

}
