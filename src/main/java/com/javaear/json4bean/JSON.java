package com.javaear.json4bean;

import com.common.bean.Json2BeanClassInfo;
import com.common.util.SystemHWUtil;
import com.javaear.json4bean.core.JsonRender;
import com.javaear.json4bean.exception.Json4BeanIOException;
import com.javaear.json4bean.util.IOUtils;
import com.javaear.json4bean.util.MapUtils;
import com.javaear.json4bean.util.StringUtils;
import com.string.widget.util.ValueWidget;
import com.time.util.TimeHWUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author aooer
 */
public abstract class JSON {

    /**
     * 默认生成文件目录
     */
    private static final String DEFAULT_DEST_DIR = System.getProperty("user.dir");

    /**
     * 根据json字符串
     * 写javaBean文件
     *
     * @param jsonStr   json字符串
     * @param className 类名
     */
    public static void writeBean(String jsonStr, String className, Map<String, String> complexPropertyMap, boolean parseInteger) {
        writeBean(jsonStr, className, null, complexPropertyMap, parseInteger);
    }

    /**
     * 根据json字符串
     * 写javaBean文件
     *
     * @param jsonStr   json字符串
     * @param className 类名
     */
    public static void writeBean(String jsonStr, String className, String packageName, Map<String, String> complexPropertyMap, boolean parseInteger) {
        writeBean(jsonStr, className, packageName, DEFAULT_DEST_DIR, complexPropertyMap, parseInteger);
    }

    /**
     * 根据json字符串
     * 写javaBean文件
     *
     * @param jsonStr   json字符串
     * @param className 类名
     * @param destDir   目标文件路径
     */
    public static Json2BeanClassInfo writeBean(String jsonStr, String className, String packageName, String destDir, Map<String, String> complexPropertyMap, boolean parseInteger) {
        try {
            if ((StringUtils.isEmpty(destDir) || destDir.equals(DEFAULT_DEST_DIR)) && !StringUtils.isEmpty(packageName))
                destDir += File.separator + packageName.replace(".", File.separator);
            if (!new File(destDir).exists()) new File(destDir).mkdirs();
            Json2BeanClassInfo json2BeanClassInfo = JsonRender.getInstance().reander(jsonStr, className, packageName, destDir, complexPropertyMap, parseInteger);
            return json2BeanClassInfo;
        } catch (IOException e) {
            throw new Json4BeanIOException(e);
        }
    }

    /**
     * 解析bean为json字符串
     *
     * @param object bean
     * @return jsonString
     */
    public static String toJsonString(Object object) {
        return JsonRender.getInstance().toJsonString(object);
    }

    /**
     * 解析json字符串为javaBean对象
     *
     * @param jsonStr     json字符串
     * @param targetClass 类名
     */
    public static <T> T parseObject(String jsonStr, Class<T> targetClass, boolean parseInteger) {
        return JsonRender.getInstance().parseObject(jsonStr, targetClass, parseInteger);
    }

    /**
     * 设置自定义代码注释模板，默认模板示例test-classpath:resources/code-template
     *
     * @param codeTemplatePath 自定义注释模板路径
     */
    public static void setCodeTemplate(String codeTemplatePath) {
        try {
            String codeTemplateStr = IOUtils.getContent(codeTemplatePath);
            if (!ValueWidget.isNullOrEmpty(codeTemplateStr)) {
                codeTemplateStr = codeTemplateStr.replaceAll("([\\s]*\\*/[\\s]*#class)", "  <br>" + SystemHWUtil.CRLF + " * " + TimeHWUtil.getCurrentDateTime() + "$1");
            }
            MapUtils.CODE_TEMPLATE_MAP.put("copyright", codeTemplateStr.split("#copyright")[1].trim());
            MapUtils.CODE_TEMPLATE_MAP.put("class", codeTemplateStr.split("#class")[1].trim());
            MapUtils.CODE_TEMPLATE_MAP.put("getter", codeTemplateStr.split("#getter")[1].trim());
            MapUtils.CODE_TEMPLATE_MAP.put("setter", codeTemplateStr.split("#setter")[1].trim());
        } catch (IOException e) {
            System.out.println(codeTemplatePath + " does not exist.");
            throw new Json4BeanIOException(e);
        }
    }

    /**
     * 设置是否生成多个javaBean
     *
     * @param isCreateMultiBean 对于复杂的嵌套式jsonStr，是否生成多个javaBean文件(true)或只是生成内部类形式(false)
     */
    public static void setWriteMultiBean(Boolean isCreateMultiBean) {
        StringUtils.isCreateMultiBean = isCreateMultiBean;
    }
}
