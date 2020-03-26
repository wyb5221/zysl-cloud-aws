package com.zysl.aws.test;

import com.zysl.aws.model.FileInfo;
import com.zysl.aws.service.AmasonService;
import com.zysl.aws.service.impl.AmasonServiceImpl;

public class Test1 {
    static int x = 10;  //10
    static {x += 5;}  //15

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
//        AmasonService service = new AmasonServiceImpl();
//        Class<? extends AmasonService> aClass = service.getClass();
//        System.out.println(aClass);
//        System.out.println(aClass.getSuperclass());

        Class<?> aClass = Class.forName("com.zysl.aws.model.FileInfo");
        System.out.println(aClass.getSimpleName());
        System.out.println(aClass.getDeclaredFields());

        FileInfo fileInfo = (FileInfo)aClass.newInstance();
        fileInfo.setKey("123");
        System.out.println(fileInfo.getKey());

        System.out.println(aClass.getDeclaredField("key"));

    }
    static {x /= 3;}  //5
}
