package com.example.musicbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class YouTubeService {

    @Value("${youtube.api-key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String YOUTUBE_API_URL =
        "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=%s&key=%s";

    public String getVideoTitle(String videoId) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("YouTube API key not configured, using default title");
            return "YouTube Video";
        }

        try {
            String url = String.format(YOUTUBE_API_URL, videoId, apiKey);
            String response = restTemplate.getForObject(url, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null && items.isArray() && items.size() > 0) {
                JsonNode snippet = items.get(0).get("snippet");
                if (snippet != null && snippet.has("title")) {
                    return snippet.get("title").asText();
                }
            }

            log.warn("Could not find title for video: {}", videoId);
            return "YouTube Video";
        } catch (Exception e) {
            log.error("Failed to fetch YouTube video title for: {}", videoId, e);
            return "YouTube Video";
        }
    }
}
