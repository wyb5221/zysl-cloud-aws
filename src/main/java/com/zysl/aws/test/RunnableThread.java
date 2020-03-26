package com.zysl.aws.test;

import java.util.Random;

public class RunnableThread implements Runnable {
    @Override
    public void run() {
        int random = new Random().nextInt(100); // 获取100以内的随机整数
        // 以下打印随机数日志，包括当前时间、当前线程、随机数值等信息
        System.out.println(Thread.currentThread().getName()+"任务生成的随机数="+random);
    }
}
