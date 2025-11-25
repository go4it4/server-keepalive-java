package com.coding.config;

import com.coding.model.MessageChannel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "message")
public class MessageConfig {

    /**
     * message period: value of seconds, default 180s
     */
    private Long period = 180L;

    private static final Long MIN_PERIOD = 60L;

    /**
     * message suffix: serverName+suffix
     */
    private String suffix = " server break down";

    /**
     * Message Channel
     */
    private List<MessageChannel> channels = new ArrayList<>();

    public Long getPeriod() {
        if (period == null || period < 60) {
            return MIN_PERIOD;
        }

        return period;
    }
}
