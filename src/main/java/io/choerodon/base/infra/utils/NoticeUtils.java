package io.choerodon.base.infra.utils;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoticeUtils {

    private NoticeUtils() {
    }

    public static String renderMessageMapToString(String content, Map<String, String> messageMap) {
        Set<Map.Entry<String, String>> sets = messageMap.entrySet();
        for (Map.Entry<String, String> entry : sets) {
            String regex = "\\$\\{" + entry.getKey() + "\\}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            content = matcher.replaceAll(entry.getValue());
        }
        return content;
    }
}
