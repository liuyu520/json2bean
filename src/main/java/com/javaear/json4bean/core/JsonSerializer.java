package com.javaear.json4bean.core;

import com.common.util.SystemHWUtil;
import com.javaear.json4bean.util.ArrayUtils;
import com.javaear.json4bean.util.MapUtils;
import com.javaear.json4bean.util.StringUtils;
import com.string.widget.util.ValueWidget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author aooer
 */
abstract class JsonSerializer implements Serializable {

    /**
     * 校验json
     *
     * @param json json
     */
    private static void validate(Object json) {
        if (json == null || !(json instanceof LinkedHashMap))
            throw new IllegalArgumentException("jsonstr validate exception");
    }

    /**
     * 序列化json对象
     *
     * @param jsonObj          :      jsonObj
     * @param className        :     className
     * @param finalPrefixSpace 前置空间
     * @return bean content内容
     */
    static String serialize(Object jsonObj, String className, final String finalPrefixSpace, Map<String, String> complexPropertyMap) {
        validate(jsonObj);
        StringBuilder stringBuilder = new StringBuilder(StringUtils.LINE);
        StringBuilder methodBuilder = new StringBuilder();
        StringBuilder innerBuilder = new StringBuilder();
        final String prefixSpace = StringUtils.isCreateMultiBean ? StringUtils.PREFIX_SPACE : finalPrefixSpace;
        stringBuilder.append(prefixSpace.substring(4)).append("public ").append(prefixSpace.equals(StringUtils.PREFIX_SPACE) ? "" : "static ").append("class ").append(className).append(" implements java.io.Serializable{");
        ((LinkedHashMap<?, ?>) jsonObj).entrySet().stream().forEach(e -> {
            String key = StringUtils.upperCaseFirstChar((String) e.getKey());
            if (key.equals("List") || key.equals("Map")
                    || key.equals("Object")) {
                key = key + "Custom";
            }
            key = key.replaceAll("[\\s]+", "");//解决"request charEncoding": "UTF-8",
            Object value = e.getValue();
            String key2 = ((String) e.getKey()).replaceAll("[\\s]+", "");
            /*if(key2.equals("list")){
                key2=key2+"Custom";
            }*/
            //list集合
            if (value instanceof ArrayList) {
                Object fieldFirst = null;
                String fieldType = null;
                if (((ArrayList) value).size() < 1) {
                    fieldType = "String";
                } else {
                    fieldFirst = ((ArrayList) value).get(0);
                    fieldType = fieldFirst.getClass().getSimpleName();
                    if (fieldFirst instanceof ArrayList)
                        fieldType = "List";
                    stringBuilder.insert(0, SystemHWUtil.CRLF + "import java.util.List;");
                    if (fieldFirst instanceof LinkedHashMap) {
                        fieldType = getComplexPropertyName(complexPropertyMap, key);
                    }
                }
//                    throw new IllegalArgumentException("jsonListNode maybe null! nodekey is ".concat(key));
                stringBuilder.append(newLine(prefixSpace)).append("private ").append("List<").append(fieldType).append(">").append(" ").append(e.getKey()).append(";");
                appendGetterSetter(methodBuilder, prefixSpace, "List<" + fieldType + ">", key2);
                if (null != fieldFirst && (fieldFirst instanceof ArrayList || fieldFirst instanceof LinkedHashMap))
                    innerBuilder.append(StringUtils.LINE).append(serialize(fieldFirst, getComplexPropertyName(complexPropertyMap, key), prefixSpace + prefixSpace, complexPropertyMap));
            } else if (value instanceof LinkedHashMap) {
                if (((LinkedHashMap) value).size() == 0) {
                    String propertyType = "Map";
                    stringBuilder.insert(0, SystemHWUtil.CRLF + "import java.util.Map;");
                    stringBuilder.append(newLine(prefixSpace)).append("private ").append(propertyType).append(" ").append(key2).append(";");
                    appendGetterSetter(methodBuilder, prefixSpace, propertyType, key2);
                } else {
                    key2 = getColumnName(complexPropertyMap, key2);
                    stringBuilder.append(newLine(prefixSpace)).append("private ").append(key).append(" ").append(key2).append(";");
                    appendGetterSetter(methodBuilder, prefixSpace, key, key2);
                    innerBuilder.append(StringUtils.LINE).append(serialize(value, key, prefixSpace + prefixSpace, complexPropertyMap));
                }
            } else {
                stringProperty(stringBuilder, methodBuilder, prefixSpace, e, value, complexPropertyMap);
            }
        });
        if (StringUtils.isCreateMultiBean) ArrayUtils.multiBeans.add(new HashMap<String, String>() {{
            put("className", className);
            put("classBody", stringBuilder.append(methodBuilder).append(newLine(prefixSpace.substring(4))).append("}").toString());
        }});
        stringBuilder.append(innerBuilder).append(methodBuilder).append(newLine(prefixSpace.substring(4))).append("}");
        return stringBuilder.toString();
    }

