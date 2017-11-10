package com.javaear.json4bean.util;

/**
 * @author aooer
 */
public abstract class StringUtils {

    /* 前置空间 */
    public static Boolean isCreateMultiBean = false;

    /* 前置空间 */
    public static final String PREFIX_SPACE = "    ";

    /* 系统换行符 */
    public static final String LINE = System.getProperty("line.separator");

    /**
     * 第一个字母转大写
     *
     * @param sourceStr 原str
     * @return 转换后的str
     */
    public static String upperCaseFirstChar(String sourceStr) {
        return String.valueOf(Character.toUpperCase(sourceStr.charAt(0))) + sourceStr.substring(1);
    }

    /**
     * 空字符串判断
     *
     * @param sourceStr sourceStr
     * @return 是否为空
     */
    public static boolean isEmpty(String sourceStr) {
        return sourceStr == null || "".equals(sourceStr);
    }

}
