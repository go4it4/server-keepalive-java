package com.coding.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "remote")
public class RemoteServer {

    private List<KaServer> servers = new ArrayList<>();

    @Getter
    @Setter
    public static class KaServer {

        private String name;

        private Integer openFlag;

        private String addr;

        /**
         * millis, default 10000
         */
        private Long period = 10000L;

        /**
         * millis, default 5000
         */
        private Long timeout = 5000L;

        /**
         * fail count, default 5
         */
        private Integer failCount = 5;
    }
}
