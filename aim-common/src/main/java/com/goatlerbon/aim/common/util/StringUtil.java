package com.goatlerbon.aim.common.util;

/**
 * String类型工具类
 */
public class StringUtil {
    public StringUtil() {
    }

    public static boolean isNullOrEmpty(String str) {
        return null == str || 0 == str.trim().length();
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str.trim());
    }

    public static String formatLike(String str) {
        return isNotEmpty(str)?"%" + str + "%":null;
    }
}
