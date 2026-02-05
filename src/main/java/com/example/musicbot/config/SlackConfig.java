package com.example.musicbot.config;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    private static final Logger log = LoggerFactory.getLogger(SlackConfig.class);

    @Value("${slack.bot-token:}")
    private String botToken;

    @Value("${slack.signing-secret:}")
    private String signingSecret;

    @PostConstruct
    public void init() {
        log.info("Slack Bot Token loaded: {}", botToken != null && !botToken.isEmpty()
                ? botToken.substring(0, Math.min(20, botToken.length())) + "..."
                : "EMPTY");
        log.info("Slack Signing Secret loaded: {}", signingSecret != null && !signingSecret.isEmpty()
                ? "***" + signingSecret.substring(Math.max(0, signingSecret.length() - 4))
                : "EMPTY");
    }

    @Bean
    public AppConfig slackAppConfig() {
        return AppConfig.builder()
                .singleTeamBotToken(botToken)
                .signingSecret(signingSecret)
                .build();
    }

    @Bean
    public App slackApp(AppConfig config) {
        App app = new App(config);
        // 기본 OAuth 토큰 검증 비활성화 (토큰이 유효한지 직접 확인하기 위해)
        app.config().setOAuthStartEnabled(false);
        app.config().setOAuthCallbackEnabled(false);
        return app;
    }
}
