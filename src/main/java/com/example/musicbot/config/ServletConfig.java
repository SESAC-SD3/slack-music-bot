package com.example.musicbot.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan("com.example.musicbot.slack")
public class ServletConfig {
}
