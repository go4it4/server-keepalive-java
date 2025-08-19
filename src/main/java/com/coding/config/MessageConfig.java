package com.coding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegram.message")
public class MessageConfig {

    /**
     * message period: value of minute
     */
    private Long period = 60L;

    /**
     * message suffix: serverName+suffix
     */
    private String suffix = " server break down";
}
