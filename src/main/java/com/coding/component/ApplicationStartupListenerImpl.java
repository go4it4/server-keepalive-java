package com.coding.component;

import com.coding.config.MessageConfig;
import com.coding.config.RemoteServer;
import com.coding.model.CycleStack;
import com.coding.model.MessageChannel;
import com.coding.service.PlatformMessage;
import com.coding.util.HttpClientUtil;
import com.coding.util.ThreadUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Order(-1)
@Component
@RequiredArgsConstructor
public class ApplicationStartupListenerImpl implements ApplicationListener<@NonNull ApplicationStartedEvent> {

    private final static Logger logger = LoggerFactory.getLogger(ApplicationStartupListenerImpl.class);

    private final ScheduledThreadPoolExecutor scheduledExecutor = ThreadUtil.newScheduledExecutor(
            1, 2, new KeepaliveThreadFactory());

    private static final int PERIOD = 20;

    private final RemoteServer remoteServer;

    private final MessageConfig messageConfig;

    private final List<PlatformMessage> platformMessageList;

    private final Map<String, CycleStack> SERVICE_TIME = new ConcurrentHashMap<>();

    private final Map<String, Long> MSG_TIME = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        if (remoteServer == null || remoteServer.getServers().isEmpty()) {
            return;
        }

        for (int i = 0; i < remoteServer.getServers().size(); i++) {
            RemoteServer.KaServer kaServer = remoteServer.getServers().get(i);
            if (!Integer.valueOf(1).equals(kaServer.getOpenFlag())) {
                continue;
            }
            scheduledExecutor.scheduleAtFixedRate(() -> heartBeatReq(kaServer),
                    ((long) i * (PERIOD / remoteServer.getServers().size()) + 2), PERIOD, TimeUnit.SECONDS);
        }
    }

    private void heartBeatReq(RemoteServer.KaServer server) {
        // logger.info("heartBeatReq: server name=[{}]", server.getName());
        int capacity = (int) (messageConfig.getPeriod() / PERIOD);
        CycleStack cycleStack = SERVICE_TIME.getOrDefault(server.getName(), new CycleStack(capacity));
        if (cycleStack.isFull()) {
            boolean keepalive = false;
            for (Long timestamp : cycleStack.getData()) {
                if (timestamp > 0) {
                    keepalive = true;
                    break;
                }
            }

            // server break down
            if (!keepalive) {
                Long errMsgTime = MSG_TIME.getOrDefault(server.getName(), 0L);
                boolean sendNotify = errMsgTime <= 0 || System.currentTimeMillis() - errMsgTime >= messageConfig.getPeriod() * 1000;
                if (sendNotify) {
                    logger.error("{}{}", server.getName(), messageConfig.getSuffix());
                    MSG_TIME.put(server.getName(), System.currentTimeMillis());
                    String content = "*" + server.getName() + "* " + messageConfig.getSuffix();
                    sendMessage(content);
                }
            }
        }

        int code = HttpClientUtil.requestGetStatusCode(server.getAddr());
        if (code == 200) {
            cycleStack.push(System.currentTimeMillis());
        } else {
            cycleStack.push(0L);
        }
        SERVICE_TIME.put(server.getName(), cycleStack);
    }

    private void sendMessage(String content) {
        List<MessageChannel> channels = messageConfig.getChannels();
        if (channels == null || channels.isEmpty()) {
            logger.warn("未配置消息通道");
            return;
        }

        for (MessageChannel channel : channels) {
            if (!Integer.valueOf(1).equals(channel.getOpenFlag())) {
                continue;
            }
            PlatformMessage platformMessage = getPlatformMessage(channel.getPlatform());
            if (platformMessage == null) {
                continue;
            }
            platformMessage.sendContent(channel, content);
        }
    }

    private PlatformMessage getPlatformMessage(String platform) {
        if (platformMessageList == null || platformMessageList.isEmpty()) {
            return null;
        }
        for (PlatformMessage platformMessage : platformMessageList) {
            if (platformMessage.platform().equals(platform.trim())) {
                return platformMessage;
            }
        }
        return null;
    }
}