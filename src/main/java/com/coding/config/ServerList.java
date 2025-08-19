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
@ConfigurationProperties(prefix = "config")
public class ServerList {

    private List<KaServer> servers = new ArrayList<>();

    @Getter
    @Setter
    public static class KaServer {

        private String name;

        private String addr;
    }
}
