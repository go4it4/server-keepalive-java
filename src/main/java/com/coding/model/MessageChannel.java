package com.coding.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageChannel {

    private String platform;

    private Integer openFlag;

    private String appId;

    private String appSecret;

    private String chatId;
}
