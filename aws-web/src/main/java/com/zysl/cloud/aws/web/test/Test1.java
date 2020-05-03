package com.zysl.cloud.aws.web.test;

public class Test1 {
    public static void main(String[] args) throws Exception {
//        String str = "abcd";
//        char[] chars = str.toCharArray();
//        test2();
        System.out.println("--:"+String.join("ac", "dd"));
    }

    public static void test2(){
        String str = "***A**B**C**";
        char[] chars = str.toCharArray();
        int len = chars.length -1;
        System.out.println("len:"+len);
        for (int i = chars.length-1; i >= 0; i--) {
            System.out.println(str.charAt(i));
            char a = str.charAt(i);
            if(a != '*'){
                chars[len] = str.charAt(i);
                chars[i] = '*';
                len--;
            }
        }
        System.out.println(chars);
//        for(int i=0;i<=len;i++){
//            chars[i]='*';
//        }
//        System.out.println(chars);
    }

    public static void test1(){
        String str = "***A**B**C**";
        int len = str.length()-1;
        char[] chars = new char[str.length()];
        for (int i = str.length() - 1; i >= 0; i--) {
            System.out.println(str.charAt(i));
            char a = str.charAt(i);
            if(a != '*'){
                chars[len] = str.charAt(i);
                len--;
            }
        }
        System.out.println(chars);
        for(int i=0;i<=len;i++){
            chars[i]='*';
        }
        System.out.println(chars);
    }

}