    /***
     * 自定义的成员变量名称
     * @param complexPropertyMap
     * @param key
     * @return
     */
    private static String getComplexPropertyName(Map<String, String> complexPropertyMap, String key) {
        String fieldType;
        if (ValueWidget.isNullOrEmpty(complexPropertyMap)) {
            fieldType = key;
        } else if (complexPropertyMap.containsKey(key)) {
            fieldType = complexPropertyMap.get(key);
            if (ValueWidget.isNullOrEmpty(fieldType)) {
                fieldType = key;
            }
        } else {
            fieldType = key;
        }
        //防止出现List<Items>items,应该是List<Item>items
        fieldType = fieldType.replaceAll("s$", "");//Items-->Item
        return fieldType;
    }

    /***
     * 简单的string 类型
     * @param stringBuilder
     * @param methodBuilder
     * @param prefixSpace
     * @param e
     * @param value
     */
    private static void stringProperty(StringBuilder stringBuilder, StringBuilder methodBuilder, String prefixSpace, Map.Entry<?, ?> e, Object value, Map<String, String> complexPropertyMap) {
        String propertyType = null;
        if (null == value) {
            propertyType = "String";
        } else {
            propertyType = value.getClass().getSimpleName();
        }
        String key = ((String) e.getKey()).replaceAll("[\\s]+", "");
        key = getColumnName(complexPropertyMap, key);
        stringBuilder.append(newLine(prefixSpace)).append("private ").append(propertyType).append(" ").append(key).append(";");
        appendGetterSetter(methodBuilder, prefixSpace, propertyType, key);
    }

    private static String getColumnName(Map<String, String> complexPropertyMap, String key) {
        if (!ValueWidget.isNullOrEmpty(complexPropertyMap)) {
            String newKey = complexPropertyMap.get(key);
            if (!ValueWidget.isNullOrEmpty(newKey)) {
                key = newKey;
            }
        }
        return key;
    }

    /**
     * 追加getter setter
     *
     * @param sourceBuilder sourceStr 源字符串
     * @param prefixSpace   前置空间
     * @param fieldType     字段类型
     * @param fieldName     字段名称
     */
    private static void appendGetterSetter(StringBuilder sourceBuilder, String prefixSpace, String fieldType, String fieldName) {
        sourceBuilder
                .append(StringUtils.LINE)
                .append(newLine(prefixSpace))
                .append(MapUtils.CODE_TEMPLATE_MAP.get("getter").replace("${field_name}", fieldName).replace("\r\n", "\r\n" + prefixSpace))
                .append((StringUtils.isEmpty(MapUtils.CODE_TEMPLATE_MAP.get("getter")) ? "" : newLine(prefixSpace)))
                .append("public ").append(fieldType).append(" get").append(StringUtils.upperCaseFirstChar(fieldName)).append("() {").append(newLine(prefixSpace + StringUtils.PREFIX_SPACE)).append("return this.").append(fieldName).append(";").append(newLine(prefixSpace)).append("}")
                .append(StringUtils.LINE)
                .append(newLine(prefixSpace))
                .append(MapUtils.CODE_TEMPLATE_MAP.get("setter").replace("${field_name}", fieldName).replace("\r\n", "\r\n" + prefixSpace))
                .append((StringUtils.isEmpty(MapUtils.CODE_TEMPLATE_MAP.get("setter")) ? "" : newLine(prefixSpace)))
                .append("public void set").append(StringUtils.upperCaseFirstChar(fieldName)).append("(").append(fieldType).append(" ").append(fieldName).append(") {").append(newLine(prefixSpace + StringUtils.PREFIX_SPACE)).append("this.").append(fieldName).append(" = ").append(fieldName).append(";").append(newLine(prefixSpace)).append("}");
    }

    /**
     * 新行 前置空间
     *
     * @param prefixSpace prefixSpace
     * @return newLineStr
     */
    private static String newLine(String prefixSpace) {
        return StringUtils.LINE + prefixSpace;
    }


    /**
     * 序列化json对象
     *
     * @param jsonObj jsonObj
     * @return json content内容
     */
    static String serialize(Object jsonObj) {
        validate(jsonObj);
        StringBuilder stringBuilder = new StringBuilder("{");
        ((LinkedHashMap<?, ?>) jsonObj).entrySet().stream().forEach(e -> {
            String key = StringUtils.upperCaseFirstChar((String) e.getKey());
            Object value = e.getValue();
            //list集合
            if (value instanceof ArrayList) {
                //StringBuilder listBuilder=new StringBuilder();
                String str = (String) ((ArrayList) value).stream().reduce((v1, v2) -> serialize(v1).concat(",").concat(serialize(v2))).get();
                stringBuilder.append(key).append(":").append("[").append(str).append("]");
            } else if (value instanceof LinkedHashMap) {
                stringBuilder.append(key).append(":").append(serialize(value)).append(",");
            } else {
                appendEntry(stringBuilder, e);
            }
        });
        return stringBuilder.toString().endsWith(",") ?
                stringBuilder.substring(0, stringBuilder.toString().length() - 1).concat("}")
                : stringBuilder.toString().concat("}");
    }

    private static void appendEntry(StringBuilder stringBuilder, Map.Entry<?, ?> entry) {
        stringBuilder.append("\"")
                .append(entry.getKey()).append(":")
                .append("\"")
                .append(entry.getValue() instanceof String ? "\"" : "")
                .append(entry.getValue())
                .append(entry.getValue() instanceof String ? "\"" : "")
                .append(",");
    }
}
