package com.zysl.aws.utils;

import java.util.*;

public class ListTest {
    public static void main(String[] args) {
        Date date = new Date("247003-12-01 19:41:51.0");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        System.out.println(cal.getTime());
//        DateUtil.addDateHour()
    }

    public void test1(){
        List<String> list = new ArrayList<>();
        list.add("1");list.add("2");list.add("3");list.add("4");list.add("5");list.add("6");
        list.add("7");list.add("8");list.add("9");list.add("10");list.add("11");list.add("12");
        list.add("13");list.add("14");list.add("15");list.add("16");list.add("17");
        list.add("18");list.add("19");list.add("20");list.add("21");

        Map<Integer,List<String>> map =  new BatchListUtil().batchList(list, 9);
        System.out.println(map);
        System.out.println(map.size());
    }
}
