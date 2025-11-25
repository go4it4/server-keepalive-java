package com.coding.enums;

public enum MessagePlatformEnum {

    TELEGRAM("telegram", "电报"),

    FEISHU("feishu", "飞书"),

    LARK("lark", "Lark");

    MessagePlatformEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private final String value;

    private final String desc;

    public String value() {
        return value;
    }

    public String desc() {
        return desc;
    }
}
