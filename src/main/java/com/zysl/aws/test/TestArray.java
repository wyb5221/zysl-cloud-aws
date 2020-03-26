package com.zysl.aws.test;

public class TestArray {

    public static void main(String[] args) {
        String[] arr1 = {"a","b","c","d","e"};
        String[] arr2 = {"b","d"};
        String[] arr3 = new String[arr1.length - arr2.length];
        int cout = 0;
        for (int i = 0; i < arr1.length; i++) {
            boolean flag = true;
            for (int j = 0; j < arr2.length; j++) {
                if(arr1[i].equals(arr2[j])){
                    flag = false;
                }
            }
            if(flag){
                arr3[cout] = arr1[i];
                cout++;
            }
        }

        for (int i = 0; i < arr3.length; i++) {
            System.out.println(arr3[i]);
        }
    }
}
