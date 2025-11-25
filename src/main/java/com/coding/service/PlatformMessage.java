package com.coding.service;

import com.coding.model.MessageChannel;

public interface PlatformMessage {

    String platform();

    void sendContent(MessageChannel channel, String content);

}
