package com.capstone.safeGuard.service;

import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class NaverSpeechService {

    // TODO: ID, SECRET 변경 필요
    private static final String CLIENT_ID = "YOUR_CLIENT_ID"; // 네이버 클라우드 애플리케이션 클라이언트 ID
    private static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET"; // 네이버 클라우드 애플리케이션 클라이언트 시크릿
    private static final String API_URL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt";
    private static final List<String> TARGET_TEXTS = Arrays.asList("도와주세요", "긴급 상황", "구조 요청"); // 비교할 여러 타겟 텍스트

    public String transcribeAndCompare(MultipartFile file) throws IOException {
        String transcribedText = transcribe(file);
        for (String targetText : TARGET_TEXTS) {
            if (transcribedText.contains(targetText)) {
                performAction(targetText);
                return "매치된 텍스트: '" + targetText + "'. 행동이 수행되었습니다.";
            }
        }
        return "일치하는 텍스트가 없습니다.";
    }

    private String transcribe(MultipartFile file) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file.getBytes());

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("X-NCP-APIGW-API-KEY-ID", CLIENT_ID)
                .addHeader("X-NCP-APIGW-API-KEY", CLIENT_SECRET)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("예상치 못한 응답 코드: " + response);
            }
            return response.body().string();
        }
    }

    private void performAction(String matchedText) {
        // 여기에 특정 행동을 수행하는 코드를 작성합니다.
        System.out.println("일치하는 텍스트가 발견되어 행동이 수행되었습니다: " + matchedText);
        // 예를 들어, 알림을 보내거나 데이터베이스에 기록하는 등의 작업을 수행할 수 있습니다.
    }
}
