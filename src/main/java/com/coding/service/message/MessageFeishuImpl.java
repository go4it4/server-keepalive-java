package com.coding.service.message;

import com.coding.enums.MessagePlatformEnum;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageFeishuImpl extends AbstractFeishuMessage {

    private static final Map<String, FeishuApp> APP_MAP = new ConcurrentHashMap<>();

    private static final String BASE_URL = "https://open.feishu.cn";

    @Override
    public String platform() {
        return MessagePlatformEnum.FEISHU.value();
    }

    @Override
    protected String tenantAccessTokenUrl() {
        return BASE_URL + TENANT_ACCESS_TOKEN;
    }

    @Override
    protected String messagesUrl() {
        return BASE_URL + MESSAGES;
    }

    @Override
    protected Map<String, FeishuApp> appCache() {
        return APP_MAP;
    }
}