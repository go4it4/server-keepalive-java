package com.coding.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private DateUtil() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("UTC+08");

    public static final ZoneId US_ZONE_ID = ZoneId.of("America/New_York");

    public static LocalDate nowDate() {
        return nowDate(DEFAULT_ZONE_ID);
    }

    public static LocalDate nowDate(ZoneId zoneId) {
        return LocalDate.now(zoneId);
    }

    public static LocalDateTime nowDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

    public static LocalDateTime of(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), DEFAULT_ZONE_ID);
    }

    public static long parse(String text, DateTimeFormatter formatter) {
        if (text == null || text.isEmpty()) {
            return 0L;
        }
        return LocalDateTime.parse(text, formatter).atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    public static String format(LocalDateTime time, DateTimeFormatter formatter) {
        if (time == null) {
            return null;
        }
        return time.format(formatter);
    }

    public static String format(LocalDateTime time) {
        return format(time, FORMATTER);
    }

    /**
     * 日期时间转时间戳
     *
     * @param localDateTime 日期时间
     * @return 时间戳，毫秒
     */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        return localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * 当前时间戳
     *
     * @return long 时间戳，毫秒
     */
    public static long nowMilli() {
        return toEpochMilli(LocalDateTime.now());
    }
}
