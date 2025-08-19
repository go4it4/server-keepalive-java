package com.coding.util;

import com.coding.component.ApplicationContextUtil;
import com.coding.config.TelegramConfig;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class TelegramUtil {

    private final static Logger logger = LoggerFactory.getLogger(TelegramUtil.class);

    private TelegramUtil() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    private static final Map<String, TelegramBot> BOTS = new ConcurrentHashMap<>(16);

    private static final ReentrantLock BOT_LOCK = new ReentrantLock();

    public static final LinkPreviewOptions DISABLE_LINK_PREVIEW = new LinkPreviewOptions().isDisabled(true);

    public static TelegramBot getBotByToken(String botToken) {
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
     * send one text message
     *
     * @param content message content
     */
    public static void sendOneTextMessage(String content) {
        if (!StringUtils.hasText(content)) {
            return;
        }
        TelegramConfig config = ApplicationContextUtil.getBean(TelegramConfig.class);
        if (!StringUtils.hasText(config.getBotToken()) || config.getChatId() == null) {
            return;
        }
        SendMessage sendMessage = new SendMessage(config.getChatId(), escapeSymbol(content));
        sendMessage.parseMode(ParseMode.MarkdownV2);
        sendMessage.linkPreviewOptions(DISABLE_LINK_PREVIEW);

        TelegramBot telegramBot = TelegramUtil.getBotByToken(config.getBotToken());
        SendResponse response;
        try {
            response = telegramBot.execute(sendMessage);
        } catch (Exception e) {
            logger.error("send msg to Telegram with Bot error,chatId={}", config.getChatId());
            return;
        }

        if (!response.isOk()) {
            logger.error("send msg to TelegramBot fail,chatId={},errorCode={},description={},content={}",
                    config.getChatId(), response.errorCode(), response.description(), content);
        }
    }

    /**
     * escape symbol
     *
     * @param str String
     * @return String
     */
    private static String escapeSymbol(String str) {
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
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.");
    }
}