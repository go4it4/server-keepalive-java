package com.coding.service.message;

import com.coding.model.MessageChannel;
import com.coding.service.PlatformMessage;
import com.coding.util.DateUtil;
import com.coding.util.HttpClientUtil;
import com.coding.util.JsonUtil;
import com.coding.util.RegexUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractFeishuMessage implements PlatformMessage {

    protected record FeishuApp(long expire, String tenantAccessToken) {

    }

    /**
     * tenant_access_token
     */
    protected static final String TENANT_ACCESS_TOKEN = "/open-apis/auth/v3/tenant_access_token/internal";

    /**
     * 应用消息
     */
    protected static final String MESSAGES = "/open-apis/im/v1/messages";

    private static final String BOLD_REGEX = "\\*.*?\\*";

    protected abstract String tenantAccessTokenUrl();

    protected abstract String messagesUrl();

    protected abstract Map<String, FeishuApp> appCache();

    /**
     * 发送一条文本消息
     *
     * @param channel 消息通道
     * @param content 消息内容
     */
    @Override
    public void sendContent(MessageChannel channel, String content) {
        String tenantAccessToken = getTenantAccessToken(channel.getAppId().trim(), channel.getAppSecret().trim());
        if (tenantAccessToken == null) {
            return;
        }
        if (channel.getChatId() == null || channel.getChatId().trim().isEmpty()) {
            return;
        }
        String receiveId = channel.getChatId().trim();
        Map<String, Object> urlParams = null;
        if (receiveId.startsWith("ou_")) {
            // 应用消息
            urlParams = Map.of("receive_id_type", "open_id");
        } else if (receiveId.startsWith("oc_")) {
            // 群组消息
            urlParams = Map.of("receive_id_type", "chat_id");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("msg_type", "text");
        params.put("receive_id", receiveId);
        params.put("uuid", UUID.randomUUID().toString());
        params.put("content", JsonUtil.toJsonString(Map.of("text", formatContent(content))));
        Map<String, String> headers = Map.of("Authorization", "Bearer " + tenantAccessToken);
        HttpClientUtil.requestPost(messagesUrl(), urlParams, params, headers);
    }

    private String getTenantAccessToken(String appId, String appSecret) {
        FeishuApp app = appCache().get(appId);
        if (app == null) {
            app = requestAppToken(appId, appSecret);
        } else {
            if (app.expire() < DateUtil.nowMilli()) {
                app = requestAppToken(appId, appSecret);
            }
        }
        if (app == null) {
            return null;
        }
        appCache().put(appId, app);
        return app.tenantAccessToken();
    }

    private FeishuApp requestAppToken(String appId, String appSecret) {

        Map<String, Object> params = new HashMap<>();
        params.put("app_id", appId);
        params.put("app_secret", appSecret);
        String str = HttpClientUtil.requestPost(tenantAccessTokenUrl(), params);
        JsonNode jsonNode = JsonUtil.parseObject(str);
        if (jsonNode == null) {
            return null;
        }

        int code = jsonNode.get("code").asInt(-1);
        if (code != 0) {
            return null;
        }
        long expire = JsonUtil.getLong(jsonNode, "expire");
        String tenantAccessToken = JsonUtil.getText(jsonNode, "tenant_access_token");
        long expireTime = DateUtil.toEpochMilli(DateUtil.nowDateTime().plusSeconds(expire - 60));
        return new FeishuApp(expireTime, tenantAccessToken);
    }

    private String formatContent(String content) {
        List<String> regexList = RegexUtil.findAll(BOLD_REGEX, content);
        for (String str : regexList) {
            String s = str.replace("*", "");
            content = content.replace(str, "<b>" + s + "</b>");
        }
        return content;
    }
}
