package com.zysl.aws.test;

import java.util.regex.Pattern;

public class test {

    public static void main(String[] args) {
        String pattern = "^[a-zA-Z0-9.\\-_]{3,60}$";
        String bucketName = "test-w1";
        boolean flag = Pattern.compile(pattern).matcher(bucketName).matches();

        System.out.println("flag:"+flag);
    }

}
