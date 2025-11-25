package com.coding.service.message;

import com.coding.enums.MessagePlatformEnum;
import com.coding.model.MessageChannel;
import com.coding.service.PlatformMessage;
import com.coding.util.HttpClientUtil;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Service
public class MessageTelegramImpl implements PlatformMessage {

    private final static Logger logger = LoggerFactory.getLogger(MessageTelegramImpl.class);

    private static final Map<String, TelegramBot> BOTS = new ConcurrentHashMap<>(16);

    private static final ReentrantLock BOT_LOCK = new ReentrantLock();

    private static final LinkPreviewOptions DISABLE_LINK_PREVIEW = new LinkPreviewOptions().isDisabled(true);

    @Override
    public String platform() {
        return MessagePlatformEnum.TELEGRAM.value();
    }

    /**
     * 发送一条文本消息
     *
     * @param channel 消息通道
     * @param content 消息内容
     */
    @Override
    public void sendContent(MessageChannel channel, String content) {
        sendOneTextMessage(channel.getAppSecret().trim(), Long.parseLong(channel.getChatId().trim()), content);
    }

    /**
     * send one text message
     *
     * @param content message content
     */
    public void sendOneTextMessage(String botToken, Long chatId, String content) {
        if (!StringUtils.hasText(content)) {
            return;
        }
        if (!StringUtils.hasText(botToken) || chatId == null) {
            return;
        }
        SendMessage sendMessage = new SendMessage((long) chatId, escapeSymbol(content));
        sendMessage.parseMode(ParseMode.MarkdownV2);
        sendMessage.linkPreviewOptions(DISABLE_LINK_PREVIEW);

        TelegramBot telegramBot = getBotByToken(botToken);
        SendResponse response;
        try {
            response = telegramBot.execute(sendMessage);
        } catch (Exception e) {
            logger.error("send msg to Telegram with Bot error,chatId={}", chatId);
            return;
        }

        if (!response.isOk()) {
            logger.error("send msg to TelegramBot fail,chatId={},errorCode={},description={},content={}",
                    chatId, response.errorCode(), response.description(), content);
        }
    }

    private TelegramBot getBotByToken(String botToken) {
        BOT_LOCK.lock();
        try {
            TelegramBot bot = BOTS.get(botToken);
            if (bot == null) {
                bot = new TelegramBot.Builder(botToken).okHttpClient(HttpClientUtil.getHttpClient()).build();
                BOTS.put(botToken, bot);
            }
            return bot;
        } finally {
            BOT_LOCK.unlock();
        }
    }

    /**
     * escape symbol
     *
     * @param str String
     * @return String
     */
    private String escapeSymbol(String str) {
        if (str == null) {
            return null;
        }
        // In all other places characters
        // '_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!'
        // must be escaped with the preceding character '\'.
        return str
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }
}