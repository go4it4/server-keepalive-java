package com.coding.component;

import com.coding.config.MessageConfig;
import com.coding.config.ServerList;
import com.coding.model.CycleStack;
import com.coding.util.HttpClientUtil;
import com.coding.util.TelegramUtil;
import com.coding.util.ThreadUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Order(-1)
@Component
@RequiredArgsConstructor
public class ApplicationStartupListenerImpl implements ApplicationListener<ApplicationStartedEvent> {

    private final static Logger logger = LoggerFactory.getLogger(ApplicationStartupListenerImpl.class);

    private final ScheduledThreadPoolExecutor scheduledExecutor = ThreadUtil.newScheduledExecutor(
            1, 2, new KeepaliveThreadFactory());

    private static final int PERIOD = 20;

    private final ServerList serverList;

    private final MessageConfig messageConfig;

    private final Map<String, CycleStack> SERVICE_TIME = new ConcurrentHashMap<>();

    private final Map<String, Long> MSG_TIME = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(@NonNull ApplicationStartedEvent event) {
        if (serverList == null || serverList.getServers().isEmpty()) {
            return;
        }

        for (int i = 0; i < serverList.getServers().size(); i++) {
            int finalI = i;
            scheduledExecutor.scheduleAtFixedRate(() -> heartBeatReq(serverList.getServers().get(finalI)),
                    ((long) i * (PERIOD / serverList.getServers().size()) + 2), PERIOD, TimeUnit.SECONDS);
        }
    }

    private void heartBeatReq(ServerList.KaServer server) {
        // logger.info("heartBeatReq: server name=[{}]", server.getName());
        CycleStack cycleStack = SERVICE_TIME.getOrDefault(server.getName(), new CycleStack(5));
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
                    TelegramUtil.sendOneTextMessage("*" + server.getName() + "*" + messageConfig.getSuffix());
                }
            }
        }

        int code = HttpClientUtil.requestGet0(server.getAddr());
        if (code == 200) {
            cycleStack.push(System.currentTimeMillis());
        } else {
            cycleStack.push(0L);
        }
        SERVICE_TIME.put(server.getName(), cycleStack);
    }
}