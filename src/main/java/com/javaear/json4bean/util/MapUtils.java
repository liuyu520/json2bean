package com.javaear.json4bean.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aooer
 */
public abstract class MapUtils {

    /* 转义映射 */
    public static final Map<Character, Character> TRANSEFER_CHAR_MAP = new HashMap<Character, Character>() {{
        put('"', '"');
        put('\\', '\\');
        put('/', '/');
        put('b', '\b');
        put('f', '\f');
        put('n', '\n');
        put('r', '\r');
        put('t', '\t');
    }};

    /* 代码模板 */
    public static final Map<String, String> CODE_TEMPLATE_MAP = new HashMap<String, String>() {{
        put("copyright", "");
        put("class", "");
        put("getter", "");
        put("setter", "");
    }};

}
