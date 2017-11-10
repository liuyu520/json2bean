package com.javaear.json4bean.core;

import com.common.bean.Json2BeanClassInfo;
import com.common.util.SystemHWUtil;
import com.javaear.json4bean.bean.ClassSourcePathBean;
import com.javaear.json4bean.util.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author aooer
 */
public class JsonRender {

    /*************** reander write bean ****************/

    /**
     * 渲染javaBean
     *
     * @param jsonStr   json字符串
     * @param className 类名
     */
    public Json2BeanClassInfo reander(String jsonStr, String className, String packageName, String destDir, Map<String, String> complexPropertyMap, boolean parseInteger) throws IOException {
        ArrayUtils.multiBeans.clear();//清空java文件列表
        String classBody = JsonSerializer.serialize(new JsonObject(jsonStr, parseInteger).getJsonObj(), className, StringUtils.PREFIX_SPACE, complexPropertyMap);
        List<String> classList = new ArrayList<String>();
        ClassSourcePathBean classSourcePathBean = null;
        Json2BeanClassInfo json2BeanClassInfo = new Json2BeanClassInfo();
        if (StringUtils.isCreateMultiBean)
            for (Map<String, String> multiBean : ArrayUtils.multiBeans) {
                classSourcePathBean = reanderBody(multiBean.get("classBody"), multiBean.get("className"), packageName, destDir);
                if (null != classSourcePathBean) {
                    classList.add(classSourcePathBean.getClassFullPath());
                    json2BeanClassInfo.addClassBody(classSourcePathBean.getClassSourceCode());
                }
            }
        else {
            classSourcePathBean = reanderBody(classBody, className, packageName, destDir);
            if (null != classSourcePathBean) {
                classList.add(classSourcePathBean.getClassFullPath());
                json2BeanClassInfo.addClassBody(classSourcePathBean.getClassSourceCode());
            }
        }


        json2BeanClassInfo.setClassNameList(classList);
        return json2BeanClassInfo;
    }

    /**
     * 渲染javaBean <br />
     * 生成的类实现接口import java.io.Serializable;
     *
     * @param classBody jsonObj
     * @param className 类名
     */
    ClassSourcePathBean reanderBody(String classBody, String className, String packageName, String destDir) throws IOException {
        final String javaBeanContent = StringUtils.LINE + (StringUtils.isEmpty(MapUtils.CODE_TEMPLATE_MAP.get("class")) ? "" : StringUtils.LINE) + MapUtils.CODE_TEMPLATE_MAP.get("class") +
                classBody;
        String classFullPath = destDir + File.separatorChar + className + ".java";
        String sourceCode = MapUtils.CODE_TEMPLATE_MAP.get("copyright") + (StringUtils.isEmpty(MapUtils.CODE_TEMPLATE_MAP.get("copyright")) ? "" : StringUtils.LINE) +
                (StringUtils.isEmpty(packageName) ? "" : "package " + packageName + ";" + StringUtils.LINE + StringUtils.LINE) +
                (javaBeanContent.contains("private List<") ? "import java.util.List;" + SystemHWUtil.CRLF + "import java.io.Serializable;" + javaBeanContent : javaBeanContent).trim();
        IOUtils.write(sourceCode, classFullPath);
        ClassSourcePathBean classSourcePathBean = new ClassSourcePathBean();
        classSourcePathBean.setClassFullPath(classFullPath);
        classSourcePathBean.setClassSourceCode(sourceCode);
        return classSourcePathBean;
    }

    /*************** parse reverse ****************/

    /**
     * 解析bean为json字符串
     *
     * @param object bean
     * @return jsonString
     */
    public String toJsonString(Object object) {
        return JsonSerializer.serialize(BeanUtils.bean2Map(object));
    }

    /**
     * 解析json字符串为javaBean对象
     *
     * @param jsonStr     json字符串
     * @param targetClass 类名
     */
    public <T> T parseObject(String jsonStr, Class<T> targetClass, boolean parseInteger) {
        return BeanUtils.map2Bean(new JsonObject(jsonStr, parseInteger).getJsonObj(), targetClass);
    }


    /***************
     * singleton
     ****************/

    /* 延迟加载内部类 */
    private static class JsonRenderHolder {
        private static JsonRender INSTANCE = new JsonRender();
    }

    /* 单例，实例获取方法 */
    public static JsonRender getInstance() {
        return JsonRenderHolder.INSTANCE;
    }

    private JsonRender() {
    }
}
