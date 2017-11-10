package com.javaear.json4bean.util;

import java.io.*;

/**
 * @author aooer
 */
public abstract class IOUtils {

    /**
     * 写字符串到文件
     *
     * @param sourceStr 原字符串
     * @param destFile  目标
     * @throws IOException
     */
    public static void write(String sourceStr, String destFile) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(destFile)));
        writer.write(sourceStr);
        writer.flush();
        writer.close();
    }

    /**
     * 获取文件的字符串内容
     *
     * @param sourceFile 目标
     * @throws IOException
     */
    public static String getContent(String sourceFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(sourceFile)));
        String temp;
        StringBuilder contentBuilder = new StringBuilder(temp = reader.readLine());
        while ((temp = reader.readLine()) != null)
            contentBuilder.append(temp).append("\r\n");
        reader.close();
        return contentBuilder.toString();
    }
}
