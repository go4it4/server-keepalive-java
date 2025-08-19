package com.coding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegram")
public class TelegramConfig {

    private String botToken;

    private Long chatId;

    private String msgPeriod;

    private String msgSuffix;
}
