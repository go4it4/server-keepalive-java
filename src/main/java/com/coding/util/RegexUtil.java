package com.coding.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    private RegexUtil() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    private static Pattern defaultPattern(String regex) {
        return Pattern.compile(regex, Pattern.UNIX_LINES);
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param regex   匹配的正则
     * @param content 被查找的内容
     * @return 结果集
     */
    public static List<String> findAll(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return Collections.emptyList();
        }

        List<String> list = new ArrayList<>();
        final Matcher matcher = defaultPattern(regex).matcher(content);
        while (matcher.find()) {
            list.add(matcher.group(0));
        }

        return list;
    }

}
