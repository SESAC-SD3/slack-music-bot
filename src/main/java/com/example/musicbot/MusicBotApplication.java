package com.example.musicbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MusicBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicBotApplication.class, args);
    }
}
