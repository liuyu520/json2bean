package com.javaear.json4bean;

import java.io.File;
import java.util.Map;

public class Json4BeanTest {

    /* 测试json 数据 */
    static String data = "[{\n" +
            "  \"id\": 123,\n" +
            "  \"name\": \"张三\",\n" +
            "  \"firend\": {\n" +
            "    \"fid\": \"f123\",\n" +
            "    \"fname\": \"李四\"\n" +
            "  },\n" +
            "  \"subjects\": [\n" +
            "    {\n" +
            "      \"sid\": \"o123\",\n" +
            "      \"sname\": \"王五\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"sid\": \"o124\",\n" +
            "      \"sname\": \"马六\"\n" +
            "    }\n" +
            "  ]\n" +
            "}]";

    public static void main(String[] args) {
        //JSON.setWriteMultiBean(true); //去掉此注释，生成javabean为多个对象，非内部类形式
        //默认生成内部类JavaBean，可以通过去掉上边一行注释选择非内部类形式
        writeBeanSimpleTest();
        writeBeanWithPackageNameTest();
        writeBeanWithPackageNameAndCodeTemplateTest();
//    	System.out.println(data);
//        parseObjectTest();
//        toJsonStringTest();
    }

    /**
     * 极简生成测试
     */
    public static void writeBeanSimpleTest() {
        JSON.writeBean(data, "Student", (Map<String, String>) null, false);
    }

    /**
     * 附带pageckName生成测试
     */
    public static void writeBeanWithPackageNameTest() {
        JSON.writeBean(data, "Student", "com.javaear.test", (Map<String, String>) null, false);
    }

    /**
     * 附带pageckName、注释模板、生成测试
     */
    public static void writeBeanWithPackageNameAndCodeTemplateTest() {
        //设置代码注释模板地址
        JSON.setCodeTemplate(System.getProperty("user.dir") + File.separator + "json4bean/src/test/resources/code-template.txt");
        JSON.writeBean(data, "Student", "com.javaear.test", System.getProperty("user.dir") + File.separator + "json4bean/src/test/java/" + "com/javaear/test", (Map<String, String>) null, false);
    }

    /**
     /*  * 解析json字符串为bean测试
     *//*
    public static void parseObjectTest(){
        Student student = JSON.parseObject(data, Student.class);
        System.out.println(
                "id为："+student.getId()+
                "name为："+student.getName()+
                "firend name为："+student.getFirend().getFname()+
                "subject 2 sname为："+student.getSubjects().get(1).getSname());
    }

    *//**
     * 解析bean为Json测试
     *//*
    public static void toJsonStringTest(){
        //调用parse方法赋值的对象
        Student student = JSON.parseObject(data, Student.class);
        //解析bean为字符串
        String str = JSON.toJsonString(student);
        System.out.println(str);
    }
*/

}
